package vazkii.quark.content.tools.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.api.QuarkCapabilities;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;

/**
 * @author WireSegal
 * Created at 2:27 PM on 8/17/19.
 */
public class RuneItem extends QuarkItem implements IRuneColorProvider {

	private final int color;
	private final boolean glow;

	public RuneItem(String regname, QuarkModule module, int color, boolean glow) {
		super(regname, module, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));
		this.color = color;
		this.glow = glow;
	}

	@Override
	public boolean isFoil(@NotNull ItemStack stack) {
		return glow;
	}

	@Override
	public int getRuneColor(ItemStack stack) {
		return color;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		final LazyOptional<IRuneColorProvider> holder = LazyOptional.of(() -> this);

		return new ICapabilityProvider() {

			@NotNull
			@Override
			public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
				return QuarkCapabilities.RUNE_COLOR.orEmpty(cap, holder);
			}

		};
	}
}
