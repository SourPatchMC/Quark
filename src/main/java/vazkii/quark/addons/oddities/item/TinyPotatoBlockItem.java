package vazkii.quark.addons.oddities.item;

import java.util.List;

import io.github.fabricators_of_create.porting_lib.extensions.ItemExtensions;
import io.github.fabricators_of_create.porting_lib.item.EquipmentItem;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.addons.oddities.block.TinyPotatoBlock;
import vazkii.quark.addons.oddities.block.be.TinyPotatoBlockEntity;
import vazkii.quark.addons.oddities.util.TinyPotatoInfo;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.base.handler.ContributorRewardHandler;

public class TinyPotatoBlockItem extends BlockItem implements IRuneColorProvider, Wearable, EquipmentItem {
	private static final int NOT_MY_NAME = 17;
	private static final List<String> TYPOS = List.of("vaskii", "vazki", "voskii", "vazkkii", "vazkki", "vazzki", "vaskki", "vozkii", "vazkil", "vaskil", "vazkill", "vaskill", "vaski");

	private static final String TICKS = "notMyNameTicks";

	public TinyPotatoBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	// canEquip() has been moved to InventoryMenuMixin and LivingEntityMixin.

	@NotNull
	@Override
	public String getDescriptionId(@NotNull ItemStack stack) {
		if (TinyPotatoBlock.isAngry(stack))
			return super.getDescriptionId(stack) + ".angry";
		return super.getDescriptionId(stack);
	}

	private void updateData(ItemStack stack) {
		if (ItemNBTHelper.verifyExistence(stack, "BlockEntityTag")) {
			CompoundTag cmp = ItemNBTHelper.getCompound(stack, "BlockEntityTag", true);
			if (cmp != null) {
				if (cmp.contains(TinyPotatoBlockEntity.TAG_ANGRY, Tag.TAG_ANY_NUMERIC)) {
					boolean angry = cmp.getBoolean(TinyPotatoBlockEntity.TAG_ANGRY);
					if (angry)
						ItemNBTHelper.setBoolean(stack, TinyPotatoBlock.ANGRY, true);
					else if (TinyPotatoBlock.isAngry(stack))
						ItemNBTHelper.getNBT(stack).remove(TinyPotatoBlock.ANGRY);
					cmp.remove(TinyPotatoBlockEntity.TAG_ANGRY);
				}

				if (cmp.contains(TinyPotatoBlockEntity.TAG_NAME, Tag.TAG_STRING)) {
					stack.setHoverName(Component.Serializer.fromJson(cmp.getString(TinyPotatoBlockEntity.TAG_NAME)));
					cmp.remove(TinyPotatoBlockEntity.TAG_NAME);
				}
			}
		}

		if (!ItemNBTHelper.getBoolean(stack, TinyPotatoBlock.ANGRY, false))
			ItemNBTHelper.getNBT(stack).remove(TinyPotatoBlock.ANGRY);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		updateData(stack);
		return super.onEntityItemUpdate(stack, entity);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity holder, int itemSlot, boolean isSelected) {
		updateData(stack);

		if (!world.isClientSide && holder instanceof Player player && holder.tickCount % 30 == 0 && TYPOS.contains(ChatFormatting.stripFormatting(stack.getDisplayName().getString()))) {
			int ticks = ItemNBTHelper.getInt(stack, TICKS, 0);
			if (ticks < NOT_MY_NAME) {
				player.sendSystemMessage(Component.translatable("quark.misc.you_came_to_the_wrong_neighborhood." + ticks).withStyle(ChatFormatting.RED));
				ItemNBTHelper.setInt(stack, TICKS, ticks + 1);
			}
		}
	}

	@Override
	public boolean isFoil(@NotNull ItemStack stack) {
		if (stack.hasCustomHoverName() && TinyPotatoInfo.fromComponent(stack.getHoverName()).enchanted())
			return true;
		return super.isFoil(stack);
	}

	@Override
	public int getRuneColor(ItemStack stack) {
		if (stack.hasCustomHoverName())
			return TinyPotatoInfo.fromComponent(stack.getHoverName()).runeColor();
		return -1;
	}

	@Override
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.HEAD;
	}
}
