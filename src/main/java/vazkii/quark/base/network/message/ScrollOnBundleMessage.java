package vazkii.quark.base.network.message;


import java.io.Serial;

import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;
import vazkii.quark.content.management.module.ExpandedItemInteractionsModule;

public class ScrollOnBundleMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = 5598418693967300303L;

	public int containerId;
	public int stateId;
	public int slotNum;
	public double scrollDelta;

	public ScrollOnBundleMessage() {}

	public ScrollOnBundleMessage(int containerId, int stateId, int slotNum, double scrollDelta) {
		this.containerId = containerId;
		this.stateId = stateId;
		this.slotNum = slotNum;
		this.scrollDelta = scrollDelta;
	}

	@Override
	public void receive(NetworkContext context) {
		context.enqueueWork(() -> ExpandedItemInteractionsModule.scrollOnBundle(context.sender(), containerId, stateId, slotNum, scrollDelta));
	}

}
