package vazkii.quark.base.module;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fabricmc.api.EnvType;
import org.apache.commons.lang3.text.WordUtils;
import org.objectweb.asm.Type;

import com.google.common.collect.Lists;

import org.quiltmc.loader.api.QuiltLoader;
import vazkii.quark.base.Quark;

public final class ModuleFinder {

	private static final Type LOAD_MODULE_TYPE = Type.getType(LoadModule.class);
	private static final Pattern MODULE_CLASS_PATTERN = Pattern.compile("vazkii\\.quark\\.(?:content|addons)\\.(\\w+)\\.module.\\w+Module");

	private final Map<Class<? extends QuarkModule>, QuarkModule> foundModules = new LinkedHashMap<>();

	// Modified to fit Quilt in QuiltQuark
	public void findModules() {
		Annotation[] annotations = QuiltLoader.getModContainer(Quark.MOD_ID).get().getClassLoader().getDefinedPackage("vazkii.quark").getAnnotations();
		Arrays.stream(annotations)
				.filter(annotationData -> LOAD_MODULE_TYPE.equals(Type.getType(annotationData.annotationType())))
				.sorted(Comparator.comparing(d -> d.getClass().getName()))
				.forEach(annotation -> {
					if (annotation instanceof LoadModule moduleAnnotation) {
						loadModule(moduleAnnotation);
					}
				});
	}

	// Modified to fit Quilt in QuiltQuark
	@SuppressWarnings("unchecked")
	private void loadModule(LoadModule annotation) {
		try {
			Type type = Type.getType(annotation.annotationType());
			String name = type.getClassName();
			
			Matcher m = MODULE_CLASS_PATTERN.matcher(name);
			if(!m.matches())
				throw new RuntimeException("Invalid module name " + name);
			
			Class<?> clazz = Class.forName(name, false, Quark.class.getClassLoader());
			Quark.LOG.info("Found Quark module class " + name);
			
			QuarkModule moduleObj = (QuarkModule) clazz.getDeclaredConstructor().newInstance();

			ModuleCategory category = annotation.category();

			String categoryName = category.name;
			String packageName = m.group(1);
			if (!categoryName.equals(packageName))
				throw new RuntimeException("Module " + name + " is defined in " + packageName + " but in category " + categoryName);
			
			if (category.isAddon()) {
				String mod = category.requiredMod;
				if(mod != null && !mod.isEmpty() && !QuiltLoader.isModLoaded(mod))
					moduleObj.missingDep = true;
			}

			if (annotation.name() != null)
				moduleObj.displayName = annotation.name();
			else
				moduleObj.displayName = WordUtils.capitalizeFully(clazz.getSimpleName().replaceAll("Module$", "").replaceAll("(?<=.)([A-Z])", " $1"));
			moduleObj.lowercaseName = moduleObj.displayName.toLowerCase(Locale.ROOT).replaceAll(" ", "_");

			if (annotation.description() != null)
				moduleObj.description = annotation.description();

			if (annotation.antiOverlap() != null)
				moduleObj.antiOverlap = List.of(annotation.antiOverlap());

			if (annotation.hasSubscriptions())
				moduleObj.hasSubscriptions = true;

			if (annotation.subscribeOn() != null) {
				Set<EnvType> subscribeTargets = EnumSet.noneOf(EnvType.class);

				List<EnvType> holders = Arrays.stream(annotation.subscribeOn()).toList();
				subscribeTargets.addAll(holders);

				moduleObj.subscriptionTarget = Lists.newArrayList(subscribeTargets);
			}

			if (annotation.enabledByDefault())
				moduleObj.enabledByDefault = true;

			category.addModule(moduleObj);
			moduleObj.category = category;

			foundModules.put((Class<? extends QuarkModule>) clazz, moduleObj);
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException("Failed to load Module " + annotation.name(), e);
		}
	}

	// Redundant in QuiltQuark
	/*private ModuleCategory getOrMakeCategory(ModAnnotation.EnumHolder category) {
		return ModuleCategory.valueOf(category.getValue());
	}*/

	public Map<Class<? extends QuarkModule>, QuarkModule> getFoundModules() {
		return foundModules;
	}

}
