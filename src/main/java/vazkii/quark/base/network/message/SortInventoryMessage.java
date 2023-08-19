package vazkii.quark.base.network.message;

import java.io.Serial;

import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;
import vazkii.quark.base.handler.SortingHandler;

public class SortInventoryMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -4340505435110793951L;

	public boolean forcePlayer;

	public SortInventoryMessage() { }

	public SortInventoryMessage(boolean forcePlayer) {
		this.forcePlayer = forcePlayer;
	}

	@Override
	public void receive(NetworkContext context) {
		context.enqueueWork(() -> SortingHandler.sortInventory(context.sender(), forcePlayer));
	}

}
