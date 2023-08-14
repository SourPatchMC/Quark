package vazkii.quark.base.client.config.screen.widgets;

import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.base.client.config.screen.CategoryScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;

public interface IWidgetProvider {

	@ClientOnly
	void addWidgets(CategoryScreen parent, IConfigElement element, List<WidgetWrapper> widgets);

	@ClientOnly
	String getSubtitle();

}
