package vazkii.quark.mixin;

import io.github.fabricators_of_create.porting_lib.fake_players.extensions.PlayerExtensions;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.management.module.AutomaticToolRestockModule;
import vazkii.quark.content.tools.module.ColorRunesModule;
import vazkii.quark.content.tweaks.module.CampfiresBoostElytraModule;
import vazkii.quark.content.tweaks.module.EnhancedLaddersModule;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerExtensions {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    // Acts as our own version of PlayerTickEvent from Forge. Most methods, unless explicitly otherwise, will use this version.
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void quarkQuilt$playerTickEvent(CallbackInfo ci) {
        new CampfiresBoostElytraModule().onPlayerTick((Player)((Object)this));
        new ColorRunesModule().onPlayerTick((Player)((Object)this));
        new EnhancedLaddersModule().onPlayerTick((Player)((Object)this));
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void quarkQuilt$playerTickEventEnd(CallbackInfo ci) {
        new AutomaticToolRestockModule().onPlayerTick((Player)((Object)this));
    }
}
