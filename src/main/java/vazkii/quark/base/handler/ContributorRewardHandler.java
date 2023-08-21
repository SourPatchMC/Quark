package vazkii.quark.base.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.loader.api.minecraft.DedicatedServerOnly;
import vazkii.quark.base.Quark;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

//@Mod.EventBusSubscriber(modid = Quark.MOD_ID)
public class ContributorRewardHandler {
	private static Thread thread;
	private static String name;

	private static final Map<String, Integer> tiers = new HashMap<>();

	public static int localPatronTier = 0;
	public static String featuredPatron = "N/A";

	@ClientOnly
	public static void getLocalName() {
		name = Minecraft.getInstance().getUser().getName().toLowerCase(Locale.ROOT);
	}

	public static void init() {
		if (thread != null && thread.isAlive())
			return;

		thread = new ThreadContributorListLoader();
	}

	public static int getTier(Player player) {
		return getTier(player.getGameProfile().getName());
	}

	public static int getTier(String name) {
		return tiers.getOrDefault(name.toLowerCase(Locale.ROOT), 0);
	}

	// Moved to Dev capes to src/main/java/vazkii/quark/mixin/client/PlayerInfoMixin.java

	@SubscribeEvent
	@DedicatedServerOnly
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		ContributorRewardHandler.init();
	}

	private static void load(Properties props) {
		List<String> allPatrons = new ArrayList<>(props.size());

		props.forEach((k, v) -> {
			String key = (String) k;
			String value = (String) v;

			int tier = Integer.parseInt(value);
			if(tier < 10)
				allPatrons.add(key);
			tiers.put(key.toLowerCase(Locale.ROOT), tier);

			if(key.toLowerCase(Locale.ROOT).equals(name))
				localPatronTier = tier;
		});

		if(!allPatrons.isEmpty())
			featuredPatron = allPatrons.get((int) (Math.random() * allPatrons.size()));
	}

	private static class ThreadContributorListLoader extends Thread {

		public ThreadContributorListLoader() {
			setName("Quark Contributor Loading Thread");
			setDaemon(true);
			start();
		}

		@Override
		public void run() {
			try {
				URL url = new URL("https://raw.githubusercontent.com/VazkiiMods/Quark/master/contributors.properties");
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(10*1000);
				conn.setReadTimeout(10*1000);

				Properties patreonTiers = new Properties();
				try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
					patreonTiers.load(reader);
					load(patreonTiers);
				}
			} catch (IOException e) {
				Quark.LOG.error("Failed to load patreon information", e);
			}
		}

	}

}
