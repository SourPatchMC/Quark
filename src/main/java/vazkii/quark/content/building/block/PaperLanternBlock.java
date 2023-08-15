package vazkii.quark.content.building.block;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class PaperLanternBlock extends QuarkBlock {

	private static final VoxelShape POST_SHAPE = box(6, 0, 6, 10, 16, 10);
	private static final VoxelShape LANTERN_SHAPE = box(2, 2, 2, 14, 14, 14);
	private static final VoxelShape SHAPE = Shapes.or(POST_SHAPE, LANTERN_SHAPE);

	public PaperLanternBlock(String regname, QuarkModule module) {
		super(regname, module, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.WOOD, MaterialColor.SNOW)
						.sound(SoundType.WOOD)
						.lightLevel(b -> 15)
						.strength(1.5F));
	}

	@NotNull
	@Override
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 30;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}
}
