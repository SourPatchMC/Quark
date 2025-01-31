package vazkii.quark.content.mobs.client.layer.forgotten;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.content.mobs.entity.Forgotten;

@ClientOnly
public class ForgottenSheathedItemLayer<M extends EntityModel<Forgotten>> extends RenderLayer<Forgotten, M> {

	private final ItemInHandRenderer itemInHandRenderer;
	
	public ForgottenSheathedItemLayer(RenderLayerParent<Forgotten, M> parent, ItemInHandRenderer itemInHandRenderer) {
		super(parent);
		this.itemInHandRenderer = itemInHandRenderer;
	}

	@Override
	public void render(PoseStack matrix, @NotNull MultiBufferSource bufferIn, int packedLightIn, Forgotten entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack item = entitylivingbaseIn.getEntityData().get(Forgotten.SHEATHED_ITEM);

		matrix.pushPose();
		matrix.translate(0.1, 0.2, 0.15);
		matrix.scale(0.75F, 0.75F, 0.75F);
		matrix.mulPose(Vector3f.ZP.rotationDegrees(90));
		itemInHandRenderer.renderItem(entitylivingbaseIn, item, ItemTransforms.TransformType.NONE, true, matrix, bufferIn, packedLightIn);
		matrix.popPose();
	}
}
