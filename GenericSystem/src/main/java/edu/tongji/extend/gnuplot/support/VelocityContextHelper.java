/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.velocity.VelocityContext;

import edu.tongji.extend.gnuplot.AssembleTemplate;

/**
 * Velocity 工具类，初始化上下文和工具注入
 * 
 * @author chench
 * @version $Id: VelocityContextHelper.java, v 0.1 1 Apr 2014 17:38:05 chench Exp $
 */
public class VelocityContextHelper {

    /** 工具类集合，通过Spring配置注入 */
    private Map<String, Object> utilBeansMap = new HashMap<String, Object>();

    /**
     * 填充<code>VelocityContext</code>，包括业务数据对象和相关的工具类
     * 
     * @param template          报文配置模板
     * @param genericMessage    标准的Velocity模板通用参数的数据结构
     * @return
     */
    public VelocityContext fillContext(AssembleTemplate template, GenericMessage genericMessage) {

        VelocityContext context = new VelocityContext();

        fillMessageContext(context, genericMessage);

        fillUtilContext(context);

        fillExContext(context, template);

        return context;
    }

    /**
     * 报文数据上下文
     * 
     * @param context
     * @param genericMessage    标准的Velocity模板通用参数的数据结构
     */
    private void fillMessageContext(VelocityContext context, GenericMessage genericMessage) {
        context.put("m", genericMessage);
    }

    /**
     * 填充工具类上下文
     * 
     * @param context
     */
    private void fillUtilContext(VelocityContext context) {
        Set<Entry<String, Object>> entrySet = utilBeansMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            context.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 补充额外的上下文信息，主要是一些额外的模板
     * 
     * @param context
     * @param messageTemplate
     */
    private void fillExContext(VelocityContext context, AssembleTemplate assembleTemplate) {

    }

    /**
     * Setter method for property <tt>utilBeansMap</tt>.
     * 
     * @param utilBeansMap value to be assigned to property utilBeansMap
     */
    public void setUtilBeansMap(Map<String, Object> utilBeansMap) {
        this.utilBeansMap = utilBeansMap;
    }

}
