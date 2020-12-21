package dependency.injection.distrib;

import be.kdg.distrib.skeletonFactory.Skeleton;
import be.kdg.distrib.stubFactory.StubFactory;
import dependency.injection.core.DependencyManager;
import dependency.injection.util.UsableClassesGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;


public class DistribTest {

    @BeforeClass
    public static void setup() throws Exception {
        String name = DistribDependencyRegistration.class.getName();
        Class clazz = Thread.currentThread().getContextClassLoader().loadClass(name);
        DependencyManager.use(UsableClassesGenerator.generateConstructors());
        DependencyManager.forceRegisterClass(clazz);
        DependencyManager.invokeRegistrators();

    }

    @Test
    public void runSkeleton() throws Exception {
        be.kdg.distrib.skeletonFactory.Skeleton skeleton = (Skeleton) DependencyManager.createInstance(SkeletonImpl.class);
        assertNotNull(skeleton.getAddress());
        skeleton.run();
    }

    @Test
    public void makeSkeletonHandleRequest() throws Exception{
        be.kdg.distrib.skeletonFactory.Skeleton skeleton = (Skeleton) DependencyManager.createInstance(SkeletonImpl.class);
        skeleton.run();
        Stubbed stubbed = (Stubbed) StubFactory.createStub(Stubbed.class,skeleton.getAddress().getIpAddress(),skeleton.getAddress().getPortNumber());
        stubbed.setS("hey");
        assertEquals("hey",stubbed.getS());
    }

    @Test
    public void createSkeleton() throws Exception{
        Skeleton skeleton = (Skeleton) DependencyManager.createInstance(SkeletonImpl.class);
        assertNotNull(skeleton);
    }

    @Test
    public void skeletonIsSingleton() throws Exception{
        Skeleton skeleton = (Skeleton) DependencyManager.createInstance(SkeletonImpl.class);
        Skeleton skeleton2 = (Skeleton) DependencyManager.createInstance(SkeletonImpl.class);
        assertEquals(skeleton.getAddress(), skeleton2.getAddress());
    }

    @Test
    public void createStub() throws Exception{
        Stubbed stubbed = (Stubbed) DependencyManager.createInstance(Stubbed.class);
        assertNotNull(stubbed);
    }

    @AfterClass
    public static void tearDown(){
        DependencyManager.refreshContext();
    }

}
