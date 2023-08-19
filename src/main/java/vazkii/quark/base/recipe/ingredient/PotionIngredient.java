package vazkii.quark.base.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import vazkii.quark.base.handler.BrewingHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author WireSegal
 * Created at 5:10 PM on 9/23/19.
 */
public class PotionIngredient extends Ingredient {
	private final Item item;
	private final Potion potion;

	public PotionIngredient(Item item, Potion potion) {
		super(Stream.of(new Ingredient.ItemValue(BrewingHandler.of(item, potion))));
		this.item = item;
		this.potion = potion;
	}

	@Override
	public boolean test(@Nullable ItemStack input) {
		if (input == null)
			return false;
		//Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
		return item == input.getItem() && PotionUtils.getPotion(input) == potion;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@NotNull
	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return PotionIngredient.Serializer.INSTANCE;
	}

	@NotNull
	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", Objects.toString(CraftingHelper.getID(PotionIngredient.Serializer.INSTANCE)));
		json.addProperty("item", Objects.toString(Registry.ITEM.getKey(item)));
		json.addProperty("potion", Objects.toString(Registry.POTION.getKey(potion)));
		return json;
	}

	public static class Serializer implements IIngredientSerializer<PotionIngredient> {
		public static final PotionIngredient.Serializer INSTANCE = new PotionIngredient.Serializer();

		@NotNull
		@Override
		public PotionIngredient parse(@NotNull FriendlyByteBuf buffer) {
			Item item = Registry.ITEM.get(buffer.readResourceLocation());
			Potion potion = Registry.POTION.get(buffer.readResourceLocation());
			return new PotionIngredient(item, potion);
		}

		@NotNull
		@Override
		public PotionIngredient parse(@NotNull JsonObject json) {
			Item item = Registry.ITEM.get(new ResourceLocation(json.getAsJsonPrimitive("item").getAsString()));
			Potion potion = Registry.POTION.get(new ResourceLocation(json.getAsJsonPrimitive("potion").getAsString()));
			return new PotionIngredient(item, potion);
		}

		@Override
		public void write(@NotNull FriendlyByteBuf buffer, @NotNull PotionIngredient ingredient) {
			buffer.writeUtf(Objects.toString(Registry.ITEM.getKey(ingredient.item)));
			buffer.writeUtf(Objects.toString(Registry.POTION.getKey(ingredient.potion)));
		}
	}
}
