package ownerszz.libraries.dependency.injection.model;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;

@Dependency
public class TestObjectWithNonResolvableSelfReferencingConstructor {
    private TestObjectWithNonResolvableSelfReferencingConstructor t;
    @ResolveDependencies
    public TestObjectWithNonResolvableSelfReferencingConstructor(TestObjectWithNonResolvableSelfReferencingConstructor t) {
        this.t = t;
    }
}
