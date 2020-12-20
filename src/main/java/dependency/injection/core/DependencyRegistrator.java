package dependency.injection.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Dependency
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DependencyRegistrator {
}
