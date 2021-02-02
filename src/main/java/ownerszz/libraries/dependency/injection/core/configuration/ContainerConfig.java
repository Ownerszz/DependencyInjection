package ownerszz.libraries.dependency.injection.core.configuration;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.DependencyLifecycle;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Dependency(lifecycle = DependencyLifecycle.SINGLETON)
public @interface ContainerConfig {
}
