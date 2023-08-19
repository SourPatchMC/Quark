package vazkii.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.io.Serial;

import org.quiltmc.loader.api.minecraft.ClientOnly;

import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;

public class UpdateTridentMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -4716987873031723456L;

	public int tridentID;
	public ItemStack stack;

	public UpdateTridentMessage() { }

	public UpdateTridentMessage(int trident, ItemStack stack) {
		this.tridentID = trident;
		this.stack = stack;
	}

	@Override
	@ClientOnly
	public void receive(NetworkContext context) {
		context.enqueueWork(() -> {
			Level level = Minecraft.getInstance().level;
			if (level != null) {
				Entity entity = level.getEntity(tridentID);
				if (entity instanceof ThrownTrident trident) {
					trident.tridentItem = stack;
				}
			}
		});
	}

}
