package vazkii.quark.base.network.message.oddities;

import java.io.Serial;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;
import vazkii.quark.addons.oddities.block.be.MatrixEnchantingTableBlockEntity;
import vazkii.quark.addons.oddities.inventory.MatrixEnchantingMenu;

public class MatrixEnchanterOperationMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = 2272401655489445173L;

	public int operation;
	public int arg0, arg1, arg2;

	public MatrixEnchanterOperationMessage() { }

	public MatrixEnchanterOperationMessage(int operation, int arg0, int arg1, int arg2) {
		this.operation = operation;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	@Override
	public void receive(NetworkContext context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.sender();
			AbstractContainerMenu container = player.containerMenu;

			if(container instanceof MatrixEnchantingMenu matrixMenu) {
				MatrixEnchantingTableBlockEntity enchanter = matrixMenu.enchanter;
				enchanter.onOperation(player, operation, arg0, arg1, arg2);
			}
		});
	}

}
