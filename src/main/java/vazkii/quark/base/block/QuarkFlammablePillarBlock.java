package vazkii.quark.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
	import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import vazkii.quark.base.module.QuarkModule;

public class QuarkFlammablePillarBlock extends QuarkPillarBlock {

	public QuarkFlammablePillarBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, int flammability, Properties properties) {
		super(regname, module, creativeTab, properties);
		BlockContentRegistries.FLAMMABLE_BLOCK.put(this, new FlammableBlockEntry(5, flammability)); //todo: This is probably not 1:1 with Forge, we should probably fix that.

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
