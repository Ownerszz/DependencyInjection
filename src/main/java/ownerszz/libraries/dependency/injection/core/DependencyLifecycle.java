package ownerszz.libraries.dependency.injection.core;

/**
 * The lifecycle of an dependency.
 * Mark a {@link Dependency} as {@link DependencyLifecycle#SINGLETON} if you want that only 1 instance of that dependency will ever exist
 * or until {@link DependencyManager#refreshContext()} is called.
 *
 * Mark a {@link Dependency} as {@link DependencyLifecycle#SCOPED} if you want that only 1 instance of that dependency will exist withing a scope.
 * Due to current limitation the container will not automatically create scoped instances and completely lets you control the scopes.
 *
 * Mark a {@link Dependency} as {@link DependencyLifecycle#SINGLETON} if you want that the container to always create a new instance
 */
public enum DependencyLifecycle {
    TRANSIENT,
    SCOPED,
    SINGLETON
}
