package vazkii.quark.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Mixin(value = PlayerInfo.class)
public class PlayerInfoMixin {
    @Shadow
    @Final
    private GameProfile profile;
    @Shadow @Final
    private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;

    @Inject(at = @At("HEAD"), method = "getCapeLocation")
    protected void registerTextures(CallbackInfoReturnable<ResourceLocation> cir) {
        final Set<String> DEV_UUID = Set.of(
                "8c826f34-113b-4238-a173-44639c53b6e6", // Vazkii
                "0d054077-a977-4b19-9df9-8a4d5bf20ec3", // wi0iv
                "458391f5-6303-4649-b416-e4c0d18f837a", // yrsegal
                "75c298f9-27c8-415b-9a16-329e3884054b", // minecraftvinnyq
                "6c175d10-198a-49f9-8e2b-c74f1f0178f3", // MilkBringer / Sully
                "e67eb09a-b5af-4822-b756-9065cdc49913", // IThundxr
                "0d21b52c-296f-49b7-b9c6-358da211090e", // Maximum
                "07cb3dfd-ee1d-4ecf-b5b5-f70d317a82eb", // Sioulplex // Dont you fucking dare call me Sioul I will slaughter you - Siuol
                "7b888d55-dc30-4a4c-9cc8-c7073024f286"  // Bubblie

        );

        final Set<String> done = Collections.newSetFromMap(new WeakHashMap<>());

        final String uuid_string = profile.getId().toString();

        if (DEV_UUID.contains(uuid_string) && !done.contains(uuid_string)) {
            ResourceLocation location = new ResourceLocation("quark", "textures/misc/dev_cape.png");
            this.textureLocations.put(MinecraftProfileTexture.Type.CAPE, location);
            this.textureLocations.put(MinecraftProfileTexture.Type.ELYTRA, location);
            done.add(uuid_string);
        }
    }
}
