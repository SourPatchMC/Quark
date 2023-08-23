package vazkii.quark.content.world.module;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.MaterialColor;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldLoadEvents;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.handler.WoodSetHandler.WoodSet;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.mixin.accessor.AccessorTreeConfiguration;

@LoadModule(category = ModuleCategory.WORLD, antiOverlap = { "caverns_and_chasms" })
public class AzaleaWoodModule extends QuarkModule {

	public static WoodSet woodSet;

	@Override
	public void register() {
		woodSet = WoodSetHandler.addWoodSet(this, "azalea", MaterialColor.COLOR_LIGHT_GREEN, MaterialColor.COLOR_BROWN, true);
		//ugly I know but config is fired before this now
		enabledStatusChanged(true, this.enabled, this.enabled);

		//fixme would this work? if not where would this go?
		ServerWorldLoadEvents.LOAD.register((server, level) -> overrideAzaleaTrunk(server));
	}

	@SuppressWarnings("unchecked")
	public static void overrideAzaleaTrunk(MinecraftServer server) {
		ConfiguredFeature<TreeConfiguration, ?> configured = null;
		try {
			configured = (ConfiguredFeature<TreeConfiguration, ?>) server.registryAccess()
					.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY)
					.getOrThrow((ResourceKey<ConfiguredFeature<?,?>>) TreeFeatures.AZALEA_TREE);
		} catch (IllegalStateException e) {
			Quark.LOG.error("Failed to replace Azalea trunk", e);
		}

		if (configured != null) {
			TreeConfiguration config = configured.config();
			((AccessorTreeConfiguration) config).setTrunkProvider(BlockStateProvider.simple(woodSet.log));
		}
	}
}
