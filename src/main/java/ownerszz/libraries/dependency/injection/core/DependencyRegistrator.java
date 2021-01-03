package ownerszz.libraries.dependency.injection.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a class with {@link DependencyRegistrator} to register your own framework to this one.
 * @see Dependency
 * @see DependencyManager#invokeRegistrators()
 */
@Dependency
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DependencyRegistrator {
}
