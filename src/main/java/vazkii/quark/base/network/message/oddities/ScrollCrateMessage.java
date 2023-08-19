package vazkii.quark.base.network.message.oddities;

import java.io.Serial;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;
import vazkii.quark.addons.oddities.inventory.CrateMenu;

public class ScrollCrateMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -921358009630134620L;

	public boolean down;

	public ScrollCrateMessage() { }

	public ScrollCrateMessage(boolean down) {
		this.down = down;
	}

	@Override
	public void receive(NetworkContext context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.sender();
			AbstractContainerMenu container = player.containerMenu;

			if(container instanceof CrateMenu crate)
				crate.scroll(down, false);
		});
	}

}
