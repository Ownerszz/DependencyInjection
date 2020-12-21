package dependency.injection.core;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Dependency {
    boolean runnable() default false;
    DependencyLifecycle lifecycle() default DependencyLifecycle.TRANSIENT;
}
