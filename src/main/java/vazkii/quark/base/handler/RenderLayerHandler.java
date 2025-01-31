package vazkii.quark.base.handler;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.fml.DistExecutor;

import java.util.HashMap;
import java.util.Map;

public class RenderLayerHandler {

	private static final Map<Block, RenderTypeSkeleton> mapping = new HashMap<>();
	private static final Map<Block, Block> inheritances = new HashMap<>();

	@ClientOnly
	private static Map<RenderTypeSkeleton, RenderType> renderTypes;

	public static void setRenderType(Block block, RenderTypeSkeleton skeleton) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> setRenderTypeClient(block, skeleton));
	}

	public static void setInherited(Block block, Block parent) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> setInheritedClient(block, parent));
	}

	@ClientOnly
	public static void init() {
		for(Block b : inheritances.keySet()) {
			Block inherit = inheritances.get(b);
			if(mapping.containsKey(inherit))
				mapping.put(b, mapping.get(inherit));
		}

		for(Block b : mapping.keySet())
			ItemBlockRenderTypes.setRenderLayer(b, renderTypes.get(mapping.get(b)));

		inheritances.clear();
		mapping.clear();
	}

	@ClientOnly
	private static void setRenderTypeClient(Block block, RenderTypeSkeleton skeleton) {
		resolveRenderTypes();
		mapping.put(block, skeleton);
	}

	@ClientOnly
	private static void setInheritedClient(Block block, Block parent) {
		resolveRenderTypes();
		inheritances.put(block, parent);
	}

	@ClientOnly
	private static void resolveRenderTypes() {
		if(renderTypes == null) {
			renderTypes = new HashMap<>();

			renderTypes.put(RenderTypeSkeleton.SOLID, RenderType.solid());
			renderTypes.put(RenderTypeSkeleton.CUTOUT, RenderType.cutout());
			renderTypes.put(RenderTypeSkeleton.CUTOUT_MIPPED, RenderType.cutoutMipped());
			renderTypes.put(RenderTypeSkeleton.TRANSLUCENT, RenderType.translucent());
		}
	}

	public enum RenderTypeSkeleton {

		SOLID,
		CUTOUT,
		CUTOUT_MIPPED,
		TRANSLUCENT

	}

}
