package vazkii.quark.base.module.config.type;

import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.client.config.ConfigCategory;

public class AbstractConfigType implements IConfigType {

	@ClientOnly
	protected ConfigCategory category;

	public AbstractConfigType() { }

	@Override
	@ClientOnly
	public void setCategory(ConfigCategory category) {
		this.category = category;
	}

}
