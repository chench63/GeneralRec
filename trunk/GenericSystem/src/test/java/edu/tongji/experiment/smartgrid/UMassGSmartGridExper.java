/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author chenkh
 * @version $Id: UMassGSmartGridExper.java, v 0.1 2013-12-17 下午6:35:04 chenkh Exp $
 */
public final class UMassGSmartGridExper {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/smartgrid/umass/umass-one-time-pad.xml");
            Engine engine = (Engine) ctx.getBean("engine");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, "edu.tongji.experiment.smartgrid.UMassGSmartGridExper 发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
