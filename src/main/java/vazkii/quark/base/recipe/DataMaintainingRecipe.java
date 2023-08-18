package vazkii.quark.base.recipe;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;
import vazkii.arl.util.ItemNBTHelper;

/**
 * @author WireSegal
 * Created at 2:08 PM on 8/24/19.
 */
public class DataMaintainingRecipe implements CraftingRecipe {
	public static final Serializer SERIALIZER = new Serializer();

	private final CraftingRecipe parent;
	private final Ingredient pullDataFrom;

	public DataMaintainingRecipe(CraftingRecipe parent, Ingredient pullDataFrom) {
		this.parent = parent;
		this.pullDataFrom = pullDataFrom;
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
		return parent.matches(inv, worldIn);
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv) {
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
	public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer inv) {
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

	private static class ShapedDataMaintainingRecipe extends DataMaintainingRecipe implements IShapedRecipe<CraftingContainer> {
		private final IShapedRecipe<CraftingContainer> parent;

		@SuppressWarnings("unchecked")
		public ShapedDataMaintainingRecipe(CraftingRecipe parent, Ingredient pullDataFrom) {
			super(parent, pullDataFrom);
			this.parent = (IShapedRecipe<CraftingContainer>) parent;
		}

		@Override
		public int getRecipeWidth() {
			return parent.getRecipeWidth();
		}

		@Override
		public int getRecipeHeight() {
			return parent.getRecipeHeight();
		}
	}

	public static class Serializer implements RecipeSerializer<DataMaintainingRecipe> {

		@NotNull
		@Override
		public DataMaintainingRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
			String trueType = GsonHelper.getAsString(json, "true_type");
			if (trueType.equals("quark:maintaining"))
				throw new JsonSyntaxException("Recipe type circularity");

			Ingredient pullFrom = Ingredient.fromJson(json.get("copy_data_from"));

			RecipeSerializer<?> serializer = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(trueType));
			if (serializer == null)
				throw new JsonSyntaxException("Invalid or unsupported recipe type '" + trueType + "'");
			Recipe<?> parent = serializer.fromJson(recipeId, json);
			if (!(parent instanceof CraftingRecipe craftingRecipe))
				throw new JsonSyntaxException("Type '" + trueType + "' is not a crafting recipe");

			if (parent instanceof IShapedRecipe)
				return new ShapedDataMaintainingRecipe(craftingRecipe, pullFrom);
			return new DataMaintainingRecipe(craftingRecipe, pullFrom);
		}

		@NotNull
		@Override
		public DataMaintainingRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
			Ingredient pullFrom = Ingredient.fromNetwork(buffer);

			String trueType = buffer.readUtf(32767);

			RecipeSerializer<?> serializer = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(trueType));
			if (serializer == null)
				throw new IllegalArgumentException("Invalid or unsupported recipe type '" + trueType + "'");
			Recipe<?> parent = serializer.fromNetwork(recipeId, buffer);
			if (!(parent instanceof CraftingRecipe craftingRecipe))
				throw new IllegalArgumentException("Type '" + trueType + "' is not a crafting recipe");

			if (parent instanceof IShapedRecipe)
				return new ShapedDataMaintainingRecipe(craftingRecipe, pullFrom);
			return new DataMaintainingRecipe(craftingRecipe, pullFrom);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DataMaintainingRecipe recipe) {
			recipe.pullDataFrom.toNetwork(buffer);
			buffer.writeUtf(Objects.toString(Registry.RECIPE_SERIALIZER.getKey(recipe.parent.getSerializer())), 32767);
			((RecipeSerializer<Recipe<?>>) recipe.parent.getSerializer()).toNetwork(buffer, recipe.parent);
		}
	}
}
