package vazkii.quark.content.tools.module;

import com.google.common.collect.ImmutableSet;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerTickEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.api.QuarkCapabilities;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.UpdateTridentMessage;
import vazkii.quark.content.tools.client.render.GlintRenderTypes;
import vazkii.quark.content.tools.item.RuneItem;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author WireSegal
 * Hacked by svenhjol
 * Created at 1:52 PM on 8/17/19.
 */
@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class ColorRunesModule extends QuarkModule {

	public static final String TAG_RUNE_ATTACHED = Quark.MOD_ID + ":RuneAttached";
	public static final String TAG_RUNE_COLOR = Quark.MOD_ID + ":RuneColor";

	public static final int RUNE_TYPES = 17;

	@Config(description = "Whether you can blend runes of each of the 'primary' colors plus white to create rainbow runes.", flag = "craftable_rainbow_rune")
	public static boolean rainbowRuneCraftable = true;

	@Config(description = "Whether you can blend runes of the 'primary' colors to create other colors of rune.", flag = "color_blending_runes")
	public static boolean colorBlendingRunes = true;

	private static final ThreadLocal<ItemStack> targetStack = new ThreadLocal<>();
	@Hint public static TagKey<Item> runesTag;
	public static TagKey<Item> runesLootableTag;
	public static List<RuneItem> runes;
	public static Item rainbow_rune;
	public static Item blank_rune;

	@Config public static int dungeonWeight = 10;
	@Config public static int netherFortressWeight = 8;
	@Config public static int jungleTempleWeight = 8;
	@Config public static int desertTempleWeight = 8;
	@Config public static int itemQuality = 0;
	@Config public static int applyCost = 5;

	public static QuarkGenericTrigger applyRuneTrigger;
	public static QuarkGenericTrigger fullRainbowTrigger;

	public static void setTargetStack(ItemStack stack) {
		targetStack.set(stack);
	}

	public static int changeColor() {
		ItemStack target = targetStack.get();

		return getStackColor(target);
	}

	private static int getStackColor(ItemStack target) {
		if (target == null)
			return -1;

		LazyOptional<IRuneColorProvider> cap = get(target);

		if (cap.isPresent()) {
			int color = cap.orElse((s) -> -1).getRuneColor(target);
			if (color != -1)
				return color;
		}

		if (!ItemNBTHelper.getBoolean(target, TAG_RUNE_ATTACHED, false))
			return -1;

		ItemStack proxied = ItemStack.of(ItemNBTHelper.getCompound(target, TAG_RUNE_COLOR, false));
		LazyOptional<IRuneColorProvider> proxyCap = get(proxied);
		return proxyCap.orElse((s) -> -1).getRuneColor(target);
	}

	@ClientOnly
	public static RenderType getGlint() {
		return renderType(GlintRenderTypes.glint, RenderType::glint);
	}

	@ClientOnly
	public static RenderType getGlintTranslucent() {
		return renderType(GlintRenderTypes.glintTranslucent, RenderType::glintTranslucent);
	}

	@ClientOnly
	public static RenderType getEntityGlint() {
		return renderType(GlintRenderTypes.entityGlint, RenderType::entityGlint);
	}

	@ClientOnly
	public static RenderType getGlintDirect() {
		return renderType(GlintRenderTypes.glintDirect, RenderType::glintDirect);
	}

	@ClientOnly
	public static RenderType getEntityGlintDirect() {
		return renderType(GlintRenderTypes.entityGlintDirect, RenderType::entityGlintDirect);
	}

	@ClientOnly
	public static RenderType getArmorGlint() {
		return renderType(GlintRenderTypes.armorGlint, RenderType::armorGlint);
	}

	@ClientOnly
	public static RenderType getArmorEntityGlint() {
		return renderType(GlintRenderTypes.armorEntityGlint, RenderType::armorEntityGlint);
	}

	@ClientOnly
	private static RenderType renderType(List<RenderType> list, Supplier<RenderType> vanilla) {
		int color = changeColor();
		return color >= 0 && color <= RUNE_TYPES ? list.get(color) : vanilla.get();
	}

	private static final Map<ThrownTrident, ItemStack> TRIDENT_STACK_REFERENCES = new WeakHashMap<>();

	public static void syncTrident(Consumer<Packet<?>> packetConsumer, ThrownTrident trident, boolean force) {
		ItemStack stack = trident.getPickupItem();
		ItemStack prev = TRIDENT_STACK_REFERENCES.get(trident);
		if (force || prev == null || ItemStack.isSameItemSameTags(stack, prev))
			packetConsumer.accept(QuarkNetwork.toVanillaPacket(new UpdateTridentMessage(trident.getId(), stack), NetworkDirection.PLAY_TO_CLIENT));
		else
			TRIDENT_STACK_REFERENCES.put(trident, stack);
	}

	public static ItemStack withRune(ItemStack stack, ItemStack rune) {
		ItemNBTHelper.setBoolean(stack, ColorRunesModule.TAG_RUNE_ATTACHED, true);
		ItemNBTHelper.setCompound(stack, ColorRunesModule.TAG_RUNE_COLOR, rune.serializeNBT());
		return stack;
	}

	public static ItemStack withRune(ItemStack stack, DyeColor color) {
		return withRune(stack, new ItemStack(runes.get(color.getId())));
	}

	public ColorRunesModule() {
		super();
		PlayerTickEvents.START.register(this::onPlayerTick);
		LootTableEvents.MODIFY.register(this::onLootTableLoad);

	}

	@Override
	public void register() {
		runes = Arrays.stream(DyeColor.values()).map(color -> new RuneItem(color.getSerializedName() + "_rune", this, color.getId(), true)).toList();

		rainbow_rune = new RuneItem("rainbow_rune", this, 16, true);
		blank_rune = new RuneItem("blank_rune", this, 17, false);

		applyRuneTrigger = QuarkAdvancementHandler.registerGenericTrigger("apply_rune");
		fullRainbowTrigger = QuarkAdvancementHandler.registerGenericTrigger("full_rainbow");
	}

	@Override
	public void setup() {
		runesTag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Quark.MOD_ID, "runes"));
		runesLootableTag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Quark.MOD_ID, "runes_lootable"));
	}

	public void onLootTableLoad(ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder, LootTableSource source) {
		int weight = 0;

		if (id.equals(BuiltInLootTables.SIMPLE_DUNGEON))
			weight = dungeonWeight;
		else if (id.equals(BuiltInLootTables.NETHER_BRIDGE))
			weight = netherFortressWeight;
		else if (id.equals(BuiltInLootTables.JUNGLE_TEMPLE))
			weight = jungleTempleWeight;
		else if (id.equals(BuiltInLootTables.DESERT_PYRAMID))
			weight = desertTempleWeight;

		if(weight > 0) {
			LootPoolEntryContainer entry = LootItem.lootTableItem(blank_rune)
					.setWeight(weight)
					.setQuality(itemQuality)
					.build();
			MiscUtil.addToLootTable(tableBuilder, entry);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();
		ItemStack output = event.getOutput();

		if(!left.isEmpty() && !right.isEmpty() && canHaveRune(left) && right.is(runesTag)) {
			ItemStack out = (output.isEmpty() ? left : output).copy();
			ItemNBTHelper.setBoolean(out, TAG_RUNE_ATTACHED, true);
			ItemNBTHelper.setCompound(out, TAG_RUNE_COLOR, right.serializeNBT());
			event.setOutput(out);

			String name = event.getName();
			int cost = Math.max(1, applyCost);

			if(name != null && !name.isEmpty() && (!out.hasCustomHoverName() || !out.getHoverName().getString().equals(name))) {
				out.setHoverName(Component.literal(name));
				cost++;
			}

			event.setCost(cost);
			event.setMaterialCost(1);
		}
	}

	@SubscribeEvent
	public void onAnvilUse(AnvilRepairEvent event) {
		ItemStack right = event.getRight();

		if(right.is(runesTag) && event.getEntity() instanceof ServerPlayer sp)
			applyRuneTrigger.trigger(sp);
	}

	public void onPlayerTick(Player player) {
		final String tag = "quark:what_are_you_gay_or_something";

		boolean wasRainbow = player.getPersistentData().getBoolean(tag);
		boolean rainbow = isPlayerRainbow(player);

		if(wasRainbow != rainbow) {
			player.getPersistentData().putBoolean(tag, rainbow);
			if(rainbow && player instanceof ServerPlayer sp)
				fullRainbowTrigger.trigger(sp);
		}
	}

	private boolean isPlayerRainbow(Player player) {
		Set<EquipmentSlot> checks = ImmutableSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

		for(EquipmentSlot slot : checks) {
			ItemStack stack = player.getItemBySlot(slot);
			if(stack.isEmpty() || getStackColor(stack) != 16) // 16 = rainbow rune
				return false;
		}

		return true;
	}

	private static boolean canHaveRune(ItemStack stack) {
		return stack.isEnchanted() || (stack.getItem() == Items.COMPASS && CompassItem.isLodestoneCompass(stack)); // isLodestoneCompass = is lodestone compass
	}

	private static LazyOptional<IRuneColorProvider> get(ICapabilityProvider provider) {
		return provider.getCapability(QuarkCapabilities.RUNE_COLOR);
	}

}
