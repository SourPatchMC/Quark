package vazkii.quark.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import vazkii.quark.base.proxy.CommonProxy;

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

//		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		proxy.start();
	}
}
