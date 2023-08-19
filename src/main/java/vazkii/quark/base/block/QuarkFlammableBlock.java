package vazkii.quark.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.module.QuarkModule;

public class QuarkFlammableBlock extends QuarkBlock {

	private final int flammability;

	public QuarkFlammableBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, int flamability, Properties properties) {
		super(regname, module, creativeTab, properties);
		this.flammability = flamability;
	}

	// TODO: AAAAAAAAAAAAA - Maximum
	// @Override
	// public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
	// 	return true;
	// }

	// @Override
	// public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
	// 	return flammability;
	// }

}
