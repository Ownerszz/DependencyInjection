package ownerszz.libraries.dependency.injection.model.deep.dependencies;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;
import ownerszz.libraries.dependency.injection.model.TestObject;
import ownerszz.libraries.dependency.injection.model.TestObjectWithDefaultConstructor;
import ownerszz.libraries.dependency.injection.model.TestObjectWithResolvableConstructor;

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
