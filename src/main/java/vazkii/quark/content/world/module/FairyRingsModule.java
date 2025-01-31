package vazkii.quark.content.world.module;

import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.quark.base.world.WorldGenWeights;
import vazkii.quark.content.world.gen.FairyRingGenerator;

import java.util.ArrayList;
import java.util.List;

@LoadModule(category = ModuleCategory.WORLD)
public class FairyRingsModule extends QuarkModule {

	@Config public static double forestChance = 0.00625;
	@Config public static double plainsChance = 0.0025;
	@Config public static DimensionConfig dimensions = new DimensionConfig(false, "minecraft:overworld");

	@Config(name = "Ores")
	public static List<String> oresRaw = Lists.newArrayList("minecraft:emerald_ore", "minecraft:diamond_ore");

	public static List<BlockState> ores;

	@Override
	public void setup() {
		WorldGenHandler.addGenerator(this, new FairyRingGenerator(dimensions), Decoration.TOP_LAYER_MODIFICATION, WorldGenWeights.FAIRY_RINGS);
	}

	@Override
	public void configChanged() {
		ores = new ArrayList<>();
		for(String s : oresRaw) {
			Block b = Registry.BLOCK.get(new ResourceLocation(s));
			if (b != null) {
				ores.add(b.defaultBlockState());
			}
			else {
				new IllegalArgumentException("Block " + s + " does not exist!").printStackTrace();
			}
		}
	}

}
