package vazkii.quark.base.network.message;


import java.io.Serial;

import vazkii.arl.network.IMessage;
import vazkii.arl.quilt.NetworkContext;
import vazkii.quark.content.tweaks.module.LockRotationModule;
import vazkii.quark.content.tweaks.module.LockRotationModule.LockProfile;

public class SetLockProfileMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = 1037317801540162515L;

	public LockProfile profile;

	public SetLockProfileMessage() { }

	public SetLockProfileMessage(LockProfile profile) {
		this.profile = profile;
	}

	@Override
	public void receive(NetworkContext context) {
		context.enqueueWork(() -> LockRotationModule.setProfile(context.sender(), profile));
	}

}
