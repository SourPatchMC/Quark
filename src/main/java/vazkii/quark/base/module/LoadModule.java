package vazkii.quark.base.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.fabricmc.api.EnvType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadModule {

	ModuleCategory category();
	
	String name() default "";
	String description() default "";
	String[] antiOverlap() default { };

	boolean hasSubscriptions() default false;
	EnvType[] subscribeOn() default {EnvType.CLIENT, EnvType.SERVER};

	boolean enabledByDefault() default true;
	
}
