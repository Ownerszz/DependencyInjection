package dependency.injection.model;

import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;

@Dependency
public class TestObjectWithNonResolvableSelfReferencingConstructor {
    private TestObjectWithNonResolvableSelfReferencingConstructor t;
    @ResolveDependencies
    public TestObjectWithNonResolvableSelfReferencingConstructor(TestObjectWithNonResolvableSelfReferencingConstructor t) {
        this.t = t;
    }
}
