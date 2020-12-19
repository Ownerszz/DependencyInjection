package dependency.injection.core;

import be.kdg.distrib.skeletonFactory.Skeleton;
import dependency.injection.core.DependencyManager;
import dependency.injection.distrib.SkeletonImpl;
import dependency.injection.distrib.Stubbed;
import dependency.injection.model.TestObject;
import dependency.injection.model.TestObjectWithResolvableConstructor;
import dependency.injection.model.deep.dependencies.TestObjectWithDeepDependencies;
import dependency.injection.util.UsableClassesGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DependencyManagerTest {

    @BeforeClass
    public static void setup() throws Exception {
        DependencyManager.use(UsableClassesGenerator.generateConstructors());

    }

    @Test
    public void createSimpleInstance() throws Exception {
        TestObject testObject = (TestObject) DependencyManager.createInstance(TestObject.class);
        assertNotNull(testObject);
    }
    @Test
    public void createInstanceWithDependencies() throws Exception{
        TestObjectWithResolvableConstructor test = (TestObjectWithResolvableConstructor) DependencyManager.createInstance(TestObjectWithResolvableConstructor.class);
        assertNotNull(test);
    }

    @Test
    public void createInstanceWithDeepDependencies() throws Exception{
        TestObjectWithDeepDependencies test = (TestObjectWithDeepDependencies) DependencyManager.createInstance(TestObjectWithDeepDependencies.class);
        assertNotNull(test);
    }

    @Test
    public void createSkeleton() throws Exception{
        Skeleton skeleton = (Skeleton) DependencyManager.createInstance(SkeletonImpl.class);
        assertNotNull(skeleton);
    }

    @Test
    public void createStub() throws Exception{
        Stubbed stubbed = (Stubbed) DependencyManager.createInstance(Stubbed.class);
        assertNotNull(stubbed);
    }

    @Test
    public void run() throws Exception{
        DependencyManager.run(false);
    }
}