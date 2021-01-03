package ownerszz.libraries.dependency.injection.distrib;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.DependencyLifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Dependency(runnable = true, lifecycle = DependencyLifecycle.SINGLETON)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Skeleton {
}
