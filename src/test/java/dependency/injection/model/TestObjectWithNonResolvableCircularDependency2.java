package dependency.injection.model;

import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;

@Dependency
public class TestObjectWithNonResolvableCircularDependency2 {
    private final TestObjectWithNonResolvableCircularDependency1 t;

    @ResolveDependencies
    public TestObjectWithNonResolvableCircularDependency2(TestObjectWithNonResolvableCircularDependency1 t) {
        this.t = t;
    }
}
