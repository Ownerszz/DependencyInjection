package ownerszz.libraries.dependency.injection.core;

/**
 * How the instance is created.
 *
 * {@link DependencyCreation#WARM} means that the instance will be created using the current thread
 * and that the dependencies have to wait for another to get instantiated.
 *
 * {@link DependencyCreation#COLD} means that the instance will be created using in a separate thread
 * and that the constructor gets injected with a {@link ownerszz.libraries.dependency.injection.core.cold.dependency.ColdDependency ColdDependency}.
 *
 * Cold dependencies can be seen as proxied dependencies as they will at creating extend the {@link Dependency supplied type}.
 */
public enum DependencyCreation {
    WARM,
    COLD
}
