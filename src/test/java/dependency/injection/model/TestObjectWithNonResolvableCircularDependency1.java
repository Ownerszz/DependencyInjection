package dependency.injection.model;

import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;

@Dependency
public class TestObjectWithNonResolvableCircularDependency1 {
    private final TestObjectWithNonResolvableCircularDependency2 t;

    @ResolveDependencies
    public TestObjectWithNonResolvableCircularDependency1(TestObjectWithNonResolvableCircularDependency2 t) {
        this.t = t;
    }
}
