package vazkii.quark.addons.oddities.module;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddities.block.be.PipeBlockEntity;
import vazkii.quark.addons.oddities.block.pipe.EncasedPipeBlock;
import vazkii.quark.addons.oddities.block.pipe.PipeBlock;
import vazkii.quark.addons.oddities.client.render.be.PipeRenderer;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;

@LoadModule(category = ModuleCategory.ODDITIES)
public class PipesModule extends QuarkModule {

	public static BlockEntityType<PipeBlockEntity> blockEntityType;

	@Config(description = "How long it takes for an item to cross a pipe. Bigger = slower.") 
	private static int pipeSpeed = 5;
	
	@Config(description = "Set to 0 if you don't want pipes to have a max amount of items")
	public static int maxPipeItems = 16;
	
	@Config(description = "When items eject or are absorbed by pipes, should they make sounds?")
	public static boolean doPipesWhoosh = true;
	
	@Config(flag = "encased_pipes")
	public static boolean enableEncasedPipes = true;

	@Config public static boolean renderPipeItems = true;
	@Config public static boolean emitVibrations = true; 
	
	@Hint public static Block pipe;
	@Hint(key = "pipe", value = "encased_pipes")
	public static Block encasedPipe;
	
	public static TagKey<Block> pipesTag;
	
	public static int effectivePipeSpeed;
	
	@Override
	public void register() {
		pipe = new PipeBlock(this);
		encasedPipe = new EncasedPipeBlock(this);
		
		blockEntityType = BlockEntityType.Builder.of(PipeBlockEntity::new, pipe, encasedPipe).build(null);
		RegistryHelper.register(blockEntityType, "pipe", Registry.BLOCK_ENTITY_TYPE);
	}
	
	@Override
	public void setup() {
		pipesTag = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Quark.MOD_ID, "pipes"));
	}
	
	@Override
	public void configChanged() {
		effectivePipeSpeed = pipeSpeed * 2;
	}

	@Override
	@ClientOnly
	public void clientSetup() {
		BlockEntityRenderers.register(blockEntityType, PipeRenderer::new);
	}
	
	@Override
	@ClientOnly
	public void registerAdditionalModels(RegisterAdditional event) {
		event.register(new ModelResourceLocation(Quark.MOD_ID, "extra/pipe_flare", "inventory"));
	}
	
}

