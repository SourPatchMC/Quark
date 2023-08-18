package vazkii.quark.content.mobs.client.render.entity;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.layer.StonelingItemLayer;
import vazkii.quark.content.mobs.client.layer.StonelingLichenLayer;
import vazkii.quark.content.mobs.client.model.StonelingModel;
import vazkii.quark.content.mobs.entity.Stoneling;

@ClientOnly
public class StonelingRenderer extends MobRenderer<Stoneling, StonelingModel> {

	public StonelingRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.stoneling), 0.3F);
		addLayer(new StonelingItemLayer(this));
		addLayer(new StonelingLichenLayer(this));
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull Stoneling entity) {
		return entity.getVariant().getTexture();
	}

}
