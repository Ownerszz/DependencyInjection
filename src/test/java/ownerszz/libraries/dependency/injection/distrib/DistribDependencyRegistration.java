package ownerszz.libraries.dependency.injection.distrib;

import be.kdg.distrib.skeletonFactory.SkeletonFactory;
import ownerszz.libraries.dependency.injection.core.DependencyManager;
import ownerszz.libraries.dependency.injection.core.DependencyRegistrator;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;

@DependencyRegistrator
public class DistribDependencyRegistration {
    @ResolveDependencies
    public DistribDependencyRegistration(DependencyManager dependencyManager) throws Exception {
        //dependencyManager.toString();
        dependencyManager.registerPoxyOnAnnotation(Skeleton.class, SkeletonFactory::createSkeleton);
        /*dependencyManager.registerPoxyOnAnnotation(Stub.class, interfaze ->{
            Stub ann = AnnotationScanner.getAnnotation((Class<?>) interfaze, Stub.class);
            if(ann.resultAddress().equals("")){
                return StubFactory.createStub((Class<?>) interfaze, ann.skeletonAddress(), ann.skeletonPort());
            }else {
                return StubFactory.createStub((Class<?>) interfaze, ann.skeletonAddress(), ann.skeletonPort(), ann.resultAddress(),ann.resultPort());
            }
        } );*/
    }
}
