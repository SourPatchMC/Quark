package vazkii.quark.integration.lootr;

import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.client.event.TextureStitchEvent;

import org.jetbrains.annotations.Nullable;

/**
 * @author WireSegal
 * Created at 11:40 AM on 7/3/23.
 */
public interface ILootrIntegration {

	ILootrIntegration INSTANCE = Util.make(() -> {
		if (QuiltLoader.isModLoaded("lootr")) {
			return new LootrIntegration();
		}
		return new ILootrIntegration() {
			//NO-OP
		};
	});

	default BlockEntityType<? extends ChestBlockEntity> chestTE() {
		return null;
	}

	default BlockEntityType<? extends ChestBlockEntity> trappedChestTE() {
		return null;
	}

	@Nullable
	default Block lootrVariant(Block base) {
		return null;
	}

	default void postRegister() {
		// NO-OP
	}

	default void loadComplete() {
		// NO-OP
	}

	@ClientOnly
	default void clientSetup() {
		// NO-OP
	}

	@ClientOnly
	default void stitch(TextureStitchEvent.Pre event) {

	}
}
