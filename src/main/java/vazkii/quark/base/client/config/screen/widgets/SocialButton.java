package vazkii.quark.base.client.config.screen.widgets;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.TopLayerTooltipHandler;

public class SocialButton extends Button {

	public static final ResourceLocation SOCIAL_ICONS = new ResourceLocation(Quark.MOD_ID, "textures/gui/social_icons.png");

	private final Component text;
	private final int textColor;
	private final int socialId;

	public SocialButton(int x, int y, Component text, int textColor, int socialId, OnPress onClick) {
		super(x, y, 20, 20, Component.literal(""), onClick);
		this.textColor = textColor;
		this.socialId = socialId;
		this.text = text;
	}

	@Override
	public void renderButton(@NotNull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		// For more information, please refer to QButton.renderButton();
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		int k = this.getYImage(this.isHoveredOrFocused());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(mstack, this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
		this.blit(mstack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
		this.renderBg(mstack, minecraft, mouseX, mouseY);
		int l = getFGColor();
		drawCenteredString(mstack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, l | Mth.ceil(this.alpha * 255.0F) << 24);
		if (this.isHoveredOrFocused()) {
			this.renderToolTip(mstack, mouseX, mouseY);
		}

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, SOCIAL_ICONS);

		int u = socialId * 20;
		int v = isHovered ? 20 : 0;

		blit(mstack, x, y, u, v, 20, 20, 128, 64);

		if(isHovered)
			TopLayerTooltipHandler.setTooltip(List.of(text.getString()), mouseX, mouseY);
	}

	public int getFGColor() {
		return textColor;
	}

}
