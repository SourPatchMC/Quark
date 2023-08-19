package vazkii.quark.base.network.message;

import java.io.Serial;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;

public class ChangeHotbarMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -3942423443215625756L;

	public int bar;

	public ChangeHotbarMessage() { }

	public ChangeHotbarMessage(int bar) {
		this.bar = bar;
	}

	@Override
	public void receive(NetworkContext context) {
		context.enqueueWork(() -> {
			Player player = context.sender();

			if(bar > 0 && bar <= 3)
				for(int i = 0; i < 9; i++)
					swap(player.getInventory(), i, i + bar * 9);
		});
	}

	public void swap(Container inv, int slot1, int slot2) {
		ItemStack stack1 = inv.getItem(slot1);
		ItemStack stack2 = inv.getItem(slot2);
		inv.setItem(slot2, stack1);
		inv.setItem(slot1, stack2);
	}

}
