package vazkii.quark.addons.oddities.module;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.ModelEvent;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddities.block.TinyPotatoBlock;
import vazkii.quark.addons.oddities.block.be.TinyPotatoBlockEntity;
import vazkii.quark.addons.oddities.client.model.TinyPotatoModel;
import vazkii.quark.addons.oddities.client.render.be.TinyPotatoRenderer;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@LoadModule(category = ModuleCategory.ODDITIES, antiOverlap = "botania", hasSubscriptions = true, subscribeOn = EnvType.CLIENT)
public class TinyPotatoModule extends QuarkModule {

	public static BlockEntityType<TinyPotatoBlockEntity> blockEntityType;
	public static QuarkGenericTrigger patPotatoTrigger;

	@Hint public static Block tiny_potato;

	@Config(description = "Set this to true to use the recipe without the Heart of Diamond, even if the Heart of Diamond is enabled.", flag = "tiny_potato_never_uses_heart")
	public static boolean neverUseHeartOfDiamond = false;

	@Override
	public void register() {
		tiny_potato = new TinyPotatoBlock(this);

		blockEntityType = BlockEntityType.Builder.of(TinyPotatoBlockEntity::new, tiny_potato).build(null);
		RegistryHelper.register(blockEntityType, "tiny_potato", Registry.BLOCK_ENTITY_TYPE);

		patPotatoTrigger = QuarkAdvancementHandler.registerGenericTrigger("pat_potato");
	}

	@Override
	@ClientOnly
	public void modelBake(ModelManager manager, Map<ResourceLocation, BakedModel> models, ModelBakery loader) {
		ResourceLocation tinyPotato = new ModelResourceLocation(new ResourceLocation("quark", "tiny_potato"), "inventory");
        BakedModel originalPotato = models.get(tinyPotato);
		models.put(tinyPotato, new TinyPotatoModel(originalPotato));
	}
	
	@Override
	@ClientOnly
	public void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		Set<String> usedNames = new HashSet<>(); 

		// Register bosnia taters in packs afterwards so that quark overrides for quark tater
		registerTaters(event, "quark", usedNames, rm);
		registerTaters(event, "botania", usedNames, rm);
	}

	@ClientOnly
	private void registerTaters(ModelEvent.RegisterAdditional event, String mod, Set<String> usedNames, ResourceManager rm) {
		Map<ResourceLocation, Resource> resources = rm.listResources("models/tiny_potato", r -> r.getPath().endsWith(".json")); 
		for (ResourceLocation model : resources.keySet()) {
			if (mod.equals(model.getNamespace())) {
				String path = model.getPath();
				if ("models/tiny_potato/base.json".equals(path) || usedNames.contains(path))
					continue;

				usedNames.add(path);

				path = path.substring("models/".length(), path.length() - ".json".length());
				event.register(new ResourceLocation("quark", path));
			}
		}
	}

	@Override
	@ClientOnly
	public void clientSetup() {
		BlockEntityRenderers.register(blockEntityType, TinyPotatoRenderer::new);
	}
}
