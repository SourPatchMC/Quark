package vazkii.quark.base.network.message;

import java.io.Serial;

import net.minecraft.core.BlockPos;
import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;
import vazkii.quark.content.tweaks.module.SignEditingModule;

public class EditSignMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -329145938273036832L;

	public BlockPos pos;

	public EditSignMessage() { }

	public EditSignMessage(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void receive(NetworkContext context) {
		context.enqueueWork(() -> SignEditingModule.openSignGuiClient(pos));
	}

}
