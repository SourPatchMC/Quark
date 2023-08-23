package vazkii.quark.base.client.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.tooltip.api.client.ItemTooltipCallback;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;

@EventBusSubscriber(modid = Quark.MOD_ID, value = EnvType.CLIENT)
public class RequiredModTooltipHandler {

	private static final Map<Item, String> ITEMS = new HashMap<>();
	private static final Map<Block, String> BLOCKS = new HashMap<>();

	public static void map(Item item, String mod) {
		ITEMS.put(item, mod);
	}

	public static void map(Block block, String mod) {
		BLOCKS.put(block, mod);
	}

	public static List<ItemStack> disabledItems() {
		if(!GeneralConfig.hideDisabledContent)
			return new ArrayList<>();
		
		return ITEMS.entrySet().stream()
				.filter((entry) -> !QuiltLoader.isModLoaded(entry.getValue()))
				.map((entry) -> new ItemStack(entry.getKey()))
				.toList();
	}

	@ClientOnly
	public static void onTooltip(ItemStack itemStack, @Nullable Player player, TooltipFlag context, List<Component> lines) {
		if(!BLOCKS.isEmpty() && player != null && player.getLevel() != null) {
			for(Block b : BLOCKS.keySet())
				ITEMS.put(b.asItem(), BLOCKS.get(b));
			BLOCKS.clear();
		}


		Item item = itemStack.getItem();
		if(ITEMS.containsKey(item)) {
			String mod = ITEMS.get(item);
			if (!QuiltLoader.isModLoaded(mod)) {
				lines.add(Component.translatable("quark.misc.mod_disabled", mod).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	static {
		if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) ItemTooltipCallback.EVENT.register(RequiredModTooltipHandler::onTooltip);
	}
}
