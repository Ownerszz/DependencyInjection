package dependency.injection.distrib;

import dependency.injection.core.Dependency;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Dependency(runnable = true)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Skeleton {
}
