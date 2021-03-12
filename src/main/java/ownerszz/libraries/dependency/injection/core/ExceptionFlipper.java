package ownerszz.libraries.dependency.injection.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ExceptionFlipper {
    public  static RuntimeException  flipException(Throwable e){
        Throwable cause = e;

        boolean isTop = false;
        do {
            if(cause.getCause() == null) {
                isTop = true;
            }else {
                cause = cause.getCause();
            }
        }while (!isTop);
        cause.setStackTrace(e.getStackTrace());
        return new RuntimeException(cause.getMessage(),cause);

    }
}
