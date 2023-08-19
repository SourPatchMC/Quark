package vazkii.quark.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.content.tweaks.module.ArmedArmorStandsModule;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntity {
    protected ArmorStandMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    // Inject at constructor, no equivalent event exists to my knowledge
    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDD)V", at = @At("TAIL"))
    private void quarkQuilt$armorStandConstructorInject(Level level, double x, double y, double z, CallbackInfo ci) {
        if (ModuleLoader.INSTANCE.getModuleInstance(ArmedArmorStandsModule.class) instanceof ArmedArmorStandsModule armedArmorStandsModule && armedArmorStandsModule.enabled) {
            armedArmorStandsModule.entityConstruct((ArmorStand) ((Object)this));
        }
    }
}
