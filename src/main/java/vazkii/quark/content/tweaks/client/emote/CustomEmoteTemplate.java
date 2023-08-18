package vazkii.quark.content.tweaks.client.emote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.content.tweaks.module.EmotesModule;

@ClientOnly
public class CustomEmoteTemplate extends EmoteTemplate {

	private String name;
	
	public CustomEmoteTemplate(String file) {
		super(file + ".emote");
		
		if(name == null)
			name = file;
	}
	
	@Override
	protected BufferedReader createReader() throws FileNotFoundException {
		return new BufferedReader(new FileReader(new File(EmotesModule.emotesDir, file)));
	}
	
	@Override
	protected void setName(String[] tokens) {
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i < tokens.length; i++) {
			builder.append(tokens[i]);
			builder.append(" ");
		}
		
		name = builder.toString().trim();
	}
	
	public String getName() {
		return name;
	}
	
}
