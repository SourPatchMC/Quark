package vazkii.quark.content.building.module;

import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.ToolInteractionHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.WoodPostBlock;

@LoadModule(category = ModuleCategory.BUILDING)
public class WoodenPostsModule extends QuarkModule {

	@Hint TagKey<Item> postsTag;
	
	@Override
	public void register() {
		for(Wood wood : VanillaWoods.ALL) {
			Block b = wood.fence();
			
			boolean nether = b.defaultBlockState().getMaterial() == Material.NETHER_WOOD;
			WoodPostBlock post = new WoodPostBlock(this, b, "", nether);
			WoodPostBlock stripped = new WoodPostBlock(this, b, "stripped_", nether);
			ToolInteractionHandler.registerInteraction(ToolActions.AXE_STRIP, post, stripped);
		}
	}
	
	@Override
	public void setup() {
		postsTag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Quark.MOD_ID, "posts"));
	}

	public static boolean canLanternConnect(BlockState state, LevelReader worldIn, BlockPos pos, boolean prev) {
		return prev ||
				(ModuleLoader.INSTANCE.isModuleEnabled(WoodenPostsModule.class)
						&& state.getValue(LanternBlock.HANGING)
						&& worldIn.getBlockState(pos.above()).getBlock() instanceof WoodPostBlock);
	}

}
