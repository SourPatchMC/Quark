package vazkii.quark.content.building.block;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.base.block.QuarkVineBlock;
import vazkii.quark.base.module.QuarkModule;

public class CutVineBlock extends QuarkVineBlock implements IBlockColorProvider {

	public CutVineBlock(QuarkModule module) {
		super(module, "cut_vine", false);
	}

	@Override
	public boolean canSupportAtFace(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction dir) {
		if(dir != Direction.UP) {
			BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(dir);
			BlockState blockstate = level.getBlockState(pos.above());
			return blockstate.is(Blocks.VINE) && blockstate.getValue(booleanproperty);
		}

		return super.canSupportAtFace(level, pos, dir);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return new ItemStack(Items.VINE);
	}

	@Override
	@ClientOnly
	public BlockColor getBlockColor() {
		final BlockState grass = Blocks.VINE.defaultBlockState();
		return (state, world, pos, tintIndex) -> Minecraft.getInstance().getBlockColors().getColor(grass, world, pos, tintIndex);
	}

	@Override
	@ClientOnly
	public ItemColor getItemColor() {
		final ItemStack grass = new ItemStack(Items.VINE);
		return (stack, tintIndex) -> Minecraft.getInstance().itemColors.getColor(grass, tintIndex);
	}

}
