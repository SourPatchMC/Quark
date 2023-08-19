package vazkii.quark.mixin.client.accessor;

import java.util.Map;

import net.minecraft.core.IdMapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.IdMapper;
import net.minecraft.world.level.block.Block;

@Mixin(BlockColors.class)
public interface AccessorBlockColors {
	//fixme
	@Accessor("blockColors")
	IdMapper<BlockColor> quark$getBlockColors();
}
