package vazkii.quark.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.addons.oddities.item.TinyPotatoBlockItem;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.content.tools.module.AmbientDiscsModule;
import vazkii.quark.content.tweaks.module.ArmedArmorStandsModule;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    public void quiltQuark$tickDeathEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleLoader.INSTANCE.getModuleInstance(AmbientDiscsModule.class) instanceof AmbientDiscsModule ambientDiscsModule && ambientDiscsModule.enabled) {
            ambientDiscsModule.onMobDeath((LivingEntity) ((Object)this), source);
        }
    }

    @Inject(method = "getEquipmentSlotForItem", at = @At(value = "HEAD"), cancellable = true)
    static void quiltQuark$fixTaterEquip(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (stack.getItem() instanceof TinyPotatoBlockItem) cir.setReturnValue(EquipmentSlot.HEAD);
    }
}
