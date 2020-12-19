package dependency.injection.model.deep.dependencies;

import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;
import dependency.injection.model.TestObject;
import dependency.injection.model.TestObjectWithDefaultConstructor;
import dependency.injection.model.TestObjectWithResolvableConstructor;

@Dependency
public class TestObjectWithDeepDependencies {
    private final TestObjectWithDependencies testObjectWithDependencies;
    private final TestObject object;
    private final TestObjectWithDefaultConstructor defaultConstructor;
    private final TestObjectWithResolvableConstructor resolvableConstructor;

    @ResolveDependencies
    public TestObjectWithDeepDependencies(TestObjectWithDependencies testObjectWithDependencies, TestObject object, TestObjectWithDefaultConstructor defaultConstructor, TestObjectWithResolvableConstructor resolvableConstructor) {
        this.testObjectWithDependencies = testObjectWithDependencies;
        this.object = object;
        this.defaultConstructor = defaultConstructor;
        this.resolvableConstructor = resolvableConstructor;
    }
}
