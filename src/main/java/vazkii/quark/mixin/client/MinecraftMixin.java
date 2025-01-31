package vazkii.quark.mixin.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import vazkii.quark.content.client.module.LongRangePickBlockModule;

import org.jetbrains.annotations.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow @Nullable public HitResult hitResult;

	@Inject(method = "pickBlock", at = @At("HEAD"))
	private void pickBlockHead(CallbackInfo ci, @Share("hitResult") LocalRef<HitResult> hitResult) {
		Minecraft self = (Minecraft) (Object) this;
		hitResult.set(self.hitResult);
		self.hitResult = LongRangePickBlockModule.transformHitResult(self.hitResult);
	}

	@Inject(method = "pickBlock", at = @At("RETURN"))
	private void pickBlockReturn(CallbackInfo ci, @Share("hitResult") LocalRef<HitResult> hitResult) {
		Minecraft self = (Minecraft) (Object) this;
		self.hitResult = hitResult.get();
	}
}
