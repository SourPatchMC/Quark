package vazkii.quark.base.module.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 * Created at 1:23 PM on 8/24/19.
 */
public record FlagLootCondition(ConfigFlagManager manager,
								String flag) implements LootItemCondition {

	@Override
	public boolean test(LootContext lootContext) {
		return manager.getFlag(flag);
	}


	@NotNull
	@Override
	public LootItemConditionType getType() {
		return ConfigFlagManager.flagLootConditionType;
	}


	public record FlagSerializer(ConfigFlagManager manager) implements Serializer<FlagLootCondition> {

		@Override
		public void serialize(@NotNull JsonObject json, @NotNull FlagLootCondition value, @NotNull JsonSerializationContext context) {
			json.addProperty("flag", value.flag);
		}

		@NotNull
		@Override
		public FlagLootCondition deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) {
			String flag = json.getAsJsonPrimitive("flag").getAsString();
			return new FlagLootCondition(manager, flag);
		}
	}

}
