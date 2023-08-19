package vazkii.quark.content.tweaks.module;

import java.util.function.BiConsumer;

import io.github.fabricators_of_create.porting_lib.event.common.PlayerTickEvents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tweaks.client.item.ClockTimeGetter;
import vazkii.quark.content.tweaks.client.item.CompassAngleGetter;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class CompassesWorkEverywhereModule extends QuarkModule {

	@Config public static boolean enableCompassNerf = true;
	@Config(flag = "clock_nerf") 
	public static boolean enableClockNerf = true;

	@Config public static boolean enableNether = true;
	@Config public static boolean enableEnd = true;
	
	@Hint("clock_nerf") Item clock = Items.CLOCK;

	public CompassesWorkEverywhereModule() {
		super();
		PlayerTickEvents.START.register(this::onUpdate);
	}

	@Override
	@ClientOnly
	public void clientSetup() {
		// register = addPropertyOverride
		if(enabled && (enableCompassNerf || enableNether || enableEnd))
			enqueue(() -> ItemProperties.register(Items.COMPASS, new ResourceLocation("angle"), new CompassAngleGetter.Impl()));

		if(enabled && enableClockNerf)
			enqueue(() -> ItemProperties.register(Items.CLOCK, new ResourceLocation("time"), new ClockTimeGetter.Impl()));
	}
	
	@Override
	public void addAdditionalHints(BiConsumer<Item, Component> consumer) {
		if(!enableNether && !enableEnd && !enableCompassNerf)
			return;
		
		MutableComponent comp = Component.literal("");
		String pad = "";
		if(enableNether) {
			comp = comp.append(pad).append(Component.translatable("quark.jei.hint.compass_nether"));
			pad = " ";
		}
		if(enableEnd) {
			comp = comp.append(pad).append(Component.translatable("quark.jei.hint.compass_end"));
			pad = " ";
		}
		if(enableCompassNerf)
			comp = comp.append(pad).append(Component.translatable("quark.jei.hint.compass_nerf"));
		
		consumer.accept(Items.COMPASS, comp);
	}

	public void onUpdate(Player player) {
        Inventory inventory = player.getInventory();
        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if(stack.getItem() == Items.COMPASS)
                CompassAngleGetter.tickCompass(player, stack);
            else if(stack.getItem() == Items.CLOCK)
                ClockTimeGetter.tickClock(stack);
        }
    }

}
