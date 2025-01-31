package vazkii.quark.content.experimental.module;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import java.util.List;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false,
description = "This feature generates Resource Pack Item Model predicates on the items defined in 'Items to Change'\n"
		+ "for the Enchantments defined in 'Enchantments to Register'.\n\n"
		+ "Example: if 'minecraft:silk_touch' is added to 'Enchantments to Register', and 'minecraft:netherite_pickaxe'\n"
		+ "is added to 'Items to Change', then a predicate named 'quark_has_enchant_minecraft_silk_touch' will be available\n"
		+ "to the netherite_pickaxe.json item model, whose value will be the enchantment level.")
public class EnchantmentPredicatesModule extends QuarkModule {

	@Config
	public static List<String> itemsToChange = Lists.newArrayList();

	@Config
	public static List<String> enchantmentsToRegister = Lists.newArrayList();

	@Override
	@ClientOnly
	public void clientSetup() {
		if(enabled) {
			enqueue(() -> {
				List<Item> items = MiscUtil.massRegistryGet(itemsToChange, Registry.ITEM);
				List<Enchantment> enchants = MiscUtil.massRegistryGet(enchantmentsToRegister, Registry.ENCHANTMENT);

				for(Enchantment enchant : enchants) {
					ResourceLocation enchantRes = Registry.ENCHANTMENT.getKey(enchant);
					ResourceLocation name = new ResourceLocation(Quark.MOD_ID + "_has_enchant_" + enchantRes.getNamespace() + "_" + enchantRes.getPath());
					ItemPropertyFunction fun = (stack, level, entity, i) -> EnchantmentHelper.getItemEnchantmentLevel(enchant, stack);

					for(Item item : items)
						ItemProperties.register(item, name, fun);
				}
			});
		}
	}


}
