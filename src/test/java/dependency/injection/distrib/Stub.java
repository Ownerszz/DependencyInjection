package dependency.injection.distrib;

import dependency.injection.core.Dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Dependency
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Stub {
    String skeletonAddress();
    int skeletonPort();
    String resultAddress() default "";
    int resultPort() default -1;
}