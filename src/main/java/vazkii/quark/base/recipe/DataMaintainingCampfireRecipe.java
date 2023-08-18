package vazkii.quark.base.recipe;

import net.minecraft.core.Registry;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import vazkii.arl.util.ItemNBTHelper;

/**
 * @author WireSegal
 * Created at 2:08 PM on 8/24/19.
 */
public class DataMaintainingCampfireRecipe extends CampfireCookingRecipe {
	public static final Serializer SERIALIZER = new Serializer();

	private final AbstractCookingRecipe parent;
	private final Ingredient pullDataFrom;

	public DataMaintainingCampfireRecipe(AbstractCookingRecipe parent, Ingredient pullDataFrom) {
		super(parent.getId(), parent.getGroup(), parent.getIngredients().get(0), parent.getResultItem(), parent.getExperience(), parent.getCookingTime());
		this.parent = parent;
		this.pullDataFrom = pullDataFrom;
	}

	@Override
	public boolean matches(@NotNull Container inv, @NotNull Level level) {
		return parent.matches(inv, level);
	}

	@Override
	public float getExperience() {
		return parent.getExperience();
	}

	@Override
	public int getCookingTime() {
		return parent.getCookingTime();
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull Container inv) {
		ItemStack stack = parent.assemble(inv);
		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack inInv = inv.getItem(i);
			if (pullDataFrom.test(inInv)) {
				CompoundTag tag = ItemNBTHelper.getNBT(inInv);
				if (!tag.isEmpty())
					stack.getOrCreateTag().merge(tag);
				break;
			}
		}

		return stack;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return parent.canCraftInDimensions(width, height);
	}

	@NotNull
	@Override
	public ItemStack getResultItem() {
		return parent.getResultItem();
	}

	@NotNull
	@Override
	public ResourceLocation getId() {
		return parent.getId();
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@NotNull
	@Override
	public RecipeType<?> getType() {
		return parent.getType();
	}

	@NotNull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@NotNull Container inv) {
		return parent.getRemainingItems(inv);
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return parent.getIngredients();
	}

	@Override
	public boolean isSpecial() {
		return parent.isSpecial();
	}

	@NotNull
	@Override
	public String getGroup() {
		return parent.getGroup();
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return parent.getToastSymbol();
	}

	public static class Serializer implements RecipeSerializer<DataMaintainingCampfireRecipe> {

		@NotNull
		@Override
		public DataMaintainingCampfireRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
			String trueType = "minecraft:campfire_cooking";

			Ingredient pullFrom = Ingredient.fromJson(json.get("copy_data_from"));

			RecipeSerializer<?> serializer = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(trueType));
			if (serializer == null)
				throw new JsonSyntaxException("Invalid or unsupported recipe type '" + trueType + "'");
			Recipe<?> parent = serializer.fromJson(recipeId, json);
			if (!(parent instanceof AbstractCookingRecipe cookingRecipe))
				throw new JsonSyntaxException("Type '" + trueType + "' is not a cooking recipe");

			return new DataMaintainingCampfireRecipe(cookingRecipe, pullFrom);
		}

		@NotNull
		@Override
		public DataMaintainingCampfireRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
			Ingredient pullFrom = Ingredient.fromNetwork(buffer);

			String trueType = "minecraft:campfire_cooking";

			RecipeSerializer<?> serializer = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(trueType));
			if (serializer == null)
				throw new IllegalArgumentException("Invalid or unsupported recipe type '" + trueType + "'");
			Recipe<?> parent = serializer.fromNetwork(recipeId, buffer);
			if (!(parent instanceof AbstractCookingRecipe cookingRecipe))
				throw new IllegalArgumentException("Type '" + trueType + "' is not a cooking recipe");

			return new DataMaintainingCampfireRecipe(cookingRecipe, pullFrom);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DataMaintainingCampfireRecipe recipe) {
			recipe.pullDataFrom.toNetwork(buffer);
			((RecipeSerializer<Recipe<?>>) recipe.parent.getSerializer()).toNetwork(buffer, recipe.parent);
		}
	}
}
