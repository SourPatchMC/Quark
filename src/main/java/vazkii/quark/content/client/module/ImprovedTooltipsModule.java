package vazkii.quark.content.client.module;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Lists;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.client.resources.AttributeTooltipManager;
import vazkii.quark.content.client.tooltip.AttributeTooltips;
import vazkii.quark.content.client.tooltip.EnchantedBookTooltips;
import vazkii.quark.content.client.tooltip.FoodTooltips;
import vazkii.quark.content.client.tooltip.FuelTooltips;
import vazkii.quark.content.client.tooltip.MapTooltips;
import vazkii.quark.content.client.tooltip.ShulkerBoxTooltips;

/**
 * @author WireSegal
 * Created at 6:19 PM on 8/31/19.
 */
@LoadModule(category = ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = EnvType.CLIENT)
public class ImprovedTooltipsModule extends QuarkModule {

	@Config public static boolean attributeTooltips = true;
	@Config public static boolean foodTooltips = true;
	@Config public static boolean shulkerTooltips = true;
	@Config public static boolean mapTooltips = true;
	@Config public static boolean enchantingTooltips = true;
	@Config public static boolean fuelTimeTooltips = true;
	
	@Config public static boolean shulkerBoxUseColors = true;
	@Config public static boolean shulkerBoxRequireShift = false;
	@Config public static boolean mapRequireShift = false;

	@Config(description = "The value of each shank of food.\n" +
			"Tweak this when using mods like Hardcore Hunger which change that value.")
	public static int foodDivisor = 2;
	
	@Config public static boolean showSaturation = true;
	@Config public static int foodCompressionThreshold = 4;
	
	@Config public static int fuelTimeDivisor = 200;
	
	@Config(description = "Should item attributes be colored relative to your current equipped item?\n"
			+ "e.g. if wearing an Iron Helmet, the armor value in a Diamond Helmet will show as green, and vice versa would be red.\n"
			+ "If set to false, item attributes will show in white or red if they're negative values.") 
	public static boolean showUpgradeStatus = true;
	@Config public static boolean animateUpDownArrows = true;

	@Config
	public static List<String> enchantingStacks = Lists.newArrayList("minecraft:diamond_sword", "minecraft:diamond_pickaxe", "minecraft:diamond_shovel", "minecraft:diamond_axe", "minecraft:diamond_hoe",
			"minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots",
			"minecraft:shears", "minecraft:bow", "minecraft:fishing_rod", "minecraft:crossbow", "minecraft:trident", "minecraft:elytra", "minecraft:shield",
			"quark:pickarang", "supplementaries:slingshot", "supplementaries:bubble_blower", "farmersdelight:diamond_knife");

	@Config(description = "A list of additional stacks to display on each enchantment\n"
			+ "The format is as follows:\n"
			+ "enchant_id=item1,item2,item3...\n"
			+ "So to display a carrot on a stick on a mending book, for example, you use:\n"
			+ "minecraft:mending=minecraft:carrot_on_a_stick")
	public static List<String> enchantingAdditionalStacks = Lists.newArrayList();

	private static final String IGNORE_TAG = "quark:no_tooltip";

	public static boolean staticEnabled;

	@Override
	@ClientOnly
	public void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		register(event, AttributeTooltips.AttributeComponent.class);
		register(event, FoodTooltips.FoodComponent.class);
		register(event, ShulkerBoxTooltips.ShulkerComponent.class);
		register(event, MapTooltips.MapComponent.class);
		register(event, EnchantedBookTooltips.EnchantedBookComponent.class);
		register(event, FuelTooltips.FuelComponent.class);
	}

	@Override
	@ClientOnly
	public void registerReloadListeners(Consumer<PreparableReloadListener> registry) {
		registry.accept(new AttributeTooltipManager());
	}

	@Override
	public void configChanged() {
		staticEnabled = enabled;
		EnchantedBookTooltips.reloaded();
	}

	private static boolean ignore(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, IGNORE_TAG, false);
	}

	@ClientOnly
	private static <T extends ClientTooltipComponent & TooltipComponent> void register(RegisterClientTooltipComponentFactoriesEvent event, Class<T> clazz) {
		event.register(clazz, Function.identity());
	}

	@ClientOnly
	public void makeTooltip(@NotNull ItemStack itemStack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @NotNull Font font, @NotNull List<ClientTooltipComponent> components) {
		if(ignore(itemStack))
			return;

		if (attributeTooltips)
			AttributeTooltips.makeTooltip(itemStack, poseStack, x, y, screenWidth, screenHeight, font, components);
		if (foodTooltips || showSaturation)
			FoodTooltips.makeTooltip(itemStack, poseStack, x, y, screenWidth, screenHeight, font, components, foodTooltips, showSaturation);
		if (shulkerTooltips)
			ShulkerBoxTooltips.makeTooltip(itemStack, poseStack, x, y, screenWidth, screenHeight, font, components);
		if (mapTooltips)
			MapTooltips.makeTooltip(itemStack, poseStack, x, y, screenWidth, screenHeight, font, components);
		if (enchantingTooltips)
			EnchantedBookTooltips.makeTooltip(itemStack, poseStack, x, y, screenWidth, screenHeight, font, components);
		if(fuelTimeTooltips)
			FuelTooltips.makeTooltip(itemStack, poseStack, x, y, screenWidth, screenHeight, font, components);
	}
}
