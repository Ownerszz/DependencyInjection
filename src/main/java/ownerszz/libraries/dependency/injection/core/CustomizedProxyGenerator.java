package ownerszz.libraries.dependency.injection.core;

/*import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;*/

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.objenesis.ObjenesisHelper;

public class CustomizedProxyGenerator {
    /**
     *
     * @param interfaze the interfaze
     * @return the an instance that implements the given interface
     */
    public static <T> T createInterfaceInstance(Class<T> interfaze){
        Class<? extends T> implClass = new ByteBuddy()
                                        .subclass(interfaze)
                                        .make()
                                        .load(interfaze.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                                        .getLoaded();

        return ObjenesisHelper.newInstance(implClass);
    }
}
