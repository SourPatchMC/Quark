package vazkii.quark.base.module.config.type;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.module.config.ConfigFlagManager;

public interface IConfigType {

	@ClientOnly 
	default void setCategory(ConfigCategory category) { }
	
	default void onReload(ConfigFlagManager flagManager) { }
	
}
