package vazkii.quark.content.mobs.client.render.entity;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.layer.shiba.ShibaCollarLayer;
import vazkii.quark.content.mobs.client.layer.shiba.ShibaMouthItemLayer;
import vazkii.quark.content.mobs.client.model.ShibaModel;
import vazkii.quark.content.mobs.entity.Shiba;

public class ShibaRenderer extends MobRenderer<Shiba, ShibaModel> {

	private static final ResourceLocation[] SHIBA_BASES = {
			new ResourceLocation(Quark.MOD_ID, "textures/model/entity/shiba/shiba0.png"),
			new ResourceLocation(Quark.MOD_ID, "textures/model/entity/shiba/shiba1.png"),
			new ResourceLocation(Quark.MOD_ID, "textures/model/entity/shiba/shiba2.png")
	};

	private static final ResourceLocation SHIBA_RARE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/shiba/shiba_rare.png");
	private static final ResourceLocation SHIBA_DOGE = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/shiba/shiba_doge.png");

	public ShibaRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.shiba), 0.5F);
		addLayer(new ShibaCollarLayer(this));
		addLayer(new ShibaMouthItemLayer(this, context.getItemInHandRenderer()));
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(Shiba entity) {
		if(entity.hasCustomName() && entity.getCustomName().getContents().toString().trim().equalsIgnoreCase("doge"))
			return SHIBA_DOGE;

		long least = Math.abs(entity.getUUID().getLeastSignificantBits());
		if((least % 200) == 0)
			return SHIBA_RARE;

		int type = (int) (least % SHIBA_BASES.length);
		return SHIBA_BASES[type];
	}

}
