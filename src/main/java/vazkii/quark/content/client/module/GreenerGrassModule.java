package vazkii.quark.content.client.module;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.inputtable.ConvulsionMatrixConfig;
import vazkii.quark.mixin.client.accessor.AccessorBlockColors;

@LoadModule(category = ModuleCategory.CLIENT)
public class GreenerGrassModule extends QuarkModule {

	private static final String[] GRASS_PRESET_NAMES = { "Dreary", "Vibrant" };
	private static final String GRASS_NAME = "Grass Colors";
	private static final String[] GRASS_BIOMES = { "plains", "forest", "mountains", "jungle", "savanna", "swamp" };
	private static final int[] GRASS_COLORS = { 0xff91bd59, 0xff79c05a, 0xff8ab689, 0xff59c93c, 0xffbfb755, 0xff6a7039 };
	private static final int[] FOLLIAGE_COLORS = { 0xff77ab2f, 0xff59ae30, 0xff6da36b, 0xff30bb0b, 0xffaea42a, 0xff6a7039 };
	private static final double[][] GRASS_PRESETS = {
			{
				1.24, 0.00, 0.00,
				0.00, 0.84, 0.00,
				0.00, 0.16, 0.36
			},
			{
				1.00, 0.00, 0.00,
				0.24, 1.00, 0.24,
				0.00, 0.00, 0.60
			}
	};
	private static final double[] GRASS_DEFAULT = {
			0.89, 0.00, 0.00,
			0.00, 1.11, 0.00,
			0.00, 0.00, 0.89
	};
	
	private static final String[] WATER_PRESET_NAMES = { "Muddy", "Colder" };
	private static final String WATER_NAME = "Water Colors";
	private static final String[] WATER_BIOMES = { "generic", "swamp", "meadow", "mangrove", "cold", "warm" }; 
	private static final int[] WATER_COLORS = { 0xff3f76e4, 0xff617B64, 0xff0e4ecf, 0xff3a7a6a, 0xff3d57D6, 0xff43d5ee };
	private static final double[][] WATER_PRESETS = {
			{
				0.76, 0.00, 0.10,
				0.00, 0.80, 0.00,
				0.00, 0.00, 0.70
			},
			{
				1.00, 0.00, 0.00,
				0.24, 0.96, 0.24,
				0.20, 0.52, 1.00
			}
	};
	private static final double[] WATER_DEFAULT = {
			0.86, 0.00, 0.00,
			0.00, 1.00, 0.22,
			0.00, 0.00, 1.22
	};
	
	private static ConvulsionMatrixConfig.Params GRASS_PARAMS = new ConvulsionMatrixConfig.Params(GRASS_NAME, GRASS_DEFAULT, GRASS_BIOMES, GRASS_COLORS, FOLLIAGE_COLORS, GRASS_PRESET_NAMES, GRASS_PRESETS);
	private static ConvulsionMatrixConfig.Params WATER_PARAMS = new ConvulsionMatrixConfig.Params(WATER_NAME, WATER_DEFAULT, WATER_BIOMES, WATER_COLORS, null, WATER_PRESET_NAMES, WATER_PRESETS);

	private static boolean staticEnabled = false;
	
	@Config public static boolean affectLeaves = true;
	@Config public static boolean affectWater = false;

	@Config public static List<String> blockList = Lists.newArrayList(
			"minecraft:large_fern",
			"minecraft:tall_grass",
			"minecraft:grass_block",
			"minecraft:fern",
			"minecraft:grass",
			"minecraft:potted_fern",
			"minecraft:sugar_cane",
			"environmental:giant_tall_grass",
			"valhelsia_structures:grass_block");

	@Config public static List<String> leavesList = Lists.newArrayList(
			"minecraft:spruce_leaves",
			"minecraft:birch_leaves",
			"minecraft:oak_leaves",
			"minecraft:jungle_leaves",
			"minecraft:acacia_leaves",
			"minecraft:dark_oak_leaves",
			"atmospheric:rosewood_leaves",
			"atmospheric:morado_leaves",
			"atmospheric:yucca_leaves",
			"autumnity:maple_leaves",
			"environmental:willow_leaves",
			"environmental:hanging_willow_leaves",
			"minecraft:vine");
	
	@Config public static ConvulsionMatrixConfig colorMatrix = new ConvulsionMatrixConfig(GRASS_PARAMS);
	@Config public static ConvulsionMatrixConfig waterMatrix = new ConvulsionMatrixConfig(WATER_PARAMS);

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	@Override
	public void firstClientTick() {
		registerGreenerColor(blockList, colorMatrix, () -> true);
		registerGreenerColor(leavesList, colorMatrix,() -> affectLeaves);
	}

	@ClientOnly
	private void registerGreenerColor(Iterable<String> ids, ConvulsionMatrixConfig config, Supplier<Boolean> condition) {
		BlockColors colors = Minecraft.getInstance().getBlockColors();

		// Can't be AT'd as it's changed by forge
		// Todo: Well it can be AW'd in quilt huh.
		IdMapper<BlockColor> colorMap = ((AccessorBlockColors) colors).quark$getBlockColors();

		for (String id : ids) {
			Block block = Registry.BLOCK.get(new ResourceLocation(id));
			if (block != null) {
				int blockID = Registry.BLOCK.getId(block);
				BlockColor color = colorMap.byId(blockID);
				if (color != null)
					colors.register(getConvulsedColor(config, color, condition), block);
			}
		}
	}

	@ClientOnly
	private BlockColor getConvulsedColor(ConvulsionMatrixConfig config, BlockColor color, Supplier<Boolean> condition) {
		return (state, world, pos, tintIndex) -> {
			int originalColor = color.getColor(state, world, pos, tintIndex);
			if(!enabled || !condition.get())
				return originalColor;

			return colorMatrix.convolve(originalColor);
		};
	}
	
	public static int getWaterColor(int currColor) {
		if(!staticEnabled || !affectWater)
			return currColor;
		
		return waterMatrix.convolve(currColor);
	}

}
