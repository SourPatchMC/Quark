//package vazkii.quark.content.experimental.module;
//
//import com.mojang.blaze3d.platform.Window;
//import net.minecraft.client.Minecraft;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.BlockItem;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.Block;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.RenderGuiOverlayEvent;
//import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import vazkii.quark.base.module.LoadModule;
//import vazkii.quark.base.module.ModuleCategory;
//import vazkii.quark.base.module.QuarkModule;
//import vazkii.quark.base.module.config.Config;
//import vazkii.quark.content.tools.config.BlockSuffixConfig;
//import vazkii.quark.content.tools.item.SawItem;
//
//import java.util.Arrays;
//
//@LoadModule(category = ModuleCategory.EXPERIMENTAL, hasSubscriptions = true, enabledByDefault = false)
//public class SawModule extends QuarkModule {
//
//	@Config
//	public static BlockSuffixConfig variants = new BlockSuffixConfig(
//			Arrays.asList("slab", "stairs", "wall", "fence", "vertical_slab"),
//			Arrays.asList("quark"));
//
//	public static Item saw;
//
//	@Override
//	public void register() {
//		saw = new SawItem(this);
//	}
//
//	private String getSavedVariant(Player player) {
//		ItemStack offHand = player.getOffhandItem();
//		if(offHand.getItem() == saw)
//			return SawItem.getSavedVariant(offHand);
//
//		return "";
//	}
//
//	private Block getMainHandVariantBlock(Player player, String variant) {
//		ItemStack mainHand = player.getMainHandItem();
//		if(mainHand.getItem() instanceof BlockItem blockItem) {
//			Block block = blockItem.getBlock();
//			Block variantBlock = variants.getBlockForVariant(block, variant);
//			if(variantBlock != null)
//				return variantBlock;
//		}
//
//		return null;
//	}
//
//	@SubscribeEvent
//	@ClientOnly
//	public void onRender(RenderGuiOverlayEvent.Pre event) {
//		if(event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type())
//			return;
//
//		Minecraft mc = Minecraft.getInstance();
//		Player player = mc.player;
//		String savedVariant = getSavedVariant(player);
//
//		if(savedVariant != null && !savedVariant.isEmpty()) {
//			Block variantBlock = getMainHandVariantBlock(player, savedVariant);
//			if(variantBlock != null) {
//				ItemStack displayRight = new ItemStack(variantBlock);
//
//				Window window = event.getWindow();
//				int x = window.getGuiScaledWidth() / 2;
//				int y = window.getGuiScaledHeight() / 2 + 12;
//				int pad = 8;
//
//				ItemStack mainHand = player.getMainHandItem();
//				ItemStack displayLeft = mainHand.copy();
//				displayLeft.setCount(1);
//
//				mc.font.draw(event.getPoseStack(), "->", x - 5, y + 5, 0xFFFFFF);
//				mc.getItemRenderer().renderAndDecorateItem(displayLeft, x - 16 - pad, y);
//				mc.getItemRenderer().renderAndDecorateItem(displayRight, x + pad, y);
//			}
//		}
//	}
//
//}
