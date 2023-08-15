package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.be.VariantFurnaceBlockEntity;
import vazkii.quark.content.building.module.VariantFurnacesModule;

import org.jetbrains.annotations.NotNull;
import java.util.function.BooleanSupplier;

public class VariantFurnaceBlock extends FurnaceBlock implements IQuarkBlock {

	private final QuarkModule module;

	public VariantFurnaceBlock(String type, QuarkModule module, Properties props) {
		super(props);

		RegistryHelper.registerBlock(this, type + "_furnace");
		CreativeTabHandler.addTab(this, CreativeModeTab.TAB_DECORATIONS);

		this.module = module;
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new VariantFurnaceBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> beType) {
		return createFurnaceTicker(world, beType, VariantFurnacesModule.blockEntityType);
	}

	@Override
	protected void openContainer(Level world, @NotNull BlockPos pos, @NotNull Player player) {
		BlockEntity blockentity = world.getBlockEntity(pos);
		if(blockentity instanceof AbstractFurnaceBlockEntity furnace) {
			player.openMenu(furnace);
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
		}
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public IQuarkBlock setCondition(BooleanSupplier condition) {
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return true;
	}

}
