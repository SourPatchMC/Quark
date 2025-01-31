package vazkii.quark.base.network.message.oddities;

import io.github.fabricators_of_create.porting_lib.util.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;
import vazkii.quark.addons.oddities.inventory.BackpackMenu;

import java.io.Serial;

public class HandleBackpackMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = 3474816381329541425L;

	public boolean open;

	public HandleBackpackMessage() { }

	public HandleBackpackMessage(boolean open) {
		this.open = open;
	}

	@Override
	public void receive(NetworkContext context) {
		ServerPlayer player = context.sender();
		context.enqueueWork(() -> {
			if(open) {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
				if(stack.getItem() instanceof MenuProvider && player.containerMenu != null) {
					ItemStack holding = player.containerMenu.getCarried().copy();
					player.containerMenu.setCarried(ItemStack.EMPTY);
					NetworkHooks.openScreen(player, (MenuProvider) stack.getItem(), player.blockPosition());
					player.containerMenu.setCarried(holding);
				}
			} else {
				if(player.containerMenu != null) {
					ItemStack holding = player.containerMenu.getCarried();
					player.containerMenu.setCarried(ItemStack.EMPTY);

					BackpackMenu.saveCraftingInventory(player);
					player.containerMenu = player.inventoryMenu;
					player.inventoryMenu.setCarried(holding);
				}
			}
		});
	}

}
