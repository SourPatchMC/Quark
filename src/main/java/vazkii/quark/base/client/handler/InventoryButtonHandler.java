package vazkii.quark.base.client.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.addons.oddities.client.screen.BackpackInventoryScreen;
import vazkii.quark.api.IQuarkButtonAllowed;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.handler.InventoryTransferHandler;
import vazkii.quark.base.module.QuarkModule;

@ClientOnly
@EventBusSubscriber(modid = Quark.MOD_ID, value = Dist.CLIENT)
public final class InventoryButtonHandler {

	private static final Multimap<ButtonTargetType, ButtonProviderHolder> providers = Multimaps.newSetMultimap(new HashMap<>(), TreeSet::new);
	private static final Multimap<ButtonTargetType, Button> currentButtons = Multimaps.newSetMultimap(new HashMap<>(), LinkedHashSet::new);

	@SubscribeEvent
	public static void initGui(ScreenEvent.Init.Post event) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = event.getScreen();
		if(GeneralConfig.printScreenClassnames) {
			String print = I18n.get("quark.misc.opened_screen", ChatFormatting.AQUA + screen.getClass().getName());
			Quark.LOG.info(print);

			if(mc.player != null)
				mc.player.sendSystemMessage(Component.literal(print));
		}
		currentButtons.clear();

