package ownerszz.libraries.dependency.injection.model.deep.dependencies;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;
import ownerszz.libraries.dependency.injection.model.TestObjectWithResolvableConstructor;
@Dependency
public class TestObjectWithDependencies {
    private final TestObjectWithResolvableConstructor resolvableConstructor;
    @ResolveDependencies
    public TestObjectWithDependencies(TestObjectWithResolvableConstructor resolvableConstructor) {
        this.resolvableConstructor = resolvableConstructor;
    }

}
