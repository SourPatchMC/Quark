package vazkii.quark.addons.oddities.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.arl.interf.IBlockItemProvider;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.addons.oddities.block.be.TinyPotatoBlockEntity;
import vazkii.quark.addons.oddities.item.TinyPotatoBlockItem;
import vazkii.quark.addons.oddities.module.TinyPotatoModule;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

/**
 * @author WireSegal
 * Created at 10:16 AM on 3/14/22.
 */
public class TinyPotatoBlock extends QuarkBlock implements SimpleWaterloggedBlock, EntityBlock, IBlockItemProvider {

	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final VoxelShape SHAPE = box(6, 0, 6, 10, 6, 10);

	public static final String ANGRY = "angery";

	public static boolean isAngry(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, ANGRY, false);
	}

	public TinyPotatoBlock(QuarkModule module) {
		super("tiny_potato", module, CreativeModeTab.TAB_DECORATIONS,
				BlockBehaviour.Properties.of(Material.WOOL).strength(0.25F));
		registerDefaultState(defaultBlockState()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
	}

	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HORIZONTAL_FACING, WATERLOGGED);
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof TinyPotatoBlockEntity inventory) {
				Containers.dropContents(world, pos, inventory);
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TinyPotatoBlockEntity tater) {
			if (tater.hasCustomName())
				stack.setHoverName(tater.getCustomName());

			if (tater.angry)
				ItemNBTHelper.setBoolean(stack, ANGRY, true);
		}
		return stack;
	}

	@NotNull
	@Override
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
		return SHAPE;
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TinyPotatoBlockEntity tater) {
			tater.interact(player, hand, player.getItemInHand(hand), hit.getDirection());
			
			if(player instanceof ServerPlayer sp)
				TinyPotatoModule.patPotatoTrigger.trigger(sp);
			
			if (!world.isClientSide) {
				AABB box = SHAPE.bounds();
				((ServerLevel) world).sendParticles(ParticleTypes.HEART, pos.getX() + box.minX + Math.random() * (box.maxX - box.minX), pos.getY() + box.maxY, pos.getZ() + box.minZ + Math.random() * (box.maxZ - box.minZ), 1, 0, 0, 0, 0);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
		return defaultBlockState()
				.setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@NotNull
	@Override
	public BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, facing, facingState, level, pos, facingPos);
	}

	@Override
	public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity living, ItemStack stack) {
		boolean hasCustomName = stack.hasCustomHoverName();
		boolean isAngry = isAngry(stack);
		if (hasCustomName || isAngry) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof TinyPotatoBlockEntity tater) {
				if (hasCustomName)
					tater.name = stack.getHoverName();
				tater.angry = isAngry(stack);
			}
		}
	}

	@Override
	public boolean triggerEvent(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, int id, int param) {
		super.triggerEvent(state, world, pos, id, param);
		BlockEntity tile = world.getBlockEntity(pos);
		return tile != null && tile.triggerEvent(id, param);
	}

	@Override
	public BlockItem provideItemBlock(Block block, Item.Properties properties) {
		return new TinyPotatoBlockItem(block, properties);
	}

	@NotNull
	@Override
	public RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new TinyPotatoBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return createTickerHelper(type, TinyPotatoModule.blockEntityType, TinyPotatoBlockEntity::commonTick);
	}
}
