package com.core.smart.http.request;

import com.core.smart.tools.CastUtil;
import com.core.smart.tools.CollectionUtil;

import java.util.Map;

/**
 * 请求参数
 * Created by Administrator on 2017/11/9.
 */
public class Param {
    private Map<String,Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public boolean isEmpty(){
        return CollectionUtil.isEmpty(paramMap);
    }

    public long getLong(String name){
        return CastUtil.castLong(paramMap.get(name));
    }

    public int getInt(String name){
        return CastUtil.castInt(paramMap.get(name));
    }

    public double getDouble(String name){
        return CastUtil.castDouble(paramMap.get(name));
    }

    public Map<String,Object> getMap(){
        return paramMap;
    }
}
