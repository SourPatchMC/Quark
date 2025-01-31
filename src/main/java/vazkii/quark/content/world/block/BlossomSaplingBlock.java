package vazkii.quark.content.world.block;

import java.util.OptionalInt;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import vazkii.quark.base.block.QuarkSaplingBlock;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.world.module.BlossomTreesModule;

public class BlossomSaplingBlock extends QuarkSaplingBlock {

	public BlossomSaplingBlock(String colorName, QuarkModule module, BlossomTree tree) {
		super(colorName + "_blossom", module, tree);
		tree.sapling = this;
	}

	public static class BlossomTree extends AbstractTreeGrower {

		public final TreeConfiguration config;
		public final BlockState leaf;
		public BlossomSaplingBlock sapling;

		public BlossomTree(Block leafBlock) {
			config = (new TreeConfiguration.TreeConfigurationBuilder(
					BlockStateProvider.simple(BlossomTreesModule.woodSet.log),
					new FancyTrunkPlacer(8, 10, 10),
					BlockStateProvider.simple(leafBlock),
					new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), 4),
					new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))))
					.ignoreVines()
					.build();

			leaf = leafBlock.defaultBlockState();
		}

		@Override
		protected Holder<ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(@NotNull RandomSource rand, boolean hjskfsd) {
			return Holder.direct(new ConfiguredFeature<>(Feature.TREE, config));
		}

	}

}
