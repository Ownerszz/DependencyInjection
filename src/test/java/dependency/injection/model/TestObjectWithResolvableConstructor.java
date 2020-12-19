package dependency.injection.model;

import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;

@Dependency
public class TestObjectWithResolvableConstructor {
    private final TestObject testObject;
    private final TestObjectWithDefaultConstructor testObjectWithDefaultConstructor;
    @ResolveDependencies
    public TestObjectWithResolvableConstructor(TestObject testObject, TestObjectWithDefaultConstructor testObjectWithDefaultConstructor) {
        this.testObject = testObject;
        this.testObjectWithDefaultConstructor = testObjectWithDefaultConstructor;
    }
}
