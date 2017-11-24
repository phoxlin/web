package com.core.server;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2017/11/24.
 */
public class CentralServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RouteMapping routeMapping = new RouteMapping();

    public CentralServlet() {
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = request.getServletPath();
        String url = path.split("\\?")[0];
        if(url.contains(".") && "GET".equals(request.getMethod())) {
            Logger.warn("注意发现未处理的静态文件：" + url);
        } else if(url.equals("/")) {
            Logger.warn("注意未处理默认的根路径：" + url);
        } else {
            String[] p = url.split("/");
            Class[] cs = null;
            String key = p[1];
            ArrayList pars = new ArrayList();
            cs = new Class[p.length - 2];
            int j = 0;

            int parsSize;
            for(parsSize = 2; parsSize < p.length; ++parsSize) {
                pars.add(p[parsSize]);
                cs[j++] = String.class;
            }

            parsSize = pars.size();
            RouteObj obj = null;
            Map _params = request.getParameterMap();
            HashMap params = new HashMap();
            params.putAll(_params);
            params.put("_REQUEST_METHOD_", new String[]{request.getMethod()});
            HttpMethod method = HttpMethod.GET;
            if("GET".equals(request.getMethod())) {
                method = HttpMethod.GET;
                obj = this.routeMapping.getRouteObj(HttpMethod.GET, key, parsSize);
            } else if("POST".equals(request.getMethod())) {
                method = HttpMethod.POST;
                obj = this.routeMapping.getRouteObj(HttpMethod.POST, key, parsSize);
            } else if("PUT".equals(request.getMethod())) {
                method = HttpMethod.PUT;
                obj = this.routeMapping.getRouteObj(HttpMethod.PUT, key, parsSize);
            } else if("DELETE".equals(request.getMethod())) {
                method = HttpMethod.DELETE;
                obj = this.routeMapping.getRouteObj(HttpMethod.DELETE, key, parsSize);
            } else {
                Logger.warn("Unknown request method:" + request.getMethod());
            }

            boolean isConn = Utils.isTrue(request.getParameter("conn"));
            boolean isMDB = Utils.isTrue(request.getParameter("mdb"));
            boolean found = true;
            if(obj == null) {
                obj = new RouteObj();
                found = false;
            } else if(!isConn) {
                isConn = obj.isConn();
            }

            if(found) {
                this.execute(cs, pars.toArray(new String[pars.size()]), params, obj, isConn, isMDB, request, response);
            } else {
                this.executeDefaultAction(url, method, (String[])pars.toArray(new String[pars.size()]), params, obj, isConn, isMDB, request, response);
            }
        }

    }

    public void executeDefaultAction(String key, HttpMethod method, String[] pars, Map<String, String[]> params, RouteObj routeObj, boolean isConn, boolean isMDB, HttpServletRequest request, HttpServletResponse response) throws IOException {
        BasicAction act = new BasicAction();
        act.request = request;
        act.response = response;
        if(routeObj.getType() != ContentType.JPG) {
            act.out = response.getWriter();
        }

        JSONObject json = new JSONObject();
        json.put("rs", "Y");
        DBM db = new DBM();
        Connection conn = null;

        try {
            if(isConn) {
                if(routeObj.isSlave()) {
                    conn = db.getSlaveConnection();
                } else {
                    conn = db.getConnection();
                }

                conn.setAutoCommit(false);
                act.setConnection(conn);
            }

            act.setParameters(params);
            act.setObj(json);
            if(method != HttpMethod.GET && method != HttpMethod.POST) {
                if(method == HttpMethod.PUT) {
                    act.L = new JhLog(act, "defaultPut");
                    act.L.warn("没有找到对应的route注册信息【" + key + "】,调用系统默认的【 com.jinhua.server.BasicAction】控制器【" + method + "】->defaultPut(String[] params)方法");
                    act.defaultPut(pars);
                } else if(method == HttpMethod.DELETE) {
                    act.L = new JhLog(act, "defaultDelete");
                    act.L.warn("没有找到对应的route注册信息【" + key + "】,调用系统默认的【com.jinhua.server.BasicAction】控制器【" + method + "】->defaultDelete(String[] params)方法");
                    act.defaultDelete(pars);
                }
            } else {
                act.L = new JhLog(act, "defaultGetPost");
                act.L.warn("没有找到对应的route注册信息【" + key + "】,调用系统默认的【 com.jinhua.server.BasicAction】控制器【" + method + "】->defaultGetPost(String[] params)方法");
                act.defaultGetPost(pars);
            }

            if(isConn && conn != null) {
                conn.commit();
            }
        } catch (Exception var30) {
            if(conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException var29) {
                    ;
                }
            }

            act.L.error(var30);
            if(var30 instanceof InvocationTargetException) {
                Throwable ee = ((InvocationTargetException)var30).getTargetException();
                json.put("rs", ee.getMessage());
            } else {
                json.put("rs", var30.getMessage());
            }
        } finally {
            if(isConn) {
                try {
                    act.L.store(conn);
                } catch (Exception var28) {
                    ;
                }
            }

            act = null;
            db.freeConnection(conn);
            if(routeObj.getType() == ContentType.JSON) {
                try {
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().println(json.toString());
                } catch (IOException var27) {
                    ;
                }
            }

        }

    }

    public void execute(Class<String>[] cs, Object[] pars, Map<String, String[]> params, RouteObj routeObj, boolean isConn, boolean isMDB, HttpServletRequest request, HttpServletResponse response) {
        Action act = null;
        Method mt = null;
        Connection conn = null;
        long start = System.currentTimeMillis();
        long end = 0L;
        DBM db = new DBM();
        String nextPage = null;
        JSONObject json = new JSONObject();
        json.put("rs", "Y");
        long se = 0L;

        try {
            mt = routeObj.getAction().getMethod(routeObj.getMethod(), cs);
            act = (Action)routeObj.getAction().newInstance();
            act.L = new JhLog(act, mt.getName());
            act.request = request;
            act.response = response;
            if(routeObj.getType() != ContentType.JPG) {
                act.out = response.getWriter();
            }

            String e = act.getClass().getName();
            if(pars != null && pars.length > 0) {
                act.L.debug("Action【" + e + "】method【" + routeObj.getMethod() + "】 params【" + Utils.getListString(pars) + "】【" + (routeObj.isSlave()?"Slave":"Master") + "-" + Utils.getListString(routeObj.getM()) + "-" + routeObj.getType() + "】....(" + start + ")");
            } else {
                act.L.debug("Action【" + e + "】method【" + routeObj.getMethod() + "】【" + (routeObj.isSlave()?"Slave":"Master") + "-" + Utils.getListString(routeObj.getM()) + "-" + routeObj.getType() + "】....(" + start + ")");
            }

            if(isConn) {
                if(routeObj.isSlave()) {
                    conn = db.getSlaveConnection();
                } else {
                    conn = db.getConnection();
                }

                conn.setAutoCommit(false);
                act.setConnection(conn);
            }

            act.setParameters(params);
            act.L.debug("Parameters:");
            Iterator var23 = params.entrySet().iterator();

            while(var23.hasNext()) {
                Map.Entry ee1 = (Map.Entry)var23.next();
                act.L.debug("   =>" + (String)ee1.getKey() + ":" + Utils.getListString((Object[])ee1.getValue()));
            }

            act.setObj(json);
            mt.invoke(act, pars);
            if(isConn && conn != null) {
                conn.commit();
            }

            end = System.currentTimeMillis();
            se = end - start;
            act.L.debug("Finished Action(" + start + ") .... (" + se + "ms)");
            if(routeObj.getType() == ContentType.Forward) {
                try {
                    nextPage = act.obj.getString("nextpage");
                    if(nextPage == null || nextPage.length() <= 0) {
                        nextPage = request.getParameter("nextpage");
                    }
                } catch (Exception var45) {
                    nextPage = request.getParameter("nextpage");
                }

                if(nextPage != null && nextPage.length() > 0) {
                    if(!nextPage.startsWith("/")) {
                        nextPage = "/" + nextPage;
                    }

                    RequestDispatcher ee2 = request.getRequestDispatcher(nextPage);
                    ee2.forward(request, response);
                    act.L.debug("Forward to:" + nextPage);
                } else {
                    act.L.warn("没有找到对应的【nextpage】参数");
                }
            }
        } catch (Exception var46) {
            if(conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException var44) {
                    ;
                }
            }

            end = System.currentTimeMillis();
            se = end - start;
            act.L.debug("Finished Action(" + start + ") .... (" + se + "ms),with error:");
            act.L.error(var46);
            if(var46 instanceof InvocationTargetException) {
                Throwable ee = ((InvocationTargetException)var46).getTargetException();
                json.put("rs", ee.getMessage() == null?"null":ee.getMessage());
            } else {
                json.put("rs", var46.getMessage() == null?"null":var46.getMessage());
            }
        } finally {
            if(se > ActionMail.性能提醒时间) {
                try {
                    ActionMail actionMail = (ActionMail)Class.forName(ActionMail.actionMailClass).newInstance();
                    User u = new User();
                    u.setId(act.getSessionUser().getId());
                    u.setLoginName(act.getSessionUser().getLoginName());
                    actionMail.request = request;
                    actionMail.response = response;
                    actionMail.setUser(u);
                    actionMail.setEs(se);
                    actionMail.setStart(start);
                    actionMail.setEnd(end);
                    ArrayList actL = new ArrayList();
                    actL.addAll(act.L.getLogs());
                    actionMail.setContentList(actL);
                    actionMail.send();
                } catch (Exception var43) {
                    ;
                }
            }

            if(isConn) {
                try {
                    act.L.store(conn);
                } catch (Exception var42) {
                    ;
                }
            }

            act = null;
            db.freeConnection(conn);
            if(routeObj.getType() == ContentType.JSON || routeObj.getType() == ContentType.Forward && (nextPage == null || nextPage.length() <= 0)) {
                try {
                    response.getWriter().println(json.toString());
                } catch (IOException var41) {
                    ;
                }
            }

        }

    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if(DBUtils.getDBType() != DBType.MONGODB) {
            this.initServer();
        }

        this.regSystemActions();
    }

    private void initServer() {
        Utils.execute.execute(new Runnable() {
            public void run() {
                Jedis jd = null;
                Connection conn = null;
                DBM db = new DBM();

                try {
                    conn = db.getSlaveConnection();
                    conn.setAutoCommit(true);
                    EntityImpl backdb = new EntityImpl(conn);
                    jd = RedisUtils.getConnection();
                    if(jd != null) {
                        int classPath = backdb.executeQuery("select a.param_name,a.param_value,a.param_type from sys_param_info a");
                        if(classPath > 0) {
                            Pipeline thread = jd.pipelined();

                            for(int i = 0; i < classPath; ++i) {
                                String key = backdb.getStringValue("param_name", i);
                                String value = backdb.getStringValue("param_value", i);
                                String type = backdb.getStringValue("param_type", i);
                                if("02".equals(type)) {
                                    thread.hset(SystemUtils.PROJECT_NAME, key, Utils.getFileContent(Utils.getUploadFileInfo(value).getFile()));
                                } else {
                                    thread.hset(SystemUtils.PROJECT_NAME, key, value);
                                }
                            }

                            thread.sync();
                        }
                    } else {
                        Logger.error("Redis 服务器连接出错，请检查。。");
                    }
                } catch (Exception var18) {
                    Logger.error(var18);
                } finally {
                    RedisUtils.freeConnection(jd);
                    db.freeConnection(conn);
                }

                boolean var20 = Resources.getBooleanProperty("BACKUP_DATABASE", false);
                if(var20) {
                    try {
                        new DBTask();
                    } catch (Exception var17) {
                        var17.printStackTrace();
                    }
                }

                String var21 = Resources.getProperty("InitTask");

                try {
                    Thread var22 = (Thread)Class.forName(var21).newInstance();
                    var22.start();
                } catch (Exception var16) {
                    ;
                }

            }
        });
    }

    private void regSystemActions() {
        this.register(BasicAction.class, this.routeMapping, true);
        this.register(PageAction.class, this.routeMapping, true);
        this.register(QiniuAction.class, this.routeMapping, true);
        this.register(DesignerDBAction.class, this.routeMapping, true);
        this.register(DesignerQmAction.class, this.routeMapping, true);
        this.register(TaskAction.class, this.routeMapping, true);
        this.register(DBAction.class, this.routeMapping, true);
        this.register(WxAction.class, this.routeMapping, true);
        this.register(BasicLoginAction.class, this.routeMapping, true);
        this.scanActions();
    }

    private void scanActions() {
        ActionScaner scaner = new ActionScaner();
        scaner.scan();
        Set actions = scaner.getActions();
        Iterator var4 = actions.iterator();

        Class clss;
        while(var4.hasNext()) {
            clss = (Class)var4.next();
            this.register(clss, this.routeMapping, false);
        }

        var4 = scaner.getTasks().iterator();

        while(var4.hasNext()) {
            clss = (Class)var4.next();
            Task t = null;

            try {
                t = (Task)clss.newInstance();
                t.name = clss.getName();
                t.setRegulation(t.getRegulation());
            } catch (Exception var7) {
                Logger.error(var7);
            }
        }

    }

    public void register(Class clss, RouteMapping rm, boolean system) {
        Method[] mthods = clss.getMethods();
        Method[] var8 = mthods;
        int var7 = mthods.length;

        for(int var6 = 0; var6 < var7; ++var6) {
            Method m = var8[var6];
            Route r = (Route)m.getAnnotation(Route.class);
            if(r != null) {
                String url = r.value();
                String[] temp = url.split("/");
                if(temp != null && temp.length >= 2) {
                    url = temp[1];
                }

                RouteObj obj = new RouteObj();
                String aliase = r.aliase();
                if(aliase != null && aliase.length() > 0) {
                    String[] ps = aliase.split("/");
                    if(ps != null && ps.length >= 2) {
                        obj.setAliase(ps[1]);
                    }
                }

                Parameter[] var15 = m.getParameters();
                obj.setM(r.m());
                obj.setParamNum(Integer.valueOf(var15.length));
                obj.setUrl(url);
                obj.setAction(clss);
                obj.setMethod(m.getName());
                obj.setConn(r.conn());
                obj.setSlave(r.slave());
                obj.setType(r.type());
                obj.setSlience(r.slience());
                obj.setRealSlience(r.realSlience());
                if(!"/".equals(url) && !"QR".equals(url) && !"GetValidate".equals(url)) {
                    rm.addRoute(obj);
                    if(!system) {
                        Logger.debug("registed:" + obj);
                    }
                } else if(clss.getName().equals("com.jinhua.server.BasicAction")) {
                    rm.addRoute(obj);
                    if(!system) {
                        Logger.debug("registed:" + obj);
                    }
                }
            }
        }

    }
}
