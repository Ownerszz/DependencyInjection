package ownerszz.libraries.dependency.injection.distrib;

import ownerszz.libraries.dependency.injection.core.ResolveDependencies;
import ownerszz.libraries.dependency.injection.model.TestObject;
import ownerszz.libraries.dependency.injection.model.deep.dependencies.TestObjectWithDeepDependencies;

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
