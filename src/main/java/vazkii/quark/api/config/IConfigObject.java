package vazkii.quark.api.config;

import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public interface IConfigObject<T> extends IConfigElement {

	T getCurrentObj();
	void setCurrentObj(T obj);

}
