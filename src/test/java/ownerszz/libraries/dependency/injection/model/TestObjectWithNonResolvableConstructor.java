package ownerszz.libraries.dependency.injection.model;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;

@Dependency
public class TestObjectWithNonResolvableConstructor {
    private final Object object;
    @ResolveDependencies
    public TestObjectWithNonResolvableConstructor(Object object) {
        this.object = object;
    }
}
