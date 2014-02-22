/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import java.util.List;

/**
 * 
 * @author chench
 * @version $Id: GnuplotUtil.java, v 0.1 2014-2-17 下午1:21:55 chench Exp $
 */
public final class GnuplotUtil {

    public final static String HEADER            = "###File starts###\n"
                                                   + "#The following data is generatied by some regex.\n";

    public final static String END               = "\n\n###File completes ###";

    public final static char   ELEMENT_SEPERATOR = '\t';

    public final static char   BREAK_LINE        = '\n';

    public static void genDataFile(List<String> stream, int columnSize, String absolutePath) {
        //0.写入头部
        StringBuilder context = new StringBuilder(HEADER);

        //1.输出坐标轴部分信息
        //包含X轴信息和列数据;
        context.append(stream.get(0));
        context.append(BREAK_LINE);

        //2.输出文本数据
        int rowSeq = 0;
        for (int i = 1; i < stream.size(); i++) {
            //输出完一行时，追加换行符号
            if ((i - 1) % columnSize == 0) {
                context.append(BREAK_LINE).append(rowSeq++).append(ELEMENT_SEPERATOR);
            }

            //输出一个数据后，最佳原始分隔符
            context.append(stream.get(i)).append(ELEMENT_SEPERATOR);
        }
        context.append(END);

        //3.序列化至磁盘文件
        FileUtil.write(absolutePath, context.toString());
    }
}
