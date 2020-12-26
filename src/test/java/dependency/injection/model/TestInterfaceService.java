package dependency.injection.model;

import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;

import java.util.Collection;

@Dependency
public class TestInterfaceService {
    private final Collection<TestInterface> testInterfaces;

    @ResolveDependencies
    public TestInterfaceService(Collection<TestInterface> testInterfaces) {
        this.testInterfaces = testInterfaces;
    }

    public Collection<TestInterface> getTestInterfaces() {
        return testInterfaces;
    }
}
