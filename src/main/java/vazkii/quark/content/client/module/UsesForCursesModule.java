package vazkii.quark.content.client.module;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tweaks.client.layer.ArmorStandFakePlayerLayer;

@LoadModule(category = ModuleCategory.CLIENT)
public class UsesForCursesModule extends QuarkModule {

	private static final ResourceLocation PUMPKIN_OVERLAY = new ResourceLocation("textures/misc/pumpkinblur.png");

	public static boolean staticEnabled;

	@Config(flag = "use_for_vanishing")
	public static boolean vanishPumpkinOverlay = true;

	@Config(flag = "use_for_binding")
	public static boolean bindArmorStandsWithPlayerHeads = true;
	
	@Hint(key = "use_for_vanishing", value = "use_for_vanishing")
	Item pumpkin = Items.CARVED_PUMPKIN;
	
	@Hint(key = "use_for_binding", value = "use_for_binding")
	List<Item> bindingItems = Arrays.asList(Items.ARMOR_STAND, Items.PLAYER_HEAD);

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	@Override
	@ClientOnly
	public void modelLayers(final Map<EntityType<?>, EntityRenderer<?>> renderers, final Map<String, EntityRenderer<? extends Player>> skinMap) {
		if (renderers.get(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer render) {
			render.addLayer(new ArmorStandFakePlayerLayer<>(render, Minecraft.getInstance().getEntityModels()));
		}
	}

	@ClientOnly
	public static boolean shouldHideArmorStandModel(ItemStack stack) {
		if(!staticEnabled || !bindArmorStandsWithPlayerHeads || !stack.is(Items.PLAYER_HEAD))
			return false;
		return EnchantmentHelper.hasBindingCurse(stack);
	}

	@ClientOnly
	public static boolean shouldHidePumpkinOverlay(ResourceLocation location, Player player) {
		if(!staticEnabled || !vanishPumpkinOverlay || !location.equals(PUMPKIN_OVERLAY))
			return false;
		ItemStack stack = player.getInventory().getArmor(3);
		return stack.is(Blocks.CARVED_PUMPKIN.asItem()) &&
				EnchantmentHelper.hasVanishingCurse(stack);
	}

}
