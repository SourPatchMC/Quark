package vazkii.quark.base.module;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.api.event.ModuleStateChangedEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.quark.base.module.hint.HintObject;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class QuarkModule {

	public ModuleCategory category = null;
	public String displayName = "";
	public String lowercaseName = "";
	public String description = "";
	public List<String> antiOverlap = null;
	public boolean hasSubscriptions = false;
	public List<EnvType> subscriptionTarget = Lists.newArrayList(EnvType.CLIENT, EnvType.SERVER);
	public boolean enabledByDefault = true;
	public boolean missingDep = false;
	public List<HintObject> hints = Lists.newArrayList();

	private boolean firstLoad = true;
	public boolean enabled = false;
	public boolean disabledByOverlap = false;
	public boolean configEnabled = false;
	public boolean ignoreAntiOverlap = false;

	public QuarkModule() {
		// yep
	}

	public void construct() {
		// NO-OP
	}

	@ClientOnly
	public void constructClient() {
		// NO-OP
	}

	public void register() {
		// NO-OP
	}

	public void postRegister() {
		// NO-OP
	}

	public void configChanged() {
		// NO-OP
	}

	public void enabledStatusChanged(boolean firstLoad, boolean oldStatus, boolean newStatus) {
		// NO-OP
	}

	@ClientOnly
	public void configChangedClient() {
		// NO-OP
	}

	public void setup() {
		// NO-OP
	}

	@ClientOnly
	public void registerReloadListeners(Consumer<PreparableReloadListener> manager) {
		// NO-OP
	}

	@ClientOnly
	public void clientSetup() {
		// NO-OP
	}

	@ClientOnly
	public void modelBake(ModelEvent.BakingCompleted event) {
		// NO-OP
	}

	@ClientOnly
	public void modelLayers(EntityRenderersEvent.AddLayers event) {
		// NO-OP
	}

	@ClientOnly
	public void textureStitch(TextureStitchEvent.Pre event) {
		// NO-OP
	}

	@ClientOnly
	public void postTextureStitch(TextureStitchEvent.Post event) {
		// NO-OP
	}

	@ClientOnly
	public void registerKeybinds(RegisterKeyMappingsEvent event) {
		// NO-OP
	}

	@ClientOnly
	public void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		// NO-OP
	}

	@ClientOnly
	public void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		// NO-OP
	}

	public void loadComplete() {
		// NO-OP
	}

	public final void addStackInfo(BiConsumer<Item, Component> consumer) {
		if(!enabled)
			return;
		
		for(HintObject hint : hints)
			hint.apply(consumer);
		addAdditionalHints(consumer);
	}

	public void addAdditionalHints(BiConsumer<Item, Component> consumer) {

	}	

	@ClientOnly
	public void firstClientTick() {
		// NO-OP
	}

	public void pushFlags(ConfigFlagManager manager) {
		// NO-OP
	}

	protected void enqueue(Runnable r) {
		ModuleLoader.INSTANCE.enqueue(r);
	}

	public final void setEnabled(boolean enabled) {
		configEnabled = enabled;
		if(firstLoad) {
			Quark.LOG.info("Loading Module " + displayName);
			//MinecraftForge.EVENT_BUS.post(new ModuleLoadedEvent(lowercaseName));
		}

		disabledByOverlap = false;
		if(missingDep) enabled = false;
		else if(!ignoreAntiOverlap && antiOverlap != null) {
			for(String s : antiOverlap)
				if(QuiltLoader.getAllMods().contains(s)) {
					disabledByOverlap = true;
					enabled = false;
					break;
				}
		}

		setEnabledAndManageSubscriptions(firstLoad, enabled);
		firstLoad = false;
	}

	private void setEnabledAndManageSubscriptions(boolean firstLoad, boolean enabled) {
		if(MinecraftForge.EVENT_BUS.post(new ModuleStateChangedEvent(lowercaseName, enabled)))
			enabled = false;

		boolean wasEnabled = this.enabled;
		this.enabled = enabled;

		boolean changed = wasEnabled != enabled;

		if(changed) {
			if(hasSubscriptions && subscriptionTarget.contains(FMLEnvironment.dist)) {
				if(enabled)
					MinecraftForge.EVENT_BUS.register(this);
				else if(!firstLoad)
					MinecraftForge.EVENT_BUS.unregister(this);
			}

			enabledStatusChanged(firstLoad, wasEnabled, enabled);
		}
	}

}
