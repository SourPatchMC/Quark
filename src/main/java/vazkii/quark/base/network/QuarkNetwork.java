package vazkii.quark.base.network;

import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import vazkii.arl.network.IMessage;
import vazkii.arl.network.MessageSerializer;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.util.NetworkDirection;
import vazkii.quark.base.network.message.*;
import vazkii.quark.base.network.message.oddities.HandleBackpackMessage;
import vazkii.quark.base.network.message.oddities.MatrixEnchanterOperationMessage;
import vazkii.quark.base.network.message.oddities.ScrollCrateMessage;

import java.time.Instant;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;

public final class QuarkNetwork {

	private static NetworkHandler network;

	public static void setup() {
		MessageSerializer.mapHandlers(Instant.class, (buf, field) -> buf.readInstant(), (buf, field, instant) -> buf.writeInstant(instant));
		MessageSerializer.mapHandlers(MessageSignature.class, (buf, field) -> new MessageSignature(buf), (buf, field, signature) -> signature.write(buf));
		MessageSerializer.mapHandlers(LastSeenMessages.Update.class, (buf, field) -> new LastSeenMessages.Update(buf), (buf, field, update) -> update.write(buf));

		network.register(SortInventoryMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(InventoryTransferMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(DoubleDoorMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(HarvestMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(RequestEmoteMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(ChangeHotbarMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(SetLockProfileMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(ShareItemMessage.class, NetworkDirection.PLAY_TO_SERVER);

		network.register(HandleBackpackMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(MatrixEnchanterOperationMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(ScrollCrateMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(ScrollOnBundleMessage.class, NetworkDirection.PLAY_TO_SERVER);

		network.register(DoEmoteMessage.class, NetworkDirection.PLAY_TO_CLIENT);
		network.register(EditSignMessage.class, NetworkDirection.PLAY_TO_CLIENT);
		network.register(UpdateTridentMessage.class, NetworkDirection.PLAY_TO_CLIENT);
	}

	public static void sendToPlayer(IMessage msg, ServerPlayer player) {
		network.sendToPlayer(msg, player);
	}

	public static void sendToServer(IMessage msg) {
		network.sendToServer(msg);
	}

	public static void sendToPlayers(IMessage msg, Iterable<ServerPlayer> players) {
		for(ServerPlayer player : players)
			network.sendToPlayer(msg, player);
	}

	public static void sendToAllPlayers(IMessage msg, MinecraftServer server) {
		sendToPlayers(msg, server.getPlayerList().getPlayers());
	}

	public static Packet<?> toVanillaPacket(IMessage msg, NetworkDirection direction) {
		if (direction == NetworkDirection.PLAY_TO_CLIENT)
			return network.channel.createVanillaPacket((S2CPacket) msg);
		return network.channel.createVanillaPacket((C2SPacket) msg);
	}

}
