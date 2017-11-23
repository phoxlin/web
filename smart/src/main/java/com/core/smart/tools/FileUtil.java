package com.core.smart.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 文件操作工具类
 * Created by Administrator on 2017/11/17.
 */
public final class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 获取真实文件名
     */
    public static String getRealFileName(String fileName){
        return FilenameUtils.getName(fileName);
    }

    /**
     * 创建文件
     */
    public static File createFile(String filePath){
        File file;
        try {
            file = new File(filePath);
            File parenDir = file.getParentFile();
            if (!parenDir.exists()){
                FileUtils.forceMkdir(parenDir);
            }
        }catch (Exception e){
            LOGGER.error("create file failure",e);
            throw new RuntimeException(e);
        }

        return file;
    }
}
