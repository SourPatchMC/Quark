package vazkii.quark.base.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.content.building.block.VerticalSlabBlock;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class FuelHandler {

	private static final Map<Item, Integer> fuelValues = new HashMap<>();

	public static void addFuel(Item item, int fuel) {
		if(fuel > 0 && item != null && !fuelValues.containsKey(item))
			fuelValues.put(item, fuel);
	}

	public static void addFuel(Block block, int fuel) {
		addFuel(block.asItem(), fuel);
	}

	public static void addWood(Block block) {
		String regname = Objects.toString(RegistryHelper.getRegistryName(block, Registry.BLOCK));
		if(regname.contains("crimson") || regname.contains("warped"))
			return; //do nothing if block is crimson or warped, since they aren't flammable. #3549
		if(block instanceof VerticalSlabBlock || block instanceof SlabBlock)
			addFuel(block, 150);
		else addFuel(block, 300);
	}

	public static void addAllWoods() {
		for(Block block : Registry.BLOCK) {
			ResourceLocation regname = RegistryHelper.getRegistryName(block, Registry.BLOCK);
			if(block != null && regname.getNamespace().equals(Quark.MOD_ID) && block.defaultBlockState().getMaterial() == Material.WOOD)
				addWood(block);
		}
	}

	@SubscribeEvent
	public static void getFuel(FurnaceFuelBurnTimeEvent event) {
		Item item = event.getItemStack().getItem();
		if(fuelValues.containsKey(item))
			event.setBurnTime(fuelValues.get(item));
	}
}
