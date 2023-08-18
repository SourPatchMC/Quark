package vazkii.quark.content.tweaks.client.emote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.content.tweaks.module.EmotesModule;

@ClientOnly
public class CustomEmoteIconResourcePack extends AbstractPackResources {

	private final List<String> verifiedNames = new ArrayList<>();
	private final List<String> existingNames = new ArrayList<>();

	public CustomEmoteIconResourcePack() {
		super(EmotesModule.emotesDir);
	}

	@NotNull
	@Override
	public Set<String> getNamespaces(@NotNull PackType type) {
		if (type == PackType.CLIENT_RESOURCES)
			return ImmutableSet.of(EmoteHandler.CUSTOM_EMOTE_NAMESPACE);
		return ImmutableSet.of();
	}

	@NotNull
	@Override
	protected InputStream getResource(@NotNull String name) throws IOException {
		if(name.equals("pack.mcmeta"))
			return Quark.class.getResourceAsStream("/proxypack.mcmeta");

		if(name.equals("pack.png"))
			return Quark.class.getResourceAsStream("/proxypack.png");

		File file = getFile(name);
		if(!file.exists())
			throw new FileNotFoundException(name);

		return new FileInputStream(file);
	}

	@NotNull
	@Override
	public Collection<ResourceLocation> getResources(@NotNull PackType type, @NotNull String pathIn, @NotNull String idk, @NotNull Predicate<ResourceLocation> filter) {
		File rootPath = new File(this.file, type.getDirectory());
		List<ResourceLocation> allResources = Lists.newArrayList();

		for (String namespace : this.getNamespaces(type))
			this.crawl(new File(new File(rootPath, namespace), pathIn), 32, namespace, allResources, pathIn + "/", filter);

		return allResources;
	}

	private void crawl(File rootPath, int maxDepth, String namespace, List<ResourceLocation> allResources, String path, Predicate<ResourceLocation> filter) {
		File[] files = rootPath.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					if (maxDepth > 0)
						this.crawl(file, maxDepth - 1, namespace, allResources, path + file.getName() + "/", filter);
				} else if (!file.getName().endsWith(".mcmeta") && filter.test(new ResourceLocation(namespace, path + file.getName()))) {
					try {
						allResources.add(new ResourceLocation(namespace, path + file.getName()));
					} catch (ResourceLocationException e) {
						Quark.LOG.error(e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void close() {
		// NO-OP
	}

	@Override
	protected boolean hasResource(@NotNull String name) {
		if(!verifiedNames.contains(name)) {
			File file = getFile(name);
			if(file.exists())
				existingNames.add(name);
			verifiedNames.add(name);
		}

		return existingNames.contains(name);
	}

	private File getFile(String name) {
		String filename = name.substring(name.indexOf(":") + 1) + ".png";
		filename = filename.replaceAll("(.+/)+", "");
		
		return new File(EmotesModule.emotesDir, filename);
	}

	@Override
	public boolean isHidden() {
		return true;
	}

	@NotNull
	@Override
	public String getName() {
		return "quark-emote-pack";
	}




}
