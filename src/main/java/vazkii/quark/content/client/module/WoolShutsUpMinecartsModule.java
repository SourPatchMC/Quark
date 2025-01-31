package vazkii.quark.content.client.module;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;

@LoadModule(category = ModuleCategory.CLIENT)
public class WoolShutsUpMinecartsModule extends QuarkModule {

	private static boolean staticEnabled;
	
	@Hint(key = "wool_muffling") TagKey<Item> dampeners = ItemTags.DAMPENS_VIBRATIONS;
	
	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	@ClientOnly
	public static boolean canPlay(AbstractMinecart cart) {
		return !staticEnabled || !cart.level.getBlockState(cart.blockPosition().below()).is(BlockTags.DAMPENS_VIBRATIONS);
	}
	
}
