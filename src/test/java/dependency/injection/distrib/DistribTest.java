package dependency.injection.distrib;

import be.kdg.distrib.skeletonFactory.Skeleton;
import be.kdg.distrib.stubFactory.StubFactory;
import dependency.injection.core.DependencyManager;
import dependency.injection.util.UsableClassesGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;


public class DistribTest {

    @BeforeClass
    public static void setup() throws Exception {
        DependencyManager.use(UsableClassesGenerator.generateConstructors());
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


}
