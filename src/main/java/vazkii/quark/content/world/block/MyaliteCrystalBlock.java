package vazkii.quark.content.world.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.quark.base.module.QuarkModule;

public class MyaliteCrystalBlock extends QuarkGlassBlock implements IMyaliteColorProvider {

	public MyaliteCrystalBlock(QuarkModule module) {
		super("myalite_crystal", module, CreativeModeTab.TAB_DECORATIONS, true,
				Block.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
				.strength(0.5F, 1200F)
				.sound(SoundType.GLASS)
				.lightLevel(b -> 14)
				.requiresCorrectToolForDrops()
				.randomTicks()
				.noOcclusion());
	}
	
	private static float[] decompColor(int color) {
		int r = (color & 0xFF0000) >> 16;
		int g = (color & 0xFF00) >> 8;
		int b = color & 0xFF;
		return new float[] { (float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F };
	}
	
	// TODO: Refer to QuarkInheritedPaneBlock.java's todo - Maximum
	// @Nullable
	// @Override
	// public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
	// 	return decompColor(IMyaliteColorProvider.getColor(pos, myaliteS(), myaliteB()));
	// }
	
}
