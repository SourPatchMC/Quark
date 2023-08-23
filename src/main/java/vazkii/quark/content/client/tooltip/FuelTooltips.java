package vazkii.quark.content.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;

import java.util.List;
import java.util.Optional;

public class FuelTooltips {

	@ClientOnly
	public static void makeTooltip(@NotNull ItemStack itemStack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @NotNull Font font, @NotNull List<ClientTooltipComponent> components) {
		if(!itemStack.isEmpty()) {
			Screen screen = Minecraft.getInstance().screen;
			if(screen != null && screen instanceof AbstractFurnaceScreen<?>) {
				Optional<Integer> count = ItemContentRegistries.FUEL_TIME.get(itemStack.getItem());
				if (count.isPresent() && count.get() > 0) {
					String time = getDisplayString(count.get());
					components.add(new FuelComponent(itemStack, 18 + font.width(time), count.get()));
				}
			}
		}
	}
	
	private static String getDisplayString(int count) {
		float items = (float) count / (float) Math.max(1, ImprovedTooltipsModule.fuelTimeDivisor);
		String time = String.format(((items - (int) items) == 0) ? "x%.0f" : "x%.1f", items);
		return time;
	}
	
	
	@ClientOnly
	public record FuelComponent(ItemStack stack, int width, int count) implements ClientTooltipComponent, TooltipComponent {

		@Override
		public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull PoseStack pose, @NotNull ItemRenderer itemRenderer, int something) {
			pose.pushPose();
			pose.translate(tooltipX, tooltipY, 500);
		
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			GuiComponent.blit(pose, 1, 1, 0, 128, 13, 13, 256, 256);
			
			String time = getDisplayString(count);
			font.drawShadow(pose, time, 16, 5, 0xffb600);
			
			pose.popPose();			
		}

		@Override
		public int getHeight() {
			return 18;
		}

		@Override
		public int getWidth(@NotNull Font font) {
			return width;
		}
	}
	
}
