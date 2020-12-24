package dependency.injection.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation to mark a constructor within a {@link Dependency dependency}.
 * The constructor will be invoked with all necessary {@link Dependency dependencies}
 * when {@link DependencyManager#createInstance(Class)} or {@link DependencyManager#createSimpleInstance(Class)} is called
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface ResolveDependencies {
}
