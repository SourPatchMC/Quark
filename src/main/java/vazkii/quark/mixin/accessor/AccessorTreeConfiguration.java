package vazkii.quark.mixin.accessor;

import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TreeConfiguration.class)
public interface AccessorTreeConfiguration {
    @Accessor
    @Final
    @Mutable
    void setTrunkProvider(BlockStateProvider trunkProvider);
}