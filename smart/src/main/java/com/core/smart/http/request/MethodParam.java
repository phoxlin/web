package com.core.smart.http.request;

/**
 * Action方法上的参数
 * Created by Administrator on 2017/11/14.
 */
public class MethodParam {
    private String paramName;
    private Object paramType;
    private Boolean isNeed;

    public MethodParam(String paramName, Object paramType, Boolean isNeed) {
        this.paramName = paramName;
        this.paramType = paramType;
        this.isNeed = isNeed;
    }

    public String getParamName() {
        return paramName;
    }

    public Object getParamType() {
        return paramType;
    }

    public Boolean getIsNeed() {
        return isNeed;
    }
}
