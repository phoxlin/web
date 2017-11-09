package com.core.smart.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 编码与解码
 * Created by Administrator on 2017/11/9.
 */
public final class CodecUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodecUtil.class);

    /**
     * 编码URL
     */
    public static String encodeUrl(String source)
    {
        String target;
        try {
            target = URLEncoder.encode(source,"UTF-8");
        }catch (Exception e){
            LOGGER.error("encode url failure",e);
            throw new RuntimeException(e);
        }

        return target;
    }

    /**
     * 解码URL
     */
    public static String decodeUrl(String source)
    {
        String target;
        try {
            target = URLDecoder.decode(source, "UTF-8");
        }catch (Exception e){
            LOGGER.error("decode url failure",e);
            throw new RuntimeException(e);
        }

        return target;
    }
}
