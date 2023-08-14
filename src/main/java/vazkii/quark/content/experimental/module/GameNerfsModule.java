package vazkii.quark.content.experimental.module;

import com.mojang.serialization.Dynamic;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false, hasSubscriptions = true)
public class GameNerfsModule extends QuarkModule {

	private static final String TAG_TRADES_ADJUSTED = "quark:zombie_trades_adjusted";

	@Config(description = "Makes Mending act like the Unmending mod\n"
			+ "https://www.curseforge.com/minecraft/mc-mods/unmending")
	public static boolean nerfMending = true;

	@Config(description = "Resets all villager discounts when zombified to prevent reducing prices to ridiculous levels")
	public static boolean nerfVillagerDiscount = true;

	@Config(description = "Makes Iron Golems not drop Iron Ingots")
	public static boolean disableIronFarms = true;

	@Config(description = "Makes Boats not glide on ice")
	public static boolean disableIceRoads = true;

	@Config(description = "Makes Sheep not drop Wool when killed")
	public static boolean disableWoolDrops = true;

	@Config(description = "Disables mob griefing for only specific entities")
	public static boolean enableSelectiveMobGriefing = true;

	@Config(description = "Force Elytra to only work in specific dimensions")
	public static boolean enableDimensionLockedElytra = true;

	@Config(description = "Makes falling blocks not able to be duped via dimension crossing")
	public static boolean disableFallingBlockDupe = true;

	@Config(description = "Fixes several piston physics exploits, most notably including TNT duping")
	public static boolean disablePistonPhysicsExploits = true;

	@Config(description = "Fixes mushroom growth being able to replace blocks")
	public static boolean disableMushroomBlockRemoval = true;

	@Config
	public static List<String> nonGriefingEntities = Arrays.asList("minecraft:creeper", "minecraft:enderman");

	@Config
	public static List<String> elytraAllowedDimensions = Arrays.asList("minecraft:the_end");

	private static boolean staticEnabled;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	// Source for this magic number is the ice-boat-nerf mod
	// https://gitlab.com/supersaiyansubtlety/ice_boat_nerf/-/blob/master/src/main/java/net/sssubtlety/ice_boat_nerf/mixin/BoatEntityMixin.java
	public static float getBoatFriction(float glide) {
		return (staticEnabled && disableIceRoads) ? 0.45F : glide;
	}

	public static boolean canEntityUseElytra(LivingEntity entity, boolean prev) {
		if(!prev)
			return false;
		if(!staticEnabled || !enableDimensionLockedElytra)
			return true;

		Level level = entity.getLevel();
		String dim = level.dimensionTypeId().location().toString();
		return elytraAllowedDimensions.contains(dim);
	}

	public static boolean stopFallingBlocksDuping() {
		return staticEnabled && disableFallingBlockDupe;
	}

	public static boolean stopPistonPhysicsExploits() {
		return staticEnabled && disablePistonPhysicsExploits;
	}

	public static boolean shouldMushroomsUseTreeReplacementLogic() {
		return staticEnabled && disableMushroomBlockRemoval;
	}

	@SubscribeEvent
	public void onMobGriefing(EntityMobGriefingEvent event) {
		if(!enableSelectiveMobGriefing || event.getEntity() == null)
			return;

		String name = Registry.ENTITY_TYPE.getKey(event.getEntity().getType()).toString();
		if(nonGriefingEntities.contains(name))
			event.setResult(Result.DENY);
	}

	// stolen from King Lemming thanks mate
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void killMending(PlayerXpEvent.PickupXp event) {
		if(!nerfMending)
			return;

		Player player = event.getEntity();
		ExperienceOrb orb = event.getOrb();

		player.takeXpDelay = 2;
		player.take(orb, 1);
		if(orb.value > 0)
			player.giveExperiencePoints(orb.value);

		orb.discard();
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		if(!nerfMending)
			return;

		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();
		ItemStack out = event.getOutput();

		if(out.isEmpty() && (left.isEmpty() || right.isEmpty()))
			return;

		boolean isMended = false;

		Map<Enchantment, Integer> enchLeft = EnchantmentHelper.getEnchantments(left);
		Map<Enchantment, Integer> enchRight = EnchantmentHelper.getEnchantments(right);

		if(enchLeft.containsKey(Enchantments.MENDING) || enchRight.containsKey(Enchantments.MENDING)) {
			if(left.getItem() == right.getItem())
				isMended = true;

			if(right.getItem() == Items.ENCHANTED_BOOK)
				isMended = true;
		}

		if(isMended) {
			if(out.isEmpty())
				out = left.copy();

			if(!out.hasTag())
				out.setTag(new CompoundTag());

			Map<Enchantment, Integer> enchOutput = EnchantmentHelper.getEnchantments(out);
			for (Enchantment enchantment : enchRight.keySet()) {
				if (enchantment.canEnchant(out)) {
					int level = enchRight.get(enchantment);
					if (enchOutput.containsKey(enchantment)) {
						int levelPresent = enchOutput.get(enchantment);
						if (level > levelPresent)
							enchOutput.put(enchantment, level);
						else if (level == levelPresent && enchantment.getMaxLevel() > level)
							enchOutput.put(enchantment, level + 1);
					} else {
						enchOutput.put(enchantment, level);
					}
				}
			}
			enchOutput.remove(Enchantments.MENDING);

			EnchantmentHelper.setEnchantments(enchOutput, out);

			out.setRepairCost(0);
			if(out.isDamageableItem())
				out.setDamageValue(0);

			event.setOutput(out);
			event.setCost(5);
		}
	}

	@SubscribeEvent
	@ClientOnly
	public void onTooltip(ItemTooltipEvent event) {
		if(!nerfMending)
			return;

		Component itemgotmodified = Component.translatable("quark.misc.repaired").withStyle(ChatFormatting.YELLOW);
		int repairCost = event.getItemStack().getBaseRepairCost();
		if(repairCost > 0)
			event.getToolTip().add(itemgotmodified);
	}

	@SubscribeEvent
	public void onTick(LivingTickEvent event) {
		if(nerfVillagerDiscount && event.getEntity().getType() == EntityType.ZOMBIE_VILLAGER && !event.getEntity().getPersistentData().contains(TAG_TRADES_ADJUSTED)) {
			ZombieVillager zombie = (ZombieVillager) event.getEntity();

			Tag gossipsNbt = zombie.gossips;

			GossipContainer manager = new GossipContainer();
			manager.update(new Dynamic<>(NbtOps.INSTANCE, gossipsNbt));

			for(UUID uuid : manager.gossips.keySet()) {
				GossipContainer.EntityGossips gossips = manager.gossips.get(uuid);
				gossips.remove(GossipType.MAJOR_POSITIVE);
				gossips.remove(GossipType.MINOR_POSITIVE);
			}

			zombie.getPersistentData().putBoolean(TAG_TRADES_ADJUSTED, true);
		}
	}

	@SubscribeEvent
	public void onLoot(LivingDropsEvent event) {
		if(disableIronFarms && event.getEntity().getType() == EntityType.IRON_GOLEM)
			event.getDrops().removeIf(e -> e.getItem().getItem() == Items.IRON_INGOT);

		if(disableWoolDrops && event.getEntity().getType() == EntityType.SHEEP)
			event.getDrops().removeIf(e -> e.getItem().is(ItemTags.WOOL));
	}

}

