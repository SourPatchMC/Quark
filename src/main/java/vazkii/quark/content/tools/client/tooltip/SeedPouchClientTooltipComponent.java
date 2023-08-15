package vazkii.quark.content.tools.client.tooltip;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.content.tools.item.SeedPouchItem;

import org.jetbrains.annotations.NotNull;

@ClientOnly
public class SeedPouchClientTooltipComponent implements ClientTooltipComponent {

	private final ItemStack stack;
	private int width;

	public SeedPouchClientTooltipComponent(ItemStack stack) {
		this.stack = stack;

		Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
		if(contents != null) {
			ItemStack seed = contents.getLeft().copy();
			int count = contents.getRight();
			int stacks = Math.max(1, (count - 1) / seed.getMaxStackSize() + 1);

			width = stacks * 8 + 8;
		}
	}

	@Override
	public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull PoseStack pose, @NotNull ItemRenderer itemRenderer, int something) {
		Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
		if(contents != null) {
			ItemStack seed = contents.getLeft().copy();

			Minecraft mc = Minecraft.getInstance();
			ItemRenderer render = mc.getItemRenderer();

			int count = contents.getRight();
			int stacks = Math.max(1, (count - 1) / seed.getMaxStackSize() + 1);

			for(int i = 0; i < stacks; i++) {
				if(i == (stacks - 1))
					seed.setCount(count);

				int x = tooltipX + 8 * i;
				int y = tooltipY + (int) (Math.sin(i * 498543) * 2);

				render.renderAndDecorateItem(seed, x, y);
				render.renderGuiItemDecorations(mc.font, seed, x, y);
			}
		}
	}

	@Override
	public int getHeight() {
		return width == 0 ? 0 : 20;
	}

	@Override
	public int getWidth(@NotNull Font font) {
		return width;
	}

}
