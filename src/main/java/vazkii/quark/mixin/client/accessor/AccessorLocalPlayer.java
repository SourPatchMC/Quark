package vazkii.quark.mixin.client.accessor;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface AccessorLocalPlayer {
	@Invoker("buildSignedContent")
	ChatMessageContent quark$buildSignedContent(String message, @Nullable Component component);

	@Invoker("createMessageSigner")
	MessageSigner quark$createMessageSigner();

	@Invoker("signMessage")
	MessageSignature quark$signMessage(MessageSigner signer, ChatMessageContent content, LastSeenMessages lastSeen);
}
