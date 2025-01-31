package vazkii.quark.content.tweaks.module;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class NoDurabilityOnCosmeticsModule extends QuarkModule {

	@Config(description = "Allow applying cosmetic items such as color runes with no anvil durability usage? Cosmetic items are defined in the quark:cosmetic_anvil_items tag") 
	private boolean allowCosmeticItems = true;
	
	public static TagKey<Item> cosmeticTag;

	@Override
	public void setup() {
		cosmeticTag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Quark.MOD_ID, "cosmetic_anvil_items"));
	}
	
	@SubscribeEvent
	public void onAnvilUse(AnvilRepairEvent event) {
		ItemStack right = event.getRight();
		
		if(right.isEmpty() || (allowCosmeticItems && right.is(cosmeticTag)))
			event.setBreakChance(0F);
	}
	
}
