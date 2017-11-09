package com.core.smart.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Json 工具类
 * Created by Administrator on 2017/11/9.
 */
public final class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * POJO 转 JSON
     */
    public static <T> String toJson(T obj){
        String json;
        try{
            json = OBJECT_MAPPER.writeValueAsString(obj);
        }catch (Exception e){
            LOGGER.error("convert POJO to JSON failure",e);
            throw new RuntimeException(e);
        }

        return json;
    }

    /**
     * JSON 转 POJO
     */
    public static <T> T fromJson(String json,Class<T> type){
        T obj;
        try{
            obj = OBJECT_MAPPER.readValue(json,type);
        }catch (Exception e){
            LOGGER.error("convert JSON  to POJO failure",e);
            throw new RuntimeException(e);
        }

        return obj;
    }


}
