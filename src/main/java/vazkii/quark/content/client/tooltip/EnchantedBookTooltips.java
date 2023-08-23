package vazkii.quark.content.client.tooltip;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.RenderTooltipEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;
import vazkii.quark.content.tools.item.AncientTomeItem;
import vazkii.quark.content.tools.module.AncientTomesModule;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantedBookTooltips {

	private static List<ItemStack> testItems = null;
	private static Multimap<Enchantment, ItemStack> additionalStacks = null;

	public static final String TABLE_ONLY_DISPLAY = "quark:only_show_table_enchantments";

	public static void reloaded() {
		additionalStacks = null;
		testItems = null;
	}

	@ClientOnly
	public static void makeTooltip(@NotNull ItemStack itemStack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @NotNull Font font, @NotNull List<ClientTooltipComponent> components) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player == null)
			return;

		if(itemStack.getItem() == Items.ENCHANTED_BOOK || itemStack.getItem() == AncientTomesModule.ancient_tome) {
			int tooltipIndex = 0;

			List<EnchantmentInstance> enchants = getEnchantedBookEnchantments(itemStack);
			for(EnchantmentInstance ed : enchants) {
				Component match;
				if (itemStack.getItem() == Items.ENCHANTED_BOOK)
					match = ed.enchantment.getFullname(ed.level);
				else
					match = AncientTomeItem.getFullTooltipText(ed.enchantment);

				for(; tooltipIndex < components.size(); tooltipIndex++) {
					ClientTooltipComponent elmAt = components.get(tooltipIndex);
					if (elmAt.equals(new ClientTextTooltip(match.getVisualOrderText()))) {
						boolean tableOnly = ItemNBTHelper.getBoolean(itemStack, TABLE_ONLY_DISPLAY, false);
						List<ItemStack> items = getItemsForEnchantment(ed.enchantment, tableOnly);
						int itemCount = items.size();
						int lines = (int) Math.ceil((double) itemCount / 10.0);

						int len = 3 + Math.min(10, itemCount) * 9;
						components.add(tooltipIndex + 1, new EnchantedBookComponent(len, lines * 10, ed.enchantment, tableOnly));

						break;
					}
				}
			}
		}
	}

	private static ItemStack BOOK;

	public static List<ItemStack> getItemsForEnchantment(Enchantment e, boolean onlyForTable) {
		List<ItemStack> list = new ArrayList<>();

		for(ItemStack stack : getTestItems()) {
			Item item = stack.getItem();
			if(item instanceof QuarkItem && !((QuarkItem) item).isEnabled())
				continue;

			if(!stack.isEmpty() && e.canEnchant(stack)) {
				if (onlyForTable && (!e.canApplyAtEnchantingTable(stack) || !stack.isEnchantable() || stack.getEnchantmentValue() <= 0))
					continue;
				list.add(stack);
			}
		}

		if (onlyForTable) {
			if (BOOK == null)
				BOOK = new ItemStack(Items.BOOK);
			list.add(BOOK);
		}

		if(getAdditionalStacks().containsKey(e))
			list.addAll(getAdditionalStacks().get(e));

		return list;
	}

	public static List<EnchantmentInstance> getEnchantedBookEnchantments(ItemStack stack) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

		List<EnchantmentInstance> retList = new ArrayList<>(enchantments.size());

		for(Enchantment enchantment : enchantments.keySet()) {
			if (enchantment != null) {
				int level = enchantments.get(enchantment);
				retList.add(new EnchantmentInstance(enchantment, level));
			}
		}

		return retList;
	}

	private static Multimap<Enchantment, ItemStack> getAdditionalStacks() {
		if (additionalStacks == null)
			computeAdditionalStacks();
		return additionalStacks;
	}

	public static List<ItemStack> getTestItems() {
		if (testItems == null)
			computeTestItems();
		return testItems;
	}

	private static void computeTestItems() {
		testItems = Lists.newArrayList();

		for (String loc : ImprovedTooltipsModule.enchantingStacks) {
			Item item = Registry.ITEM.get(new ResourceLocation(loc));
			if (item != null)
				testItems.add(new ItemStack(item));
		}
	}

	private static void computeAdditionalStacks() {
		additionalStacks = HashMultimap.create();

		for(String s : ImprovedTooltipsModule.enchantingAdditionalStacks) {
			if(!s.contains("="))
				continue;

			String[] tokens = s.split("=");
			String left = tokens[0];
			String right = tokens[1];

			Enchantment ench = Registry.ENCHANTMENT.get(new ResourceLocation(left));
			if(ench != null) {
				tokens = right.split(",");

				for(String itemId : tokens) {
					Item item = Registry.ITEM.get(new ResourceLocation(itemId));
					if (item != null)
						additionalStacks.put(ench, new ItemStack(item));
				}
			}
		}
	}

	@ClientOnly
	public record EnchantedBookComponent(int width, int height,
										 Enchantment enchantment, boolean tableOnly) implements ClientTooltipComponent, TooltipComponent {

		@Override
		public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull PoseStack basePose, @NotNull ItemRenderer itemRenderer, int something) {
			PoseStack modelviewPose = RenderSystem.getModelViewStack();

			modelviewPose.pushPose();
			modelviewPose.translate(tooltipX, tooltipY, 0);
			modelviewPose.scale(0.5f, 0.5f, 1.0f);
			Minecraft mc = Minecraft.getInstance();
			List<ItemStack> items = getItemsForEnchantment(enchantment, tableOnly);
			int drawn = 0;
			for (ItemStack testStack : items) {
				mc.getItemRenderer().renderGuiItem(testStack, 6 + (drawn % 10) * 18, (drawn / 10) * 20);
				drawn++;
			}

			modelviewPose.popPose();
			RenderSystem.applyModelViewMatrix();
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public int getWidth(@NotNull Font font) {
			return width;
		}

	}
}
