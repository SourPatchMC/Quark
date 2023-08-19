package vazkii.quark.mixin.client;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.item.QuarkMusicDiscItem;

import java.util.Map;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Final
	@Shadow
	private final Map<BlockPos, SoundInstance> playingRecords = Maps.newHashMap();

	// Silly Ithundxr! Might as well uncomment it and then if it breaks we know what the cause is!
	@Inject(method = "playStreamingMusic(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/core/BlockPos;)V",
			remap = false, at = @At(value = "JUMP", ordinal = 1), cancellable = true)
	public void playStreamingMusic(SoundEvent soundEvent, BlockPos pos, CallbackInfo ci) {
		if(playingRecords instanceof QuarkMusicDiscItem quarkDisc && quarkDisc.playAmbientSound(pos)) ci.cancel();
	}
}
