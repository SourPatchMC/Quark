package vazkii.quark.base.module;

import net.fabricmc.api.EnvType;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

public enum SubscriptionTarget {

	BOTH_SIDES(true, true),
	CLIENT_ONLY(true, false),
	SERVER_ONLY(false, true),
	NONE(false, false);

	SubscriptionTarget(boolean client, boolean server) {
		this.client = client;
		this.server = server;
	}

	private final boolean client;
	private final boolean server;

	public boolean shouldSubscribe() {
		return MinecraftQuiltLoader.getEnvironmentType().equals(EnvType.CLIENT) ? client : server;
	}

	public static SubscriptionTarget fromString(String s) {
		for(SubscriptionTarget target : values())
			if(target.name().equals(s))
				return target;

		return null;
	}


}
