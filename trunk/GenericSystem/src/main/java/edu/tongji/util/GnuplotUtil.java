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

    public static void genDataFile(List<String> stream, int stockSize, String absolutePath) {
        //写入头部
        StringBuilder context = new StringBuilder(HEADER);

        for (int i = 0; i < stream.size(); i++) {
            //输出完一行时，追加换行符号
            if (i % stockSize == 0) {
                context.append(BREAK_LINE);
            }

            //输出一个数据后，最佳原始分隔符
            context.append(stream.get(i)).append(ELEMENT_SEPERATOR);
        }

        context.append(END);
        FileUtil.write(absolutePath, context.toString());
    }

    public static void plot(int length, int widh) {

    }
}
