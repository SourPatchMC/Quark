package vazkii.quark.base.network.message;

import java.io.Serial;

import net.minecraft.server.level.ServerPlayer;

import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.network.QuarkNetwork;

public class RequestEmoteMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -8569122937119059414L;

	public String emote;

	public RequestEmoteMessage() { }

	public RequestEmoteMessage(String emote) {
		this.emote = emote;
	}

	@Override
	public void receive(NetworkContext context) {
		ServerPlayer player = context.sender();
		if (player != null && player.server != null)
			context.enqueueWork(() -> QuarkNetwork.sendToAllPlayers(
					new DoEmoteMessage(emote, player.getUUID(), ContributorRewardHandler.getTier(player)),
					player.server));
	}

}
