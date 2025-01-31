package vazkii.quark.content.mobs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.content.mobs.client.model.StonelingModel;
import vazkii.quark.content.mobs.entity.Stoneling;

import org.jetbrains.annotations.NotNull;

public class StonelingLichenLayer extends RenderLayer<Stoneling, StonelingModel> {

	private static final ResourceLocation MOLD_LAYER = new ResourceLocation(Quark.MOD_ID, "textures/model/entity/stoneling/lichen_layer.png");

	public StonelingLichenLayer(RenderLayerParent<Stoneling, StonelingModel> renderer) {
		super(renderer);
	}

	@Override
	public void render(@NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int light, Stoneling stoneling, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
		if(stoneling.getEntityData().get(Stoneling.HAS_LICHEN))
			renderColoredCutoutModel(getParentModel(), MOLD_LAYER, matrix, buffer, light, stoneling, 1F, 1F, 1F);
	}

}
