package ownerszz.libraries.dependency.injection.logging;

import org.slf4j.Logger;
import ownerszz.libraries.dependency.injection.core.arguments.ArgumentReader;

public class ContainerLogger {

    public static void logInfo(Logger logger, String message,Object... args){
        if(isLoggingEnabled()){
            logger.info(message,args);
        }
    }
    public static void logError(Logger logger, String message, Object... args){
        if(isLoggingEnabled()){
            logger.error(message,args);
        }
    }
    public static void logWarn(Logger logger, String message, Object... args){
        if(isLoggingEnabled()){
            logger.warn(message,args);
        }
    }
    public static void logDebug(Logger logger, String message, Object... args){
        if(isLoggingEnabled()){
            logger.debug(message,args);
        }
    }

    private static boolean isLoggingEnabled(){
        return ArgumentReader.getValueFromArgumentsAs("loggingEnabled",boolean.class);
    }
}
