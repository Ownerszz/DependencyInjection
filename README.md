# DependencyInjection

DependencyInjection is a Java library for dependency injection

## Installation

Use the jar supplied in this repo or build from source using maven install

## Main usages
I highly recommend to not use this library and instead use Java Spring or Google Guice as their framework is more refined and has better support
### Tired of writing new
``` java
public static void main(String[] args) throws Throwable {
        DependencyManager.run(true);
    }
    
@Dependency(runnable = true, lifecycle = DependencyLifecycle.SINGLETON)
public class Client implements Runnable {
    private final Server server;
    private final Document document;
    private final ClientFrame clientFrame;
    @ResolveDependencies
    public Client(Server server, DocumentImpl document) {
        this.server = server;
        this.document = document;
        this.clientFrame = new ClientFrame(document);
        document.setTextListener(clientFrame);
    }
    ...
```
### Not able to use Spring or Guice
Hopefully this library works for you :)

### Wanting to supply your own framework to other people that use this library (hopefully none)
``` java
@DependencyRegistrator
public class DistribRegistrator {
    @ResolveDependencies
    public DistribRegistrator(DependencyManager dependencyManager) throws Exception {
        dependencyManager.registerPoxyOnAnnotation(Skeleton.class, impl -> {
            Skeleton ann = AnnotationScanner.getAnnotation(impl.getClass(),Skeleton.class);
            return SkeletonFactory.createSkeleton(impl, ann.port());
        });
        dependencyManager.registerPoxyOnAnnotation(Stub.class, interfaze ->{
            Stub ann = AnnotationScanner.getAnnotation((Class<?>) interfaze, Stub.class);
            if(ann.resultAddress().equals("") && ann.resultClass() == Object.class){
                return StubFactory.createStub((Class<?>) interfaze, ann.skeletonAddress(), ann.skeletonPort());
            }else {
                if (ann.resultClass() != Object.class){
                    try {
                        be.kdg.distrib.skeletonFactory.Skeleton result = (be.kdg.distrib.skeletonFactory.Skeleton) DependencyManager.createInstance(ann.resultClass());
                        return StubFactory.createStub((Class<?>) interfaze,
                                ann.skeletonAddress(),
                                ann.skeletonPort(),
                                result.getAddress().getIpAddress(),
                                result.getAddress().getPortNumber());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }else {
                    return StubFactory.createStub((Class<?>) interfaze,
                            ann.skeletonAddress(),
                            ann.skeletonPort(),
                            ann.resultAddress(),
                            ann.resultPort());

                }
            }
        } );
    }
}
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
