package ownerszz.libraries.dependency.injection.model.deep.dependencies;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.DependencyCreation;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;

@Dependency(creationType = DependencyCreation.COLD)
public class TestObjectChainedSlowConstructor {
    private final TestObjectWithDeepSlowConstructor testObjectWithDeepSlowConstructor;
    @ResolveDependencies
    public TestObjectChainedSlowConstructor(TestObjectWithDeepSlowConstructor testObjectWithDeepSlowConstructor) {
        this.testObjectWithDeepSlowConstructor = testObjectWithDeepSlowConstructor;
    }

    public TestObjectWithDeepSlowConstructor getTestObjectWithDeepSlowConstructor() {
        return testObjectWithDeepSlowConstructor;
    }
}
