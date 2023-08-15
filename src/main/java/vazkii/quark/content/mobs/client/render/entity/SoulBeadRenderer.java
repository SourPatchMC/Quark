package vazkii.quark.content.mobs.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.content.mobs.entity.SoulBead;

import org.jetbrains.annotations.NotNull;

public class SoulBeadRenderer extends EntityRenderer<SoulBead> {

	public SoulBeadRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull SoulBead entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

	@Override
	public boolean shouldRender(@NotNull SoulBead livingEntityIn, @NotNull Frustum camera, double camX, double camY, double camZ) {
		return false;
	}

}
