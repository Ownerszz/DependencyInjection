package dependency.injection.model.deep.dependencies;

import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;
import dependency.injection.model.TestObjectWithResolvableConstructor;
@Dependency
public class TestObjectWithDependencies {
    private final TestObjectWithResolvableConstructor resolvableConstructor;
    @ResolveDependencies
    public TestObjectWithDependencies(TestObjectWithResolvableConstructor resolvableConstructor) {
        this.resolvableConstructor = resolvableConstructor;
    }

}
