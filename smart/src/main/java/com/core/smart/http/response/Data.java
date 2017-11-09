package com.core.smart.http.response;

/**
 * 返回数据对象
 * Created by Administrator on 2017/11/9.
 */
public class Data {
    private Object model;

    public Data(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}
