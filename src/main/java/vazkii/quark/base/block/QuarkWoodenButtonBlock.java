package vazkii.quark.base.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.module.QuarkModule;

import org.jetbrains.annotations.NotNull;

public class QuarkWoodenButtonBlock extends QuarkButtonBlock {

	public QuarkWoodenButtonBlock(String regname, QuarkModule module, Properties properties) {
		super(regname, module, CreativeModeTab.TAB_REDSTONE, properties);
	}

	@NotNull
	@Override
	protected SoundEvent getSound(boolean powered) {
		return powered ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
	}

	@Override
	public int getPressDuration() {
		return 30;
	}

}
