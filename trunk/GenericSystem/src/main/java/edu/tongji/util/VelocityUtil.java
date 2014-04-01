/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Velocity 渲染工具类，模板本地载入<br>
 * 底层工具类不消灭异常，由上层显式处理
 * 
 * @author chench
 * @version $Id: VelocityUtil.java, v 0.1 1 Apr 2014 17:49:17 chench Exp $
 */
public final class VelocityUtil {

    /**
     * 根据报文模板和<code>VelocityContext</code>，渲染出实际的业务报文
     * 
     * @param context 业务上下文信息
     * @param template 模板
     * @return 
     * @throws IOException 
     */
    public static String evaluate(VelocityContext context, String template) throws IOException {
        Writer writer = new StringWriter();
        try {
            Velocity.evaluate(context, writer, StringUtil.EMPTY_STRING, template);
            return writer.toString();
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
