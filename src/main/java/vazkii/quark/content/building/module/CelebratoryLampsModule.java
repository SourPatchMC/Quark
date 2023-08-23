package vazkii.quark.content.building.module;

import net.fabricmc.api.EnvType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.tooltip.api.client.ItemTooltipCallback;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import java.util.List;

@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true, subscribeOn = EnvType.CLIENT)
public class CelebratoryLampsModule extends QuarkModule {

	@Config
	public static int lightLevel = 15;
	
	private static Block stone_lamp, stone_brick_lamp;

	public CelebratoryLampsModule() {
		super();
		ItemTooltipCallback.EVENT.register(this::onTooltip);
	}

	@Override
	public void register() {
		stone_lamp = new QuarkBlock("stone_lamp", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.STONE).lightLevel(s -> lightLevel));
		stone_brick_lamp = new QuarkBlock("stone_brick_lamp", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.STONE_BRICKS).lightLevel(s -> lightLevel));
	}


	@ClientOnly
	public void onTooltip(ItemStack itemStack, @Nullable Player player, TooltipFlag context, List<Component> lines) {
		if(context.isAdvanced()) {
			Item item = itemStack.getItem();
			if(item == stone_lamp.asItem() || item == stone_brick_lamp.asItem())
				lines.add(1, Component.translatable("quark.misc.celebration").withStyle(ChatFormatting.GRAY));
		}
	}
	
}
