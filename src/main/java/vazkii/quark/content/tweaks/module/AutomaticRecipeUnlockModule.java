package vazkii.quark.content.tweaks.module;

import com.google.common.collect.Lists;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerTickEvents;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameRules;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.ScreenEvent.Init;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import java.util.*;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class AutomaticRecipeUnlockModule extends QuarkModule {

	@Config(description = "A list of recipe names that should NOT be added in by default")
	public static List<String> ignoredRecipes = Lists.newArrayList();

	@Config public static boolean forceLimitedCrafting = false;
	@Config public static boolean disableRecipeBook = false;
	@Config(description = "If enabled, advancements granting recipes will be stopped from loading, " +
			"potentially reducing the lagspike on first world join.")
	public static boolean filterRecipeAdvancements = true;

	private static boolean staticEnabled;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	public AutomaticRecipeUnlockModule() {
		super();
		ServerPlayConnectionEvents.INIT.register((handler, server) -> onPlayerLoggedIn(handler.getPlayer()));
		ClientTickEvents.START.register((minecraft) -> clientTick());
	}

	public void onPlayerLoggedIn(ServerPlayer serverPlayer) {
		MinecraftServer server = serverPlayer.getServer();
		if (server != null) {
			List<Recipe<?>> recipes = new ArrayList<>(server.getRecipeManager().getRecipes());
			recipes.removeIf((recipe) -> recipe == null || recipe.getResultItem() == null
					|| ignoredRecipes.contains(Objects.toString(recipe.getId())) || recipe.getResultItem().isEmpty());

			int idx = 0;
			int maxShift = 1000;
			int shift;
			int size = recipes.size();
			do {
				shift = size - idx;
				int effShift = Math.min(maxShift, shift);

				List<Recipe<?>> sectionedRecipes = recipes.subList(idx, idx + effShift);
				serverPlayer.awardRecipes(sectionedRecipes);
				idx += effShift;
			} while (shift > maxShift);

			if (forceLimitedCrafting) serverPlayer.level.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(true, server);
		}

	}

	@SubscribeEvent
	@ClientOnly
	public void onInitGui(Init.Post event) {
		Screen gui = event.getScreen();
		if(disableRecipeBook && gui instanceof RecipeUpdateListener) {
			Minecraft.getInstance().player.getRecipeBook().getBookSettings().setOpen(RecipeBookType.CRAFTING, false);

			List<GuiEventListener> widgets = event.getListenersList();
			for(GuiEventListener w : widgets)
				if(w instanceof ImageButton) {
					event.removeListener(w);
					return;
				}
		}
	}

	@ClientOnly
	public void clientTick() {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player != null && mc.player.tickCount < 20) {
			ToastComponent toasts = mc.getToasts();
			Queue<Toast> toastQueue = toasts.queued;
			for(Toast toast : toastQueue)
				if(toast instanceof RecipeToast recipeToast) {
					List<Recipe<?>> stacks = recipeToast.recipes;
					if(stacks.size() > 100) {
						toastQueue.remove(toast);
						return;
					}
				}
		}
	}

	public static void removeRecipeAdvancements(Map<ResourceLocation, Advancement.Builder> builders) {
		if (!staticEnabled || !filterRecipeAdvancements)
			return;

		int i = 0;
		for (var iterator = builders.entrySet().iterator(); iterator.hasNext(); ) {
			Map.Entry<ResourceLocation, Advancement.Builder> entry = iterator.next();
			if (entry.getKey().getPath().startsWith("recipes/") && entry.getValue().getCriteria().containsKey("has_the_recipe")) {
				iterator.remove();
				i++;
			}
		}
		Quark.LOG.info("[Automatic Recipe Unlock] Removed {} recipe advancements", i);
	}
}
