package vazkii.quark.content.world.module;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.QuarkLeavesBlock;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.handler.WoodSetHandler.WoodSet;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.handler.advancement.mod.BalancedDietModifier;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.Config.Min;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.world.block.AncientSaplingBlock;
import vazkii.quark.content.world.item.AncientFruitItem;

@LoadModule(category = ModuleCategory.WORLD, hasSubscriptions = true)
public class AncientWoodModule extends QuarkModule {

	@Config(flag = "ancient_fruit_xp")
	public static boolean ancientFruitGivesExp = true;

	@Config
	@Min(1)
	public static int ancientFruitExpValue = 10;

	@Config(description = "Set to 0 to disable loot chest generation")
	@Min(0)
	public static int ancientCityLootWeight = 8;

	@Config
	@Min(0)
	public static int ancientCityLootQuality = 1;

	public static WoodSet woodSet;
	public static Block ancient_leaves;
	@Hint public static Block ancient_sapling;
	@Hint public static Item ancient_fruit;

	public static QuarkGenericTrigger ancientFruitTrigger;

	public AncientWoodModule() {
		super();
		LootTableEvents.MODIFY.register(this::onLootTableLoad); // Choice 1 only
	}

	@Override
	public void setup() {
		enqueue(() -> {
			ComposterBlock.COMPOSTABLES.put(ancient_sapling.asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ancient_leaves.asItem(), 0.3F);
			ComposterBlock.COMPOSTABLES.put(ancient_fruit.asItem(), 0.65F);
		});
	}

	@Override
	public void register() {
		woodSet = WoodSetHandler.addWoodSet(this, "ancient", MaterialColor.TERRACOTTA_WHITE, MaterialColor.TERRACOTTA_WHITE, true);
		ancient_leaves = new QuarkLeavesBlock(woodSet.name, this, MaterialColor.PLANT);
		ancient_sapling = new AncientSaplingBlock(this);
		ancient_fruit = new AncientFruitItem(this);

		VariantHandler.addFlowerPot(ancient_sapling, RegistryHelper.getInternalName(ancient_sapling).getPath(), Functions.identity());

		QuarkAdvancementHandler.addModifier(new BalancedDietModifier(this, ImmutableSet.of(ancient_fruit)));

		ancientFruitTrigger = QuarkAdvancementHandler.registerGenericTrigger("ancient_fruit_overlevel");
	}

	public void onLootTableLoad(ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder, LootTableSource source) {
		int weight = 0;

		if(id.equals(BuiltInLootTables.ANCIENT_CITY))
			weight = ancientCityLootWeight;

		if(weight > 0) {
			LootPoolEntryContainer entry = LootItem.lootTableItem(ancient_sapling)
					.setWeight(weight)
					.setQuality(ancientCityLootQuality)
					.build();
			MiscUtil.addToLootTable(tableBuilder, entry);
		}
	}
}
