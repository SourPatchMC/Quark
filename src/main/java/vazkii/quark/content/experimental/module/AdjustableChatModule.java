package vazkii.quark.content.experimental.module;

import io.github.fabricators_of_create.porting_lib.event.client.OverlayRenderCallback;
import net.fabricmc.api.EnvType;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false, hasSubscriptions = true, subscribeOn = EnvType.CLIENT)
public class AdjustableChatModule extends QuarkModule {

	@Config public static int horizontalShift = 0;
	@Config public static int verticalShift = 0;
	
	@SubscribeEvent
	@ClientOnly
	public void pre(RenderGuiOverlayEvent.Pre event) {
		if(event.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			event.getPoseStack().translate(horizontalShift, verticalShift, 0);
	}
	
	@SubscribeEvent
	@ClientOnly
	public void post(RenderGuiOverlayEvent.Post event) {
		if(event.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			event.getPoseStack().translate(-horizontalShift, -verticalShift, 0);
	}
	
}
