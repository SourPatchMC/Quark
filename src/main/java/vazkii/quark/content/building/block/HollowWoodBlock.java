package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

import org.jetbrains.annotations.NotNull;

import static vazkii.quark.base.handler.MiscUtil.directionProperty;

/**
 * Unfortunately, due to Ladder Weirdness (tm) this block is NYI
 */
public class HollowWoodBlock extends HollowFrameBlock {

    protected static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    protected static final BooleanProperty UP = BlockStateProperties.UP;
    protected static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    protected static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    protected static final BooleanProperty WEST = BlockStateProperties.WEST;
    protected static final BooleanProperty EAST = BlockStateProperties.EAST;

    private final boolean flammable;

    public HollowWoodBlock(Block sourceLog, QuarkModule module, boolean flammable) {
        this(IQuarkBlock.inherit(sourceLog, "hollow_%s"), sourceLog, module, flammable);
    }

    public HollowWoodBlock(String name, Block sourceLog, QuarkModule module, boolean flammable) {
        super(name, module, CreativeModeTab.TAB_DECORATIONS,
                MiscUtil.copyPropertySafe(sourceLog)
                        .isSuffocating((s, g, p) -> false));

        this.flammable = flammable;
        RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT_MIPPED);
        registerDefaultState(defaultBlockState()
            .setValue(DOWN, true)
            .setValue(UP, true)
            .setValue(NORTH, true)
            .setValue(SOUTH, true)
            .setValue(WEST, true)
            .setValue(EAST, true));
    }

    @Override
    public byte getShapeCode(BlockState state) {
        return shapeCode(state, DOWN, UP, NORTH, SOUTH, WEST, EAST);
    }

    // Disabling this on the quilt port until this feature is implemented in main version.
    /*@Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        BlockState newState = state;
        for (Direction dir : Direction.values())
            newState = newState.setValue(directionProperty(dir), state.getValue(directionProperty(direction.rotate(dir))));
        return newState;
    }*/

    @NotNull
    @Override
    public BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        BlockState newState = state;
        for (Direction dir : Direction.values())
            newState = newState.setValue(directionProperty(dir), state.getValue(directionProperty(mirror.mirror(dir))));
        return newState;
    }

    // Temporary method
    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction == ToolActions.AXE_STRIP) {
            Vec3 exactPos = context.getClickLocation();
            BlockPos centerPos = context.getClickedPos();
            Direction face = Direction.getNearest(exactPos.x - (centerPos.getX() + 0.5), exactPos.y - (centerPos.getY() + 0.5), exactPos.z - (centerPos.getZ() + 0.5));
            return state.cycle(directionProperty(face));
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> def) {
        super.createBlockStateDefinition(def);
        def.add(UP, DOWN, NORTH, SOUTH, WEST, EAST);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return flammable;
    }
}

