package vazkii.quark.content.tweaks.module;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.base.Quark;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tweaks.recipe.ElytraDuplicationRecipe;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class DragonScalesModule extends QuarkModule {

	@Hint public static Item dragon_scale;

	public DragonScalesModule() {
		super();
		LivingEntityEvents.TICK.register(this::onEntityTick);
	}

	@Override
	public void register() {
		Registry.register(Registry.RECIPE_SERIALIZER, Quark.MOD_ID + ":elytra_duplication", ElytraDuplicationRecipe.SERIALIZER);

		dragon_scale = new QuarkItem("dragon_scale", this, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS));
	}

	public void onEntityTick(LivingEntity entity) {
		if(entity instanceof EnderDragon dragon && !entity.getCommandSenderWorld().isClientSide) {
			if(dragon.getDragonFight() != null && dragon.getDragonFight().hasPreviouslyKilledDragon() && dragon.dragonDeathTime == 100) {
				Vec3 pos = dragon.position();
				ItemEntity item = new ItemEntity(dragon.level, pos.x, pos.y, pos.z, new ItemStack(dragon_scale, 1));
				dragon.level.addFreshEntity(item);
			}
		}
	}

}
