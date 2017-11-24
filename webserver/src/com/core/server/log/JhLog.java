package com.core.server.log;

import com.core.server.Action;
import com.core.server.db.impl.EntityImpl;
import org.bson.types.ObjectId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class JhLog {
    private String id = (new ObjectId()).toString();
    private Action action;
    private String actionStr;
    private String method;
    private List<String> logs = new ArrayList();
    private boolean slience = false;
    private boolean hasErrOrWarning = false;
    private boolean hasErr = false;
    private long s = System.currentTimeMillis();

    public JhLog() {
    }

    public JhLog(Action action, String method) {
        this.action = action;
        this.method = method;
        this.actionStr = this.action.getClass().getName();
    }

    private final void storeDB(Connection conn) {
        try {
            EntityImpl e = new EntityImpl("sys_log", conn);
            e.setSlience(true);
            e.setValue("aId", this.id);
            e.setValue("action", this.actionStr);
            e.setValue("method", this.method);
            if(this.hasErr) {
                e.setValue("level_type", "2E");
            } else if(this.hasErrOrWarning) {
                e.setValue("level_type", "3W");
            } else {
                e.setValue("level_type", "4I");
            }

            e.setValue("log", Utils.getListString(this.logs, "<br/>"));
            e.setValue("log_time", new Date());
            e.setValue("slience", Boolean.valueOf(this.slience));
            e.create();
        } catch (Exception var3) {
            Logger.error(var3);
        }

    }

    public StringBuilder getLineInfo() {
        StackTraceElement[] ses = (new Throwable()).getStackTrace();
        StringBuilder sb = new StringBuilder();
        int len = ses.length;
        if(len > 5) {
            boolean var9 = true;
        }

        for(int i = ses.length; i > 0; --i) {
            StackTraceElement ste = (new Throwable()).getStackTrace()[i - 1];
            if(ste.getLineNumber() > 0) {
                String clz = ste.getClassName();
                if(!clz.startsWith("com.jinhua.") && !clz.startsWith("java") && !clz.startsWith("sun") && !clz.startsWith("javax") && !clz.startsWith("org") && !clz.startsWith("com.sun")) {
                    sb.append(clz);
                    sb.append(", Line");

                    try {
                        sb.append(Utils.leftPadding(Integer.valueOf(ste.getLineNumber()), 5, " "));
                    } catch (Exception var8) {
                        ;
                    }

                    sb.append(":  ");
                    break;
                }
            }
        }

        return sb;
    }

    public final void store(Connection conn) {
        try {
            long e = System.currentTimeMillis();
            this.debug("Log(" + this.getId() + ") stored,.................ok(" + (e - this.s) + "ms)");
            if(Resources.DEVELOPMENT) {
                if(this.hasErr && Resources.DBLog) {
                    this.storeDB(conn);
                }
            } else if(this.hasErrOrWarning || Resources.DBLog) {
                this.storeDB(conn);
            }
        } catch (Exception var4) {
            ;
        }

        this.logs.clear();
    }

    public final void info(Object message) {
        StringBuilder sb = this.getLineInfo().append(message);
        if(!this.slience || Resources.DEVELOPMENT) {
            Logger.info(sb);
        }

        this.logs.add("4I" + sb);
    }

    public final void debug(Object message) {
        StringBuilder sb = this.getLineInfo().append(message);
        if(!this.slience || Resources.DEVELOPMENT) {
            Logger.debug(sb.toString());
        }

        this.logs.add("5D" + sb);
    }

    public final void error(Object message) {
        this.hasErrOrWarning = true;
        this.hasErr = true;
        if(message instanceof InvocationTargetException) {
            Throwable sb = ((InvocationTargetException)message).getTargetException();
            String sb1 = getErrorStack((Throwable)sb);
            StringBuilder sb2 = this.getLineInfo().append(sb1);
            Logger.error(sb2);
            this.logs.add("2E" + sb2);
        } else if(message instanceof Exception) {
            String sb3 = getErrorStack((Exception)((Exception)message));
            StringBuilder sb5 = this.getLineInfo().append(sb3);
            Logger.error(sb5);
            this.logs.add("2E" + sb5);
        } else {
            StringBuilder sb4 = this.getLineInfo().append(message);
            Logger.error(sb4);
            this.logs.add("2E" + sb4);
        }

    }

    public final void warn(Object message) {
        this.hasErrOrWarning = true;
        StringBuilder sb = this.getLineInfo().append(message);
        if(!this.slience || Resources.DEVELOPMENT) {
            Logger.warn(sb);
        }

        this.logs.add("3W" + sb);
    }

    public final void fatal(Object message) {
        this.hasErrOrWarning = true;
        this.hasErr = true;
        StringBuilder sb = this.getLineInfo().append(message);
        if(!this.slience || Resources.DEVELOPMENT) {
            Logger.fatal(sb);
        }

        this.logs.add("1F" + sb);
    }

    public final void info(Object message, Object... objs) {
        String message1 = String.format(String.valueOf(message), objs);
        StringBuilder sb = this.getLineInfo().append(message1);
        if(!this.slience || Resources.DEVELOPMENT) {
            Logger.info(sb);
        }

        this.logs.add("4I" + sb);
    }

    public final void debug(Object message, Object... objs) {
        String message1 = String.format(String.valueOf(message), objs);
        StringBuilder sb = this.getLineInfo().append(message1);
        if(!this.slience || Resources.DEVELOPMENT) {
            Logger.debug(sb);
        }

        this.logs.add("5D" + sb);
    }

    public final void error(Object message, Object... objs) {
        this.hasErrOrWarning = true;
        this.hasErr = true;
        if(message instanceof InvocationTargetException) {
            Throwable sb = ((InvocationTargetException)message).getTargetException();
            String sb1 = getErrorStack((Throwable)sb);
            StringBuilder sb2 = this.getLineInfo().append(sb1);
            Logger.error(sb2);
            this.logs.add("2E" + sb2);
        } else if(message instanceof Exception) {
            String sb3 = getErrorStack((Exception)((Exception)message));
            StringBuilder sb5 = this.getLineInfo().append(sb3);
            Logger.error(sb5);
            this.logs.add("2E" + sb5);
        } else {
            String message1 = String.format(String.valueOf(message), objs);
            StringBuilder sb4 = this.getLineInfo().append(message1);
            Logger.error(sb4);
            this.logs.add("2E" + sb4);
        }

    }

    public final void warn(Object message, Object... objs) {
        this.hasErrOrWarning = true;
        String message1 = String.format(String.valueOf(message), objs);
        StringBuilder sb = this.getLineInfo().append(message1);
        if(!this.slience || Resources.DEVELOPMENT) {
            Logger.warn(sb);
        }

        this.logs.add("3W" + sb);
    }

    public final void fatal(Object message, Object... objs) {
        this.hasErrOrWarning = true;
        this.hasErr = true;
        String message1 = String.format(String.valueOf(message), objs);
        StringBuilder sb = this.getLineInfo().append(message1);
        if(!this.slience || Resources.DEVELOPMENT) {
            Logger.fatal(sb);
        }

        this.logs.add("1F" + sb);
    }

    public static String getErrorStack(Throwable e) {
        String error = "";
        if(e != null) {
            ByteArrayOutputStream baos = null;
            PrintStream ps = null;

            String var6;
            try {
                baos = new ByteArrayOutputStream();
                ps = new PrintStream(baos);
                e.printStackTrace(ps);
                var6 = baos.toString();
            } catch (Exception var14) {
                error = e.toString();
                return error;
            } finally {
                try {
                    if(baos != null) {
                        baos.close();
                        baos = null;
                    }
                } catch (IOException var13) {
                    ;
                }

                if(ps != null) {
                    ps.close();
                    ps = null;
                }

            }

            return var6;
        } else {
            return error;
        }
    }

    public static String getErrorStack(Exception e) {
        String error = null;
        if(e != null) {
            ByteArrayOutputStream baos = null;
            PrintStream ps = null;

            try {
                baos = new ByteArrayOutputStream();
                ps = new PrintStream(baos);
                e.printStackTrace(ps);
                error = baos.toString();
            } catch (Exception var13) {
                error = e.toString();
            } finally {
                if(baos != null) {
                    try {
                        baos.close();
                    } catch (IOException var12) {
                        ;
                    }

                    baos = null;
                }

                if(ps != null) {
                    ps.close();
                }

                ps = null;
            }
        }

        return error;
    }

    public String getId() {
        return this.id;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getLogs() {
        return this.logs;
    }
}
