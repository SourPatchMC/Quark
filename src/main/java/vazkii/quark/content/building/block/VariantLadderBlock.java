package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

import org.jetbrains.annotations.NotNull;
import java.util.function.BooleanSupplier;

public class VariantLadderBlock extends LadderBlock implements IQuarkBlock {

	private final QuarkModule module;
	private final boolean flammable;

	private BooleanSupplier condition = () -> true;

	public VariantLadderBlock(String type, QuarkModule module, Block.Properties props, boolean flammable) {
		super(props);

		RegistryHelper.registerBlock(this, type + "_ladder");
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		this.module = module;
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);

		this.flammable = flammable;
	}

	public VariantLadderBlock(String type, QuarkModule module, boolean flammable) {
		this(type, module,
				Block.Properties.copy(Blocks.LADDER),
			flammable);
	}

	// TODO: AAAAAAAAAAA - Maximum
	// @Override
	// public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
	// 	return flammable;
	// }

	@Override
	public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public VariantLadderBlock setCondition(BooleanSupplier condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return condition.getAsBoolean();
	}

}
