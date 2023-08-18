package vazkii.quark.base.module.config.type;

import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.module.config.ConfigFlagManager;

public interface IConfigType {

	@ClientOnly 
	default void setCategory(ConfigCategory category) { }
	
	default void onReload(ConfigFlagManager flagManager) { }
	
}
