package vazkii.quark.content.tools.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tools.block.be.CloudBlockEntity;
import vazkii.quark.content.tools.module.BottledCloudModule;

import org.jetbrains.annotations.NotNull;

public class CloudBlock extends QuarkBlock implements EntityBlock {

	public CloudBlock(QuarkModule module) {
		super("cloud", module, null,
				Block.Properties.of(Material.CLAY)
				.sound(SoundType.WOOL)
				.strength(0)
				.noOcclusion()
				.noCollission());
	}

	@NotNull
	@Override
	public PushReaction getPistonPushReaction(@NotNull BlockState state) {
		return PushReaction.BLOCK;
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult raytrace) {
		ItemStack stack = player.getItemInHand(hand);

		if(stack.getItem() == Items.GLASS_BOTTLE) {
			fillBottle(player, player.getInventory().selected);
			world.removeBlock(pos, false);
			return InteractionResult.SUCCESS;
		}

		if(stack.getItem() instanceof BlockItem bitem) {
			Block block = bitem.getBlock();

			UseOnContext context = new UseOnContext(player, hand, new BlockHitResult(new Vec3(0.5F, 1F, 0.5F), raytrace.getDirection(), pos, false));
			BlockPlaceContext bcontext = new BlockPlaceContext(context);

			BlockState stateToPlace = block.getStateForPlacement(bcontext);
			if(stateToPlace != null && stateToPlace.canSurvive(world, pos)) {
				world.setBlockAndUpdate(pos, stateToPlace);
				world.playSound(player, pos, stateToPlace.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1F, 1F);

				if(!player.getAbilities().instabuild) {
					stack.shrink(1);
					fillBottle(player, 0);
				}

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return new ItemStack(BottledCloudModule.bottled_cloud);
	}

	private void fillBottle(Player player, int startIndex) {
		Inventory inv = player.getInventory();
		for(int i = startIndex ; i < inv.getContainerSize(); i++) {
			ItemStack stackInSlot = inv.getItem(i);
			if(stackInSlot.getItem() == Items.GLASS_BOTTLE) {
				stackInSlot.shrink(1);

				ItemStack give = new ItemStack(BottledCloudModule.bottled_cloud);
				if(!player.addItem(give))
					player.drop(give, false);
				return;
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new CloudBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return createTickerHelper(type, BottledCloudModule.blockEntityType, CloudBlockEntity::tick);
	}

}
