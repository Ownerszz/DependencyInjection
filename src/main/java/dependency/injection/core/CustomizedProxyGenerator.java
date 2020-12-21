package dependency.injection.core;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

public class CustomizedProxyGenerator {
    public static<T> T createInterfaceInstance(Class<T> interfaze){
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(Thread.currentThread().getContextClassLoader());
        enhancer.setCallbackType(NoOp.class);
        enhancer.setSuperclass(interfaze);
        enhancer.setInterfaces(new Class[]{interfaze});
        enhancer.setCallback(NoOp.INSTANCE);
        enhancer.setUseCache(false);
        enhancer.setUseFactory(false);
        enhancer.setInterceptDuringConstruction( false );
        //Class<T> temp = (Class<T>) enhancer.createClass();
        //T instance = (T) enhancer.create();
        return  (T) enhancer.create();
    }
}
