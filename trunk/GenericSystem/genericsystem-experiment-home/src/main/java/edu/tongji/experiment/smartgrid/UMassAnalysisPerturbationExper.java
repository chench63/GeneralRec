/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.smartgrid.AnalysisPerturbationEngine;
import edu.tongji.util.ExceptionUtil;

/**
 * UMass数据分析
 * 
 * @author Hanke Chen
 * @version $Id: UMassAnalysisExper.java, v 0.1 2014-2-15 下午2:28:35 chench Exp $
 */
public final class UMassAnalysisPerturbationExper {

    /**
     * 引导函数
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/smartgrid/umass/umass-analysis-perturbation.xml");
            AnalysisPerturbationEngine engine = (AnalysisPerturbationEngine) ctx.getBean("engine");
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
