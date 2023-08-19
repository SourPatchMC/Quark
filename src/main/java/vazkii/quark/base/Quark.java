package vazkii.quark.base;

import net.fabricmc.api.EnvType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import vazkii.quark.base.proxy.ClientProxy;
import vazkii.quark.base.proxy.CommonProxy;

import java.util.Objects;

//@Mod(Quark.MOD_ID)
public class Quark implements ModInitializer {

	public static final String MOD_ID = "quark";
	public static final String ODDITIES_ID = "quarkoddities";
	
	public static Quark instance;
	public static CommonProxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize(ModContainer mod) {
		instance = this;

		// todo: Migrate the proxies over to initialization stuff. Client should not be referenced here as well.
        if (Objects.requireNonNull(MinecraftQuiltLoader.getEnvironmentType()) == EnvType.CLIENT) {
            proxy = new ClientProxy();
        } else {
            proxy = new CommonProxy();
        }

		proxy.start();
	}
}
