package vazkii.quark.content.automation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.common.util.ForgeSoundType;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.block.be.FeedingTroughBlockEntity;
import vazkii.quark.content.automation.module.FeedingTroughModule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author WireSegal
 * Created at 9:39 AM on 9/20/19.
 */
public class FeedingTroughBlock extends QuarkBlock implements EntityBlock {

	private static final SoundType WOOD_WITH_PLANT_STEP = new ForgeSoundType(1.0F, 1.0F, () -> SoundEvents.WOOD_BREAK, () -> SoundEvents.GRASS_STEP, () -> SoundEvents.WOOD_PLACE, () -> SoundEvents.WOOD_HIT, () -> SoundEvents.WOOD_FALL);

	public static BooleanProperty FULL = BooleanProperty.create("full");

	public static final VoxelShape CUBOID_SHAPE = box(0, 0, 0, 16, 8, 16);
	public static final VoxelShape EMPTY_SHAPE = Shapes.join(CUBOID_SHAPE, box(2, 2, 2, 14, 8, 14), BooleanOp.ONLY_FIRST);
	public static final VoxelShape FULL_SHAPE = Shapes.join(CUBOID_SHAPE, box(2, 6, 2, 14, 8, 14), BooleanOp.ONLY_FIRST);
	public static final VoxelShape ANIMAL_SHAPE = box(0, 0, 0, 16, 24, 16);

	public FeedingTroughBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
		registerDefaultState(defaultBlockState().setValue(FULL, false));
	}

	@NotNull
	@Override
	public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		Entity entity = context instanceof EntityCollisionContext ecc ? ecc.getEntity() : null;
		if(entity instanceof Animal)
			return ANIMAL_SHAPE;

		return EMPTY_SHAPE;
	}

	@NotNull
	@Override
	public VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
		return CUBOID_SHAPE;
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return state.getValue(FULL) ? FULL_SHAPE : EMPTY_SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FULL);
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
		if (state.getValue(FULL))
			return WOOD_WITH_PLANT_STEP;
		return super.getSoundType(state, world, pos, entity);
	}

	@Override
	public void fallOn(Level world, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull Entity entity, float distance) {
		if (world.getBlockState(pos).getValue(FULL))
			entity.causeFallDamage(distance, 0.2F, DamageSource.FALL);
		else
			super.fallOn(world, state, pos, entity, distance);
	}

	@Override
	public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile instanceof FeedingTroughBlockEntity) {
				Containers.dropContents(world, pos, (FeedingTroughBlockEntity)tile);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(@NotNull BlockState state, Level world, @NotNull BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}


	@NotNull
	@Override
	public InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult trace) {
		if (world.isClientSide)
			return InteractionResult.SUCCESS;
		else {
			MenuProvider container = this.getMenuProvider(state, world, pos);
			if (container != null)
				player.openMenu(container);

			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public boolean triggerEvent(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, int id, int param) {
		super.triggerEvent(state, world, pos, id, param);
		BlockEntity tile = world.getBlockEntity(pos);
		return tile != null && tile.triggerEvent(id, param);
	}

	@Override
	@Nullable
	public MenuProvider getMenuProvider(@NotNull BlockState state, Level world, @NotNull BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		return tile instanceof MenuProvider ? (MenuProvider)tile : null;
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new FeedingTroughBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return createTickerHelper(type, FeedingTroughModule.blockEntityType, FeedingTroughBlockEntity::tick);
	}

}
