package ownerszz.libraries.dependency.injection.distrib;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.ResolveDependencies;

@Dependency
public class SampleClient implements Runnable {
    private SkeletonImpl skeleton;
    private Stubbed stub;

    @ResolveDependencies
    public SampleClient(SkeletonImpl skeleton, Stubbed stub) {
        this.skeleton = skeleton;
        this.stub = stub;
    }

    @Override
    public void run() {

        stub.setS("Client said hi!");
        System.out.println(stub.getS());
    }
}
