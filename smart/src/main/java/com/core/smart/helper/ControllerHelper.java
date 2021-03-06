package com.core.smart.helper;

import com.core.smart.annotation.Action;
import com.core.smart.http.request.Handler;
import com.core.smart.http.request.MethodParam;
import com.core.smart.http.request.Request;
import com.core.smart.tools.ArrayUtil;
import com.core.smart.tools.CollectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 * Created by Administrator on 2017/11/9.
 */
public final class ControllerHelper {

    private static final Map<Request,Handler> ACTION_MAP = new HashMap<>();
    private static final Map<Request,MethodParam> METHOD_MAP = new HashMap<>();

    private static final Map<Request,Parameter> NO_ANNOTATION_METHOD_PARAM_MAP = new HashMap<>();

    static {
        //Action注解方法的请求处理等
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)){
            for(Class<?> controllerClass : controllerClassSet){
                Method[] methods = controllerClass.getMethods();
                if (ArrayUtil.isNotEmpty(methods)){
                    for (Method method : methods){
                        //判断controller类的方法是否有Action注解
                        if(method.isAnnotationPresent(Action.class)){
                            //从Action注解中获取URL规则
                            Action action =  method.getAnnotation(Action.class);
                            String mapping = action.value();
                            //System.out.println("===========Action mapping:"+mapping);
                            if (mapping.matches("\\w+:/\\w*")){
                                String[] array = mapping.split(":");
                                if (ArrayUtil.isNotEmpty(array)&&array.length==2){
                                    String requestMethod = array[0];
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod,requestPath);
                                    Handler handler = new Handler(controllerClass,method);
                                    ACTION_MAP.put(request,handler);

                                    //参数
                                    Parameter[] parameters = method.getParameters();
                                    for (Parameter parameter : parameters){
                                        if (parameter.isAnnotationPresent(com.core.smart.annotation.Parameter.class)){
                                            com.core.smart.annotation.Parameter p = parameter.getAnnotation(com.core.smart.annotation.Parameter.class);
                                            if (p.value()){
                                                MethodParam methodParam = new MethodParam(parameter.getName(),parameter.getType(),p.value());
                                                METHOD_MAP.put(request,methodParam);
                                            }
                                        }else {
                                            NO_ANNOTATION_METHOD_PARAM_MAP.put(request,parameter);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取Handler
     */
    public static Handler getHandler(String requestMethod,String requestPath)
    {
        Request request = new Request(requestMethod,requestPath);
        return ACTION_MAP.get(request);
    }

    /**
     * 获取Action方法带注解的参数
     */
    public static MethodParam getMethodParam(String requestMethod,String requestPath){
        Request request = new Request(requestMethod,requestPath);
        return METHOD_MAP.get(request);
    }

    /**
     * 获取Action方法不带注解的参数
     */
    public static Parameter getMethodNoAnnotationParam(String requestMethod,String requestPath){
        Request request = new Request(requestMethod,requestPath);
        return NO_ANNOTATION_METHOD_PARAM_MAP.get(request);
    }

}
