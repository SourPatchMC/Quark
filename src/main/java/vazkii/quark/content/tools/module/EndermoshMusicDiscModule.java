package vazkii.quark.content.tools.module;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkMusicDiscItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class EndermoshMusicDiscModule extends QuarkModule {

	@Config private boolean playEndermoshDuringEnderdragonFight = false;

	@Config private boolean addToEndCityLoot = true;
	@Config private int lootWeight = 5;
	@Config private int lootQuality = 1;

	@Hint public static QuarkMusicDiscItem endermosh;

	@ClientOnly private boolean isFightingDragon;
	@ClientOnly private int delay;
	@ClientOnly private SimpleSoundInstance sound;

	public EndermoshMusicDiscModule() {
		super();
		LootTableEvents.MODIFY.register(this::onLootTableLoad);
		ClientTickEvents.END.register(this::tick);
	}

	@Override
	public void register() {
		endermosh = new QuarkMusicDiscItem(14, () -> QuarkSounds.MUSIC_ENDERMOSH, "endermosh", this, 3783); // Tick length calculated from endermosh.ogg - 3:09.150
	}

	public void onLootTableLoad(ResourceManager resourceManager, LootTables lootManager, ResourceLocation res, LootTable.Builder tableBuilder, LootTableSource source) {
		if(addToEndCityLoot) {
			if(res.equals(BuiltInLootTables.END_CITY_TREASURE)) {
				LootPoolEntryContainer entry = LootItem.lootTableItem(endermosh)
						.setWeight(lootWeight)
						.setQuality(lootQuality)
						.build();

				MiscUtil.addToLootTable(LootTable.lootTable().build(), entry);
			}
		}
	}

	@ClientOnly
	public void tick(Minecraft mc) {
		if (playEndermoshDuringEnderdragonFight) {
			boolean wasFightingDragon = isFightingDragon;

			isFightingDragon = mc.level != null
					&& mc.level.dimension().location().equals(LevelStem.END.location())
					&& mc.gui.getBossOverlay().shouldPlayMusic();

			final int targetDelay = 50;

			if(isFightingDragon) {
				if(delay == targetDelay) {
					sound = SimpleSoundInstance.forMusic(QuarkSounds.MUSIC_ENDERMOSH);
					mc.getSoundManager().playDelayed(sound, 0);
					mc.gui.setNowPlaying(endermosh.getDisplayName());
				}

				double x = mc.player.getX();
				double z = mc.player.getZ();

				if(mc.screen == null && ((x*x) + (z*z)) < 3000) // is not in screen and within island
					delay++;

			} else if(wasFightingDragon && sound != null) {
				mc.getSoundManager().stop(sound);
				delay = 0;
				sound = null;
			}
		}
	}
}
