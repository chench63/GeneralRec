/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * 简单文件处理工具，读取文件中所有行。
 * 
 * @author chenkh
 * @version $Id: FileUtil.java, v 0.1 2013-11-26 上午11:16:49 chenkh Exp $
 */
public final class FileUtil {

    /**
     * 禁用构造函数
     */
    private FileUtil() {

    }

    /**
     * 简单读取文件，
     * 返回文件所有行，且去掉空格.
     * 
     * @param path   文件路径
     * @return
     */
    public static String[] readlines(String path) {
        File file = new File(path);

        //读取并解析数据
        if (!file.isFile() | !file.exists()) {
            ExceptionUtil.caught(new FileNotFoundException("File Not Found"), "读取文件发生异常，校验文件路径: "
                                                                              + path);
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            List<String> testCaseSet = new ArrayList<String>();
            String context = null;
            while ((context = reader.readLine()) != null) {
                testCaseSet.add(StringUtil.trim(context));
            }

            return testCaseSet.toArray(new String[testCaseSet.size()]);
        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + path);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }

        //出现异常，返回null
        return null;
    }
}
