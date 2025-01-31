package vazkii.quark.base.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.settings.KeyModifier;

import org.jetbrains.annotations.NotNull;
import java.util.function.BiPredicate;

@ClientOnly
public class SortedPredicatedKeyBinding extends SortedKeyBinding {
	private final BiPredicate<KeyModifier, InputConstants.Key> allowed;

	public SortedPredicatedKeyBinding(String description, Type type, int keyCode, String category, int priority, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		super(description, type, keyCode, category, priority);
		this.allowed = allowed;
	}

	@Override
	public void setKeyModifierAndCode(@NotNull KeyModifier keyModifier, @NotNull InputConstants.Key keyCode) {
		if (allowed.test(keyModifier, keyCode))
			super.setKeyModifierAndCode(keyModifier, keyCode);
	}
}
