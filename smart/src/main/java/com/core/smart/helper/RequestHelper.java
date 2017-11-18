package com.core.smart.helper;

import com.core.smart.http.request.FormParam;
import com.core.smart.http.request.Param;
import com.core.smart.tools.ArrayUtil;
import com.core.smart.tools.CodecUtil;
import com.core.smart.tools.StreamUtil;
import com.core.smart.tools.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 请求助手类
 * Created by Administrator on 2017/11/17.
 */
public final class RequestHelper {

    /**
     * 创建请求对象
     */
    public static Param createParam(HttpServletRequest request)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));

        return new Param(formParamList);
    }

    private static List<FormParam> parseParameterNames(HttpServletRequest request){
        List<FormParam> formParamList = new ArrayList<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()){
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if (ArrayUtil.isNotEmpty(fieldValues)){
                Object fieldValue;
                if (fieldValues.length==1){
                    fieldValue = fieldValues[0];
                }else{
                    StringBuilder sb = new StringBuilder("");
                    for (int i=0;i<fieldValues.length;i++){
                        sb.append(fieldValues[i]);
                        if (i!=fieldValues.length-1){
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = sb.toString();
                }
                formParamList.add(new FormParam(fieldName,fieldValue));
            }
        }

        return formParamList;
    }

    private static List<FormParam> parseInputStream(HttpServletRequest request)throws  IOException{
        List<FormParam> formParamList = new ArrayList<>();
        String body = CodecUtil.decodeUrl(StreamUtil.getString(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)){
            String[] kvs = StringUtil.splitString(body,"&");
            if (ArrayUtil.isNotEmpty(kvs)){
                for (String kv: kvs) {
                    String[] array = StringUtil.splitString(kv,"=");
                    if (ArrayUtil.isNotEmpty(array)){
                        formParamList.add(new FormParam(array[0],array[1]));
                    }
                }
            }
        }

        return formParamList;
    }

}
