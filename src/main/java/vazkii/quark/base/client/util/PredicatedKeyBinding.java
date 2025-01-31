package vazkii.quark.base.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.settings.KeyModifier;

import org.jetbrains.annotations.NotNull;
import java.util.function.BiPredicate;

@ClientOnly
public class PredicatedKeyBinding extends KeyMapping {
	private final BiPredicate<KeyModifier, InputConstants.Key> allowed;

	public PredicatedKeyBinding(String description, Type type, int keyCode, String category, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		super(description, type, keyCode, category);
		this.allowed = allowed;
	}

	@Override
	public void setKeyModifierAndCode(@NotNull KeyModifier keyModifier, @NotNull InputConstants.Key keyCode) {
		if (allowed.test(keyModifier, keyCode))
			super.setKeyModifierAndCode(keyModifier, keyCode);
	}
}
