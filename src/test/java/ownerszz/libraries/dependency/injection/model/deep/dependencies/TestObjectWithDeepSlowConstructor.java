package ownerszz.libraries.dependency.injection.model.deep.dependencies;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.DependencyCreation;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;
import ownerszz.libraries.dependency.injection.model.TestObjectWithSlowConstructor;

@Dependency
public class TestObjectWithDeepSlowConstructor {
    private final TestObjectWithSlowConstructor objectWithSlowConstructor;
    private final TestObjectWithDeepDependencies testObjectWithDeepDependencies;

    @ResolveDependencies
    public TestObjectWithDeepSlowConstructor(TestObjectWithSlowConstructor objectWithSlowConstructor, TestObjectWithDeepDependencies testObjectWithDeepDependencies) throws Exception {
        this.objectWithSlowConstructor = objectWithSlowConstructor;
        this.testObjectWithDeepDependencies = testObjectWithDeepDependencies;
    }

    public TestObjectWithSlowConstructor getObjectWithSlowConstructor() {
        return objectWithSlowConstructor;
    }

    public TestObjectWithDeepDependencies getTestObjectWithDeepDependencies() {
        return testObjectWithDeepDependencies;
    }
}
