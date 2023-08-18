package vazkii.quark.base.client.util;

import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;
import org.quiltmc.loader.api.minecraft.ClientOnly;

/**
 * @author WireSegal
 * Created at 12:19 PM on 10/6/19.
 */
@ClientOnly
public class SortedKeyBinding extends KeyMapping {
	private final int priority;

	public SortedKeyBinding(String description, Type type, int keyCode, String category, int priority) {
		super(description, type, keyCode, category);
		this.priority = priority;
	}

	@Override
	public int compareTo(KeyMapping keyBinding) {
		if (this.getCategory().equals(keyBinding.getCategory()) && keyBinding instanceof SortedKeyBinding sorted)
			return Integer.compare(priority, sorted.priority);
		return super.compareTo(keyBinding);
	}
}
