package com.mozu.base.utils;

import java.lang.reflect.Method;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mozu.api.ApiContext;

public class MozuAppLoggerWrapper {
    
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = null;
        
        try {
            // see if the class is loaded
            URL url = clazz.getResource("/com/mozu/logger/MozuAppLogger.class");
            if (url!=null) {
                Class<?> loggerClass = Class.forName("com.mozu.logger.MozuAppLogger");
                Class<?>[] cArg = new Class[1];
                cArg[0] = Class.class;
                Method init = loggerClass.getMethod("getLogger", cArg);
                logger = (Logger) init.invoke(null, clazz);
            } else {
                logger = LoggerFactory.getLogger(clazz);
            }
        } catch (Exception e) {
            logger = LoggerFactory.getLogger(clazz);
        }
        
        return logger;
    }
    
    public static void setLoggerContext(ApiContext apiContext) {
        try {
            // see if the class is loaded
            URL url = MozuAppLoggerWrapper.class.getResource("/com/mozu/logger/LoggerContextManager.class");
            if (url!=null) {
                Class<?> loggerContextClass = Class.forName("com.mozu.logger.LoggerContextManager");
                Class<?>[] cArg = new Class[1];
                cArg[0] = ApiContext.class;
                Method setContext = loggerContextClass.getMethod("setApiContext", cArg);
                Object[] args = new Object[1];
                args[0]=apiContext;
                setContext.invoke(null, args);
            }
        } catch (Exception e) {
        }
    }

    public static void clearLoggerContext() {
        try {
            // see if the class is loaded
            URL url = MozuAppLoggerWrapper.class.getResource("/com/mozu/logger/LoggerContextManager.class");
            if (url!=null) {
                Class<?> loggerContextClass = Class.forName("com.mozu.logger.LoggerContextManager");
                Method setContext = loggerContextClass.getMethod("endThread", new Class[0]);
                setContext.invoke(null, new Object[0]);
            }
        } catch (Exception e) {
        }
    }
}
