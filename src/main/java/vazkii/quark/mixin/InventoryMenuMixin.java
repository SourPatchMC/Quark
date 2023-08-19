package vazkii.quark.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.addons.oddities.item.TinyPotatoBlockItem;
import vazkii.quark.base.handler.ContributorRewardHandler;

@Mixin(targets = "net.minecraft.world.inventory.InventoryMenu$1")
public class InventoryMenuMixin {
    @Shadow @Final private Player val$owner;

    @ModifyReturnValue(method = "mayPlace", at = @At("RETURN"))
    private boolean quiltQuark$equipSlotCheck(boolean prev, ItemStack stack) {
        if (prev && stack.getItem() instanceof TinyPotatoBlockItem && ContributorRewardHandler.getTier(val$owner) <= 0) {
            return false;
        }

        return prev;
    }
}
