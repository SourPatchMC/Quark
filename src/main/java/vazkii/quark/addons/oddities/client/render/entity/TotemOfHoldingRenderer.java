package vazkii.quark.addons.oddities.client.render.entity;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.addons.oddities.entity.TotemOfHoldingEntity;
import vazkii.quark.base.Quark;

/**
 * @author WireSegal
 * Created at 2:01 PM on 3/30/20.
 */
@ClientOnly
public class TotemOfHoldingRenderer extends EntityRenderer<TotemOfHoldingEntity> {

	private static final ModelResourceLocation LOCATION_MODEL = new ModelResourceLocation(Quark.MOD_ID, "extra/totem_of_holding", "inventory");
	
	public TotemOfHoldingRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(TotemOfHoldingEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
		int deathTicks = entity.getDeathTicks();
		boolean dying = entity.isDying();
		float time = ClientTicker.ticksInGame + partialTicks;
		float scale = !dying ? 1F : Math.max(0, TotemOfHoldingEntity.DEATH_TIME - (deathTicks + partialTicks)) / TotemOfHoldingEntity.DEATH_TIME;
		float rotation = time + (!dying ? 0 : (deathTicks + partialTicks) * 5);
		double translation = !dying ? (Math.sin(time * 0.03) * 0.1) : ((deathTicks + partialTicks) / TotemOfHoldingEntity.DEATH_TIME * 5);

		Minecraft mc = Minecraft.getInstance();
		BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
		ModelManager modelManager = mc.getModelManager();

		matrixStackIn.pushPose();
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rotation));
		matrixStackIn.translate(0, translation, 0);
		matrixStackIn.scale(scale, scale, scale);
		matrixStackIn.translate(-0.5, 0, -0.5);
		dispatcher.getModelRenderer().
				renderModel(matrixStackIn.last(), bufferIn.getBuffer(Sheets.cutoutBlockSheet()),
						null,
						modelManager.getModel(LOCATION_MODEL), 1.0F, 1.0F, 1.0F, packedLightIn, OverlayTexture.NO_OVERLAY);
		matrixStackIn.popPose();

		super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	protected int getBlockLightLevel(@NotNull TotemOfHoldingEntity entityIn, @NotNull BlockPos position) {
		return 15;
	}

	@Override
	protected boolean shouldShowName(TotemOfHoldingEntity entity) {
		if (entity.hasCustomName()) {
			Minecraft mc = Minecraft.getInstance();
			return !mc.options.hideGui && mc.hitResult != null &&
					mc.hitResult.getType() == HitResult.Type.ENTITY &&
					((EntityHitResult) mc.hitResult).getEntity() == entity;
		}

		return false;
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(@NotNull TotemOfHoldingEntity entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