		boolean apiAllowed = screen instanceof IQuarkButtonAllowed;
		if(screen instanceof AbstractContainerScreen<?> containerScreen && (apiAllowed || GeneralConfig.isScreenAllowed(screen))) {

			if(containerScreen instanceof InventoryScreen || containerScreen.getClass().getName().contains("CuriosScreen"))
				applyProviders(event, ButtonTargetType.PLAYER_INVENTORY, containerScreen, s -> s.container == mc.player.getInventory() && s.getSlotIndex() == 17);
			else {
				if(apiAllowed || InventoryTransferHandler.accepts(containerScreen.getMenu(), mc.player)) {
					applyProviders(event, ButtonTargetType.CONTAINER_INVENTORY, containerScreen, s -> s.container != mc.player.getInventory() && s.getSlotIndex() == 8);
					applyProviders(event, ButtonTargetType.CONTAINER_PLAYER_INVENTORY, containerScreen, s -> s.container == mc.player.getInventory() && s.getSlotIndex() == 17);
				}
			}
		}
	}

	private static Collection<ButtonProviderHolder> forGui(Screen gui) {
		Set<ButtonProviderHolder> holders = new HashSet<>();
		if (gui instanceof AbstractContainerScreen<?> screen) {

			if (gui instanceof InventoryScreen)
				holders.addAll(providers.get(ButtonTargetType.PLAYER_INVENTORY));
			else {
				Minecraft mc = Minecraft.getInstance();
				if(InventoryTransferHandler.accepts(screen.getMenu(), mc.player)) {
					holders.addAll(providers.get(ButtonTargetType.CONTAINER_INVENTORY));
					holders.addAll(providers.get(ButtonTargetType.CONTAINER_PLAYER_INVENTORY));
				}
			}
		}

		return holders;
	}

	@SubscribeEvent
	public static void mouseInputEvent(ScreenEvent.MouseButtonPressed.Pre pressed) {
		Screen gui = pressed.getScreen();
		if (gui instanceof AbstractContainerScreen<?> screen) {
			if(!GeneralConfig.isScreenAllowed(screen))
				return;

			Collection<ButtonProviderHolder> holders = forGui(screen);

			for (ButtonProviderHolder holder : holders) {
				if (holder.keybind != null &&
						holder.keybind.matchesMouse(pressed.getButton()) &&
						(holder.keybind.getKeyModifier() == KeyModifier.NONE || holder.keybind.getKeyModifier().isActive(KeyConflictContext.GUI))) {
					holder.pressed.accept(screen);
					pressed.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void keyboardInputEvent(ScreenEvent.KeyPressed.Post pressed) {
		Screen gui = pressed.getScreen();
		if (gui instanceof AbstractContainerScreen<?> screen) {
			if(!GeneralConfig.isScreenAllowed(screen))
				return;

			Collection<ButtonProviderHolder> holders = forGui(screen);

			for (ButtonProviderHolder holder : holders) {
				if (holder.keybind != null &&
						holder.keybind.matches(pressed.getKeyCode(), pressed.getScanCode()) &&
						(holder.keybind.getKeyModifier() == KeyModifier.NONE || holder.keybind.getKeyModifier().isActive(KeyConflictContext.GUI))) {
					holder.pressed.accept(screen);
					pressed.setCanceled(true);
				}
			}
		}

	}

	private static void applyProviders(ScreenEvent.Init.Post event, ButtonTargetType type, AbstractContainerScreen<?> screen, Predicate<Slot> slotPred) {
		Collection<ButtonProviderHolder> holders = providers.get(type);
		if(!holders.isEmpty()) {
			for(Slot slot : screen.getMenu().slots)
				if(slotPred.test(slot)) {
					int x = slot.x + 6;
					int y = slot.y - 13;

					if(screen instanceof BackpackInventoryScreen)
						y -= 60;

					for(ButtonProviderHolder holder : holders) {
						Button button = holder.getButton(screen, x, y);
						if(button != null) {
							event.addListener(button);
							currentButtons.put(type, button);
							x -= 12;
						}
					}

					return;
				}
		}
	}

	public static Collection<Button> getActiveButtons(ButtonTargetType type) {
		return currentButtons.get(type);
	}

	public static void addButtonProvider(QuarkModule module, ButtonTargetType type, int priority, KeyMapping binding, Consumer<AbstractContainerScreen<?>> onKeybind, ButtonProvider provider, Supplier<Boolean> enableCond) {
		providers.put(type, new ButtonProviderHolder(module, priority, provider, binding, onKeybind, enableCond));
	}

	public static void addButtonProvider(RegisterKeyMappingsEvent event, QuarkModule module, ButtonTargetType type, int priority, String keybindName, Consumer<AbstractContainerScreen<?>> onKeybind, ButtonProvider provider, Supplier<Boolean> enableCond) {
		KeyMapping keybind = ModKeybindHandler.init(event, keybindName, null, ModKeybindHandler.INV_GROUP);
		keybind.setKeyConflictContext(KeyConflictContext.GUI);
		addButtonProvider(module, type, priority, keybind, onKeybind, provider, enableCond);
	}

	public static void addButtonProvider(QuarkModule module, ButtonTargetType type, int priority, ButtonProvider provider, Supplier<Boolean> enableCond) {
		providers.put(type, new ButtonProviderHolder(module, priority, provider, enableCond));
	}

	public enum ButtonTargetType {
		PLAYER_INVENTORY,
		CONTAINER_INVENTORY,
		CONTAINER_PLAYER_INVENTORY
	}

	public interface ButtonProvider {
		Button provide(AbstractContainerScreen<?> parent, int x, int y);
	}

	private static class ButtonProviderHolder implements Comparable<ButtonProviderHolder> {

		private final int priority;
		private final QuarkModule module;
		private final ButtonProvider provider;

		private final KeyMapping keybind;
		private final Consumer<AbstractContainerScreen<?>> pressed;
		private final Supplier<Boolean> enableCond;
		
		public ButtonProviderHolder(QuarkModule module, int priority, ButtonProvider provider, KeyMapping keybind, Consumer<AbstractContainerScreen<?>> onPressed, Supplier<Boolean> enableCond) {
			this.module = module;
			this.priority = priority;
			this.provider = provider;
			this.keybind = keybind;
			this.pressed = onPressed;
			this.enableCond = enableCond;
		}

		public ButtonProviderHolder(QuarkModule module, int priority, ButtonProvider provider, Supplier<Boolean> enableCond) {
			this(module, priority, provider, null, (screen) -> {}, enableCond);
		}

		@Override
		public int compareTo(@NotNull ButtonProviderHolder o) {
			return priority - o.priority;
		}

		public Button getButton(AbstractContainerScreen<?> parent, int x, int y) {
			return (module.enabled && (enableCond == null || enableCond.get())) 
					? provider.provide(parent, x, y) : null;
		}

	}

}
