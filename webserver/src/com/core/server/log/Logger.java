package com.core.server.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class Logger {
    private static Log log;

    static {
        PropertyConfigurator.configure(Utils.getRootClassPath() + "log4j.properties");
        log = LogFactory.getLog("fw");
    }

    public Logger() {
    }

    public static final void info(Object message) {
        log.info(message);
    }

    public static final void debug(Object message) {
        String msg = String.valueOf(message);
        if(msg.length() > 400) {
            msg = msg.substring(0, 400);
            msg = msg + "<太长隐藏显示>.......";
        }

        log.debug(msg);
    }

    public static final void error(Object message) {
        if(message instanceof Exception) {
            log.error(Utils.getErrorStack((Exception)message));
        } else {
            log.error(message);
        }

    }

    public static final void warn(Object message) {
        log.warn(message);
    }

    public static final void fatal(Object message) {
        log.fatal(message);
    }

    public static final void info(Object message, Object... objs) {
        String message1 = String.format(String.valueOf(message), objs);
        log.info(message1);
    }

    public static final void debug(Object message, Object... objs) {
        String message1 = String.format(String.valueOf(message), objs);
        debug(message1);
    }

    public static final void error(Object message, Object... objs) {
        if(message instanceof Exception) {
            log.error(Utils.getErrorStack((Exception)message));
        } else {
            String message1 = String.format(String.valueOf(message), objs);
            log.error(message1);
        }

    }

    public static final void warn(Object message, Object... objs) {
        String message1 = String.format(String.valueOf(message), objs);
        log.warn(message1);
    }

    public static final void fatal(Object message, Object... objs) {
        String message1 = String.format(String.valueOf(message), objs);
        log.fatal(message1);
    }
}
