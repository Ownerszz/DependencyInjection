package ownerszz.libraries.dependency.injection.model;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;

@Dependency
public class TestObjectWithNonResolvableCircularDependency1 {
    private final TestObjectWithNonResolvableCircularDependency2 t;

    @ResolveDependencies
    public TestObjectWithNonResolvableCircularDependency1(TestObjectWithNonResolvableCircularDependency2 t) {
        this.t = t;
    }
}
