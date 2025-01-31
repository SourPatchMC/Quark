package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.QuarkModule;

public class QuarkStandingSignBlock extends StandingSignBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkStandingSignBlock(String regname, QuarkModule module, WoodType type, Properties properties) {
		super(properties, type);
		this.module = module;

		RegistryHelper.registerBlock(this, regname, false);
	}

	@Override
	public QuarkStandingSignBlock setCondition(BooleanSupplier enabledSupplier) {
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

}
