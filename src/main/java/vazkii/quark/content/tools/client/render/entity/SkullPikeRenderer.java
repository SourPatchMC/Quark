package vazkii.quark.content.tools.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.tools.entity.SkullPike;

import org.jetbrains.annotations.NotNull;

public class SkullPikeRenderer extends EntityRenderer<SkullPike> {

	public SkullPikeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(@NotNull SkullPike entity, float yaw, float partialTicks, @NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int light) {
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull SkullPike arg0) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

}
