package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.arl.interf.IBlockItemProvider;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.be.VariantTrappedChestBlockEntity;
import vazkii.quark.content.building.module.VariantChestsModule.IChestTextureProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class VariantTrappedChestBlock extends ChestBlock implements IBlockItemProvider, IQuarkBlock, IChestTextureProvider {

	public final String type;
	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	private final String path;

	public VariantTrappedChestBlock(String prefix, String type, QuarkModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
		super(props, supplier);
		RegistryHelper.registerBlock(this, (prefix != null ? prefix + "_" : "") + type + "_trapped_chest");

		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_REDSTONE);

		this.type = type;
		this.module = module;

		path = (isCompat() ? "compat/" : "") + type + "/";
	}

	public VariantTrappedChestBlock(String type, QuarkModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
		this(null, type, module, supplier, props);
	}

	protected boolean isCompat() {
		return false;
	}

	// TODO: AAAAAAAAAA - Maximum
	// @Override
	// public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
	// 	return 0;
	// }

	// @Override
	// public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
	// 	return false;
	// }

	@Override
	public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public VariantTrappedChestBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState p_153065_) {
		return new VariantTrappedChestBlockEntity(pos, p_153065_);
	}

	@Override
	@ClientOnly
	public BlockItem provideItemBlock(Block block, Item.Properties props) {
		return new VariantChestBlock.Item(block, props);
	}

	public static class Compat extends VariantTrappedChestBlock {

		public Compat(String type, String mod, QuarkModule module, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, Properties props) {
			super(type, module, supplier, props);
			setCondition(() -> QuiltLoader.isModLoaded(mod));
		}

		@Override
		protected boolean isCompat() {
			return true;
		}
	}

	@Override
	public String getChestTexturePath() {
		return "model/chest/" + path;
	}

	@Override
	public boolean isTrap() {
		return true;
	}

	// VANILLA TrappedChestBlock copy

	@NotNull
	@Override
	protected Stat<ResourceLocation> getOpenChestStat() {
		return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
	}

	@Override
	public boolean isSignalSource(@NotNull BlockState state) {
		return true;
	}

	@Override
	public int getSignal(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull Direction side) {
		return Mth.clamp(ChestBlockEntity.getOpenCount(world, pos), 0, 15);
	}

	@Override
	public int getDirectSignal(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull Direction side) {
		return side == Direction.UP ? state.getSignal(world, pos, side) : 0;
	}

}
