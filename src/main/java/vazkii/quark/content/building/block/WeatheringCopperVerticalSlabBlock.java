package vazkii.quark.content.building.block;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.block.CustomWeatheringCopper;
import vazkii.quark.base.module.QuarkModule;

public class WeatheringCopperVerticalSlabBlock extends QuarkVerticalSlabBlock implements CustomWeatheringCopper {
	private final WeatheringCopper.WeatherState weatherState;
	public WeatheringCopperVerticalSlabBlock first;
	public WeatheringCopperVerticalSlabBlock prev;
	public WeatheringCopperVerticalSlabBlock next;

	public WeatheringCopperVerticalSlabBlock(Block parent, QuarkModule module) {
		super(parent, module);
		weatherState = ((WeatheringCopper) parent).getAge();
	}

	@Override
	public void randomTick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource random) {
		this.onRandomTick(state, world, pos, random);
	}

	@Override
	public boolean isRandomlyTicking(@NotNull BlockState state) {
		return getNext(state).isPresent();
	}

	@NotNull
	@Override
	public Optional<BlockState> getNext(@NotNull BlockState state) {
		return next == null ? Optional.empty() : Optional.of(next.withPropertiesOf(state));
	}

	@NotNull
	@Override
	public Optional<BlockState> getPrevious(@NotNull BlockState state) {
		return prev == null ? Optional.empty() : Optional.of(prev.withPropertiesOf(state));
	}

	@NotNull
	@Override
	public BlockState getFirst(@NotNull BlockState state) {
		return first.withPropertiesOf(state);
	}

	@NotNull
	@Override
	public WeatheringCopper.WeatherState getAge() {
		return weatherState;
	}

}
