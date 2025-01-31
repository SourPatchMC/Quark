package vazkii.quark.content.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.handler.RayTraceHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.CLIENT)
public class LongRangePickBlockModule extends QuarkModule {

	public static boolean staticEnabled;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	@ClientOnly
	public static HitResult transformHitResult(HitResult hitResult) {
		if(staticEnabled) {
			Minecraft mc = Minecraft.getInstance();
			Player player = mc.player;
			Level level = mc.level;

			if(hitResult != null) {
				if(hitResult instanceof BlockHitResult bhr && !level.getBlockState(bhr.getBlockPos()).isAir())
					return hitResult;

				if(hitResult instanceof EntityHitResult ehr)
					return hitResult;
			}

			HitResult result = RayTraceHandler.rayTrace(player, level, player, Block.OUTLINE, Fluid.NONE, 200);
			if(result != null && result.getType() == Type.BLOCK)
				return result;
		}

		return hitResult;
	}

}
