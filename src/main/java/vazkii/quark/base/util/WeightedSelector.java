package vazkii.quark.base.util;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

/**
 * @author WireSegal
 * Created at 8:42 PM on 10/1/19.
 *
 * A clone of WeightedList with actual names.
 */
public class WeightedSelector<U> {
	private final List<WeightedSelector.Entry<? extends U>> entries = Lists.newArrayList();
	private final Random random;

	public WeightedSelector() {
		this(new Random());
	}

	public WeightedSelector(Random rand) {
		this.random = rand;
	}

	public void add(@NotNull U value, int weight) {
		this.entries.add(new WeightedSelector.Entry<>(value, weight));
	}

	public void shuffle() {
		this.entries.forEach((entry) -> entry.randomizeWithValue(this.random.nextFloat()));
		this.entries.sort(Comparator.comparingDouble(WeightedSelector.Entry::getWeightedPower));
	}

	@NotNull
	public U select(U defaultValue) {
		shuffle();
		if (this.entries.isEmpty())
			return defaultValue;
		return this.entries.get(0).getValue();
	}

	@Nullable
	public U select() {
		return select(null);
	}

	@NotNull
	public Stream<? extends U> stream() {
		return this.entries.stream().map(WeightedSelector.Entry::getValue);
	}

	@Override
	public String toString() {
		return "WeightedList[" + this.entries + "]";
	}

	@NotNull
	public WeightedSelector<U> copy() {
		WeightedSelector<U> copied = new WeightedSelector<>();
		for (Entry<? extends U> entry : entries)
			copied.add(entry.value, entry.weight);
		return copied;
	}

	private static class Entry<T> {
		private final T value;
		private final int weight;
		private double weightedPower;

		private Entry(T value, int weight) {
			this.weight = weight;
			this.value = value;
		}

		public double getWeightedPower() {
			return this.weightedPower;
		}

		public void randomizeWithValue(float randValue) {
			this.weightedPower = -Math.pow(randValue, (1.0F / this.weight));
		}

		@NotNull
		public T getValue() {
			return this.value;
		}

		@Override
		public String toString() {
			return "" + this.weight + ":" + this.value;
		}
	}
}
