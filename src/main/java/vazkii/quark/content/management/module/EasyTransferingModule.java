package vazkii.quark.content.management.module;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import vazkii.quark.base.client.handler.InventoryButtonHandler;
import vazkii.quark.base.client.handler.InventoryButtonHandler.ButtonTargetType;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.InventoryTransferMessage;
import vazkii.quark.content.management.client.screen.widgets.MiniInventoryButton;

@LoadModule(category = ModuleCategory.MANAGEMENT)
public class EasyTransferingModule extends QuarkModule {

	public static boolean shiftLocked = false;

	@Config public static boolean enableShiftLock = true;

	@Override
	@ClientOnly
	public void registerKeybinds(RegisterKeyMappingsEvent event) {
		addButton(event, 1, "insert", false);
		addButton(event, 2, "extract", true);

			InventoryButtonHandler.addButtonProvider(event, this, ButtonTargetType.CONTAINER_PLAYER_INVENTORY, 3,
					"shift_lock",
					(screen) -> shiftLocked = !shiftLocked,
					(parent, x, y) -> new MiniInventoryButton(parent, 4, x, y, "quark.gui.button.shift_lock",
							(b) -> shiftLocked = !shiftLocked)
					.setTextureShift(() -> shiftLocked),
					() -> enableShiftLock);
	}

	@ClientOnly
	private void addButton(RegisterKeyMappingsEvent event, int priority, String name, boolean restock) {
		InventoryButtonHandler.addButtonProvider(event, this, ButtonTargetType.CONTAINER_PLAYER_INVENTORY, priority,
				"transfer_" + name,
				(screen) -> QuarkNetwork.sendToServer(new InventoryTransferMessage(Screen.hasShiftDown(), restock)),
				(parent, x, y) -> new MiniInventoryButton(parent, priority, x, y, 
						(t) -> t.add(I18n.get("quark.gui.button." + name + (Screen.hasShiftDown() ? "_filtered" : ""))),
						(b) -> QuarkNetwork.sendToServer(new InventoryTransferMessage(Screen.hasShiftDown(), restock)))
				.setTextureShift(Screen::hasShiftDown),
				null);
	}

	public static boolean hasShiftDown(boolean ret) {
		return ret || (enableShiftLock && shiftLocked);
	}
	
}
