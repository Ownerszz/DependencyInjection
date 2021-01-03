package ownerszz.libraries.dependency.injection.core;

import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;

import java.lang.annotation.*;

/**
 * Root annotation for our container.
 * Supply classes with this annotation so that they will get resolved.
 * If you want your {@link Dependency} to run after initialising then set {@link Dependency#runnable()} to true
 * and make sure that your {@link Dependency} implements {@link Runnable}
 *
 * All classes marked with {@link Dependency} or a "inheriting" annotation
 * will get injected to the constructor if the constructor is marked with {@link ResolveDependencies}
 * @see DependencyLifecycle
 * @see AnnotationScanner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Dependency {
    boolean runnable() default false;
    DependencyLifecycle lifecycle() default DependencyLifecycle.TRANSIENT;
}
