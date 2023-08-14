package vazkii.quark.api.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ClientOnly
public interface IConfigCategory extends IConfigElement {

	IConfigCategory addCategory(String name, @NotNull String comment, Object holderObject);
	<T> IConfigElement addEntry(ConfigValue<T> value, T default_, Supplier<T> getter, @NotNull String comment, @NotNull Predicate<Object> restriction);

	default <T> void addEntry(ConfigValue<T> forgeValue) {
		addEntry(forgeValue, forgeValue.get(), forgeValue::get, "", o -> true);
	}

	default IConfigCategory addCategory(String name) {
		return addCategory(name, "", null);
	}

	// getters you probably don't have any use for
	String getPath();
	int getDepth();
	List<IConfigElement> getSubElements();

	// probably stuff you shouldn't touch

	void updateDirty();
	void close();


}
