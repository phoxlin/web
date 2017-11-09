package com.core.smart.helper;

import com.core.smart.annotation.Action;
import com.core.smart.http.request.Handler;
import com.core.smart.http.request.Request;
import com.core.smart.tools.ArrayUtil;
import com.core.smart.tools.CollectionUtil;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 * Created by Administrator on 2017/11/9.
 */
public final class ControllerHelper {

    private static final Map<Request,Handler> ACTION_MAP = new HashMap<>();

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

}
