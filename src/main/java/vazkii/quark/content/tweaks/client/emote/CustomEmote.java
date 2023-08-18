package vazkii.quark.content.tweaks.client.emote;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.content.tweaks.module.EmotesModule;

@ClientOnly
public class CustomEmote extends TemplateSourcedEmote {

	public CustomEmote(EmoteDescriptor desc, Player player, HumanoidModel<?> model, HumanoidModel<?> armorModel, HumanoidModel<?> armorLegsModel) {
		super(desc, player, model, armorModel, armorLegsModel);
	}

	@Override
	public boolean shouldLoadTimelineOnLaunch() {
		return EmotesModule.customEmoteDebug;
	}

}
