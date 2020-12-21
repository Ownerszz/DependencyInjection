package dependency.injection.distrib;

import be.kdg.distrib.skeletonFactory.SkeletonFactory;
import be.kdg.distrib.stubFactory.StubFactory;
import dependency.injection.annotation.scanner.AnnotationScanner;
import dependency.injection.core.DependencyManager;
import dependency.injection.core.DependencyRegistrator;
import dependency.injection.core.ResolveDependencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DependencyRegistrator
public class DistribDependencyRegistration {
    @ResolveDependencies
    public DistribDependencyRegistration(DependencyManager dependencyManager) throws Exception {
        //dependencyManager.toString();
        dependencyManager.registerPoxyOnAnnotation(Skeleton.class, SkeletonFactory::createSkeleton);
        dependencyManager.registerPoxyOnAnnotation(Stub.class, interfaze ->{
            Stub ann = AnnotationScanner.getAnnotation(interfaze.getClass(), Stub.class);
            if(ann.resultAddress().equals("")){
                return StubFactory.createStub(interfaze.getClass().getInterfaces()[0], ann.skeletonAddress(), ann.skeletonPort());
            }else {
                return StubFactory.createStub(interfaze.getClass(), ann.skeletonAddress(), ann.skeletonPort(), ann.resultAddress(),ann.resultPort());
            }
        } );
    }
}
