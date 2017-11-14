package com.core.smart.http;

import com.core.smart.helper.BeanHelper;
import com.core.smart.helper.ConfigHelper;
import com.core.smart.helper.ControllerHelper;
import com.core.smart.helper.HelperLoader;
import com.core.smart.http.request.Handler;
import com.core.smart.http.request.MethodParam;
import com.core.smart.http.request.Param;
import com.core.smart.http.response.Data;
import com.core.smart.http.response.View;
import com.core.smart.tools.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求转发器
 * Created by Administrator on 2017/11/9.
 */
@WebServlet(urlPatterns = "/*",loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet{
    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化Helper类
        HelperLoader.init();

        ServletContext servletContext = config.getServletContext();
        //注册 处理jsp的 Servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath()+"*");
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+"*");
    }

    public  void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String requestMethod = request.getMethod().toLowerCase();
        String requestPath = request.getPathInfo();

        Handler handler = ControllerHelper.getHandler(requestMethod,requestPath);
        MethodParam methodParam = ControllerHelper.getMethodParam(requestMethod,requestPath);
        if (handler!=null){
            /*if (methodParam!=null){
                if (methodParam.getIsNeed()){
                    methodParam.getParamName();
                }
            }*/

            //获取Controller类及其实例
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);
            Map<String,Object> paramMap = new HashMap<>();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()){
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                paramMap.put(paramName,paramValue);
            }
            String body = CodecUtil.encodeUrl(StreamUtil.getString(request.getInputStream()));
            if (StringUtil.isNotEmpty(body)){
                String[] params = body.split("&");
                if (ArrayUtil.isNotEmpty(params)){
                    for(String param :params){
                        String[] array = param.split("=");
                        if (ArrayUtil.isNotEmpty(array)&&array.length==2){
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName,paramValue);
                        }
                    }
                }
            }
            Param param = new Param(paramMap);
            Object result;
            Method actionMethod = handler.getActionMethod();
            if (param.isEmpty()){
                 result = ReflectionUtil.invokeMethod(controllerBean,actionMethod);
            }else
                 result = ReflectionUtil.invokeMethod(controllerBean,actionMethod,param);
            //处理返回值
            if(result instanceof View){
                View view = (View)result;
                String path = view.getPath();
                if (StringUtil.isNotEmpty(path)){
                    if (path.startsWith("/")){
                        response.sendRedirect(request.getContextPath()+path);
                    }else {
                        Map<String,Object> model = view.getModel();
                        for(Map.Entry<String,Object> entry:model.entrySet()){
                            request.setAttribute(entry.getKey(),entry.getValue());
                            request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);
                        }
                    }
                }
            }else if(result instanceof Data){
                Data data = (Data)result;
                Object model = data.getModel();
                if (model!=null){
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter writer = response.getWriter();
                    String json = JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }
        }

    }



}
