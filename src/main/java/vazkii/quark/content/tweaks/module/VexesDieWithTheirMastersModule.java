package vazkii.quark.content.tweaks.module;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

//todo: Convert to mixin
@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class VexesDieWithTheirMastersModule extends QuarkModule {
	public VexesDieWithTheirMastersModule() {
		super();
		LivingEntityEvents.TICK.register(this::checkWhetherAlreadyDead);
	}

	// omae wa mou shindeiru
	public void checkWhetherAlreadyDead(LivingEntity livingEntity) {
		if (livingEntity instanceof Vex vex) {
			Mob owner = vex.getOwner();
			if (owner != null && owner.isDeadOrDying() && !vex.isDeadOrDying())
				vex.hurt(DamageSource.mobAttack(owner).bypassArmor().bypassInvul().bypassMagic(), vex.getHealth());
		}
	}
}
