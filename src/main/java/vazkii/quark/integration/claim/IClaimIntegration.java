package vazkii.quark.integration.claim;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import org.jetbrains.annotations.NotNull;

public interface IClaimIntegration {

    IClaimIntegration INSTANCE = Util.make(() -> {
        if (ModList.get().isLoaded("flan")) {
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
