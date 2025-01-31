package vazkii.quark.content.building.module;

import io.github.fabricators_of_create.porting_lib.event.common.PlayerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.api.ICrawlSpaceBlock;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.HollowLogBlock;

@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true)
public class HollowLogsModule extends QuarkModule {

	private static final String TAG_TRYING_TO_CRAWL = "quark:trying_crawl";

	public static QuarkGenericTrigger crawlTrigger;

	@Config(flag = "hollow_log_auto_crawl")
	public static boolean enableAutoCrawl = true;

	@Hint(key = "hollow_logs", value = "hollow_log_auto_crawl")
	TagKey<Block> hollowLogsTag;

	public HollowLogsModule() {
		super();
		PlayerTickEvents.START.register(this::playerTick);
	}

	@Override
	public void register() {
		for(Wood wood : VanillaWoods.ALL) {
			new HollowLogBlock(wood.log(), this, !wood.nether());
//			new HollowWoodBlock(wood.wood(), this, !wood.nether());
		}

		crawlTrigger = QuarkAdvancementHandler.registerGenericTrigger("hollow_log_crawl");
	}

	@Override
	public void setup() {
		hollowLogsTag = TagKey.create(Registry.BLOCK_REGISTRY, new  ResourceLocation(Quark.MOD_ID, "hollow_logs"));
	}

	public void playerTick(Player player) {
		if(enableAutoCrawl) {
			BlockPos playerPos = player.blockPosition();
			boolean isTrying = player.isVisuallyCrawling() ||
				(player.isCrouching() && !player.isColliding(playerPos, player.level.getBlockState(playerPos)));
			boolean wasTrying = player.getPersistentData().getBoolean(TAG_TRYING_TO_CRAWL);

			if (!player.isVisuallyCrawling()) {
				if (isTrying && !wasTrying) {
					Direction dir = player.getDirection();
					Direction opp = dir.getOpposite();
					if (dir.getAxis() != Axis.Y) {
						BlockPos pos = playerPos.relative(dir);

						if (!tryClimb(player, opp, playerPos)) // Crawl out
							if (!tryClimb(player, opp, playerPos.above())) // Crawl out
								if (!tryClimb(player, dir, pos)) // Crawl into
									tryClimb(player, dir, pos.above()); // Crawl into
					}
				}
			}

			if(isTrying != wasTrying)
				player.getPersistentData().putBoolean(TAG_TRYING_TO_CRAWL, isTrying);
		}
	}

	private boolean tryClimb(Player player, Direction dir, BlockPos pos) {
		BlockState state = player.level.getBlockState(pos);
		Block block = state.getBlock();

		if(block instanceof ICrawlSpaceBlock crawlSpace) {
			if(crawlSpace.canCrawl(player.level, state, pos, dir)) {
				player.setPose(Pose.SWIMMING);
				player.setSwimming(true);

				double x = pos.getX() + 0.5 - (dir.getStepX() * 0.3);
				double y = pos.getY() + crawlSpace.crawlHeight(player.level, state, pos, dir);
				double z = pos.getZ() + 0.5 - (dir.getStepZ() * 0.3);

				player.setPos(x, y, z);

				if(player instanceof ServerPlayer sp && crawlSpace.isLog(sp, state, pos, dir))
					crawlTrigger.trigger(sp);

				return true;
			}
		}

		return false;
	}
}
