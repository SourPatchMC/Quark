package vazkii.quark.api.config;

import java.io.PrintStream;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public interface IConfigElement extends Comparable<IConfigElement> {

	String getName();
	String getGuiDisplayName();
	List<String> getTooltip();
	@Nullable IConfigCategory getParent();
	boolean isDirty();
	void clean();
	void save();

	void refresh();
	void reset(boolean hard);
	void print(String pad, PrintStream out);

}
