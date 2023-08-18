package vazkii.quark.api.config;

import java.util.Map;
import java.util.function.Consumer;

import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public interface IExternalCategory extends IConfigCategory {

	void commit();
	Map<String, IConfigCategory> getTopLevelCategories();

	IExternalCategory addTopLevelCategory(String name, Consumer<IExternalCategory> onChangedCallback);

}
