package dependency.injection.distrib;

import be.kdg.distrib.skeletonFactory.Skeleton;
import dependency.injection.core.Dependency;
import dependency.injection.core.ResolveDependencies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
