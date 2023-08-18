package vazkii.quark.content.experimental.module;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false)
public class EnchantmentsBegoneModule extends QuarkModule {

	@Config
	public static List<String> enchantmentsToBegone = Lists.newArrayList();

	private static boolean staticEnabled;

	private static final List<Enchantment> enchantments = Lists.newArrayList();

	@Override
	public void configChanged() {
		staticEnabled = enabled;

		enchantments.clear();

		for (String enchantKey : enchantmentsToBegone) {
			Enchantment enchantment = Registry.ENCHANTMENT.get(new ResourceLocation(enchantKey));
			if (enchantment != null)
				enchantments.add(enchantment);
		}
	}

	public static void begoneItems(NonNullList<ItemStack> stacks) {
		if (!staticEnabled)
			return;

		stacks.removeIf((it) -> {
			if (it.is(Items.ENCHANTED_BOOK)) {
				Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(it);
				for (Enchantment key : enchants.keySet()) {
					if (enchantments.contains(key)) {
						return true;
					}
				}
			}
			return false;
		});
	}

	public static boolean shouldBegone(Enchantment enchantment) {
		return staticEnabled && enchantments.contains(enchantment);
	}

	public static List<Enchantment> begoneEnchantments(List<Enchantment> list) {
		if (!staticEnabled)
			return list;

		return list.stream().filter(Predicate.not(enchantments::contains)).collect(Collectors.toList());
	}

	public static List<EnchantmentInstance> begoneEnchantmentInstances(List<EnchantmentInstance> list) {
		if (!staticEnabled)
			return list;

		return list.stream().filter(it -> !enchantments.contains(it.enchantment)).collect(Collectors.toList());
	}
}
