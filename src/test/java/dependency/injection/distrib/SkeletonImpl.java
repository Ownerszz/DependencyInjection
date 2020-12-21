package dependency.injection.distrib;

import dependency.injection.core.Dependency;
import dependency.injection.core.DependencyLifecycle;
import dependency.injection.core.ResolveDependencies;
import dependency.injection.model.TestObject;
import dependency.injection.model.deep.dependencies.TestObjectWithDeepDependencies;

@Skeleton
public class SkeletonImpl {
    private final TestObject testObject;
    private final TestObjectWithDeepDependencies deepDependencies;
    private String s;

    @ResolveDependencies
    public SkeletonImpl(TestObject testObject, TestObjectWithDeepDependencies deepDependencies) {
        this.testObject = testObject;
        this.deepDependencies = deepDependencies;
    }


    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
