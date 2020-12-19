package dependency.injection.model;

import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;

@Dependency
public class TestObjectWithNonResolvableConstructor {
    private final Object object;
    @ResolveDependencies
    public TestObjectWithNonResolvableConstructor(Object object) {
        this.object = object;
    }
}
