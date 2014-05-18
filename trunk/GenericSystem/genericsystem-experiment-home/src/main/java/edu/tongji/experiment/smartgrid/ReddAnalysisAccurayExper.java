/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.smartgrid.AnalysisAccuracyEngine;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author chench
 * @version $Id: ReddAnalysisPerturbationExper.java, v 0.1 18 May 2014 10:55:54 chench Exp $
 */
public final class ReddAnalysisAccurayExper {

    /**
     * 引导函数
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/smartgrid/umass/umass-analysis-accuracy.xml");
            AnalysisAccuracyEngine engine = (AnalysisAccuracyEngine) ctx.getBean("engine");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, UMassAnalysisPerturbationExper.class + " 发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
