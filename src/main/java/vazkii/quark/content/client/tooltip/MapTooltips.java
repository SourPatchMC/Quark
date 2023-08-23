package vazkii.quark.content.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;

import java.util.List;

public class MapTooltips {

	private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");

	@ClientOnly
	public static void makeTooltip(@NotNull ItemStack itemStack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @NotNull Font font, @NotNull List<ClientTooltipComponent> components) {
		if(!itemStack.isEmpty() && itemStack.getItem() instanceof MapItem) {

			if(!ImprovedTooltipsModule.mapRequireShift || Screen.hasShiftDown())
				components.add(1, new MapComponent(itemStack));
			else if(ImprovedTooltipsModule.mapRequireShift && !Screen.hasShiftDown())
				components.add(1, new ClientTextTooltip(Component.translatable("quark.misc.map_shift").getVisualOrderText()));
		}
	}

	@ClientOnly
	public record MapComponent(ItemStack stack) implements ClientTooltipComponent, TooltipComponent {

		@Override
		public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull PoseStack pose, @NotNull ItemRenderer itemRenderer, int something) {
			Minecraft mc = Minecraft.getInstance();

			MapItemSavedData mapdata = MapItem.getSavedData(stack, mc.level);
			Integer mapID = MapItem.getMapId(stack);

			if (mapdata == null)
				return;

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, RES_MAP_BACKGROUND);

			int pad = 7;
			int size = 135 + pad;
			float scale = 0.5F;

			pose.pushPose();
			pose.translate(tooltipX + 3, tooltipY + 3, 500);
			pose.scale(scale, scale, 1F);
			RenderSystem.enableBlend();

			GuiComponent.blit(pose, -pad, -pad, 0, 0, size, size, size, size);

			BufferBuilder buffer = Tesselator.getInstance().getBuilder();
			MultiBufferSource.BufferSource immediateBuffer = MultiBufferSource.immediate(buffer);
			mc.gameRenderer.getMapRenderer().render(pose, immediateBuffer, mapID, mapdata, true, 240);
			immediateBuffer.endBatch();
			pose.popPose();
		}

		@Override
		public int getHeight() {
			Minecraft mc = Minecraft.getInstance();
			MapItemSavedData mapdata = MapItem.getSavedData(stack, mc.level);
			return mapdata != null ? 75 : 0;
		}

		@Override
		public int getWidth(@NotNull Font font) {
			return 72;
		}
	}


}
