package vazkii.quark.integration.claim;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.QuiltLoader;

public interface IClaimIntegration {

    IClaimIntegration INSTANCE = Util.make(() -> {
        if (QuiltLoader.isModLoaded("flan")) {
            return new FlanIntegration();
        }
        return new IClaimIntegration() {
        }; //NO OP
    });

    default boolean canBreak(@NotNull Player player, @NotNull BlockPos pos) {
        return true;
    }

    default boolean canPlace(@NotNull Player player, @NotNull BlockPos pos) {
        return true;
    }

    default boolean canReplace(@NotNull Player player, @NotNull BlockPos pos) {
        return true;
    }

    default boolean canAttack(@NotNull Player player, @NotNull Entity victim) {
        return true;
    }

    default boolean canInteract(@NotNull Player player, @NotNull BlockPos targetPos) {
        return true;
    }

}
