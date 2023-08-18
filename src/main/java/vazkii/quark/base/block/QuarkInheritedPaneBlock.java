package vazkii.quark.base.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.quark.base.handler.RenderLayerHandler;

/**
 * @author WireSegal
 * Created at 1:09 PM on 9/19/19.
 */
public class QuarkInheritedPaneBlock extends QuarkPaneBlock implements IQuarkBlock, IBlockColorProvider {

	public final IQuarkBlock parent;

	public QuarkInheritedPaneBlock(IQuarkBlock parent, String name, Block.Properties properties) {
		super(name, parent.getModule(), properties, null);

		this.parent = parent;
		RenderLayerHandler.setInherited(this, parent.getBlock());
	}

	public QuarkInheritedPaneBlock(IQuarkBlock parent, Block.Properties properties) {
		this(parent, IQuarkBlock.inheritQuark(parent, "%s_pane"), properties);
	}

	public QuarkInheritedPaneBlock(IQuarkBlock parent) {
		this(parent, Block.Properties.copy(parent.getBlock()));
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && parent.isEnabled();
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return parent.getBlock().getBeaconColorMultiplier(parent.getBlock().defaultBlockState(), world, pos, beaconPos);
	}

	@Override
	@ClientOnly
	public BlockColor getBlockColor() {
		return parent instanceof IBlockColorProvider provider ? provider.getBlockColor() : null;
	}

	@Override
	@ClientOnly
	public ItemColor getItemColor() {
		return parent instanceof IItemColorProvider provider ? provider.getItemColor() : null;
	}
}
