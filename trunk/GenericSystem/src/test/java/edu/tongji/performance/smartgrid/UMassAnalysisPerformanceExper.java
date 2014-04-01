/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.performance.smartgrid;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.smartgrid.SmartGridEngine;
import edu.tongji.experiment.smartgrid.UMassOneTimePadExper;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author chench
 * @version $Id: UMassAnalysisPerformanceExper.java, v 0.1 2014-3-18 上午10:14:27 chench Exp $
 */
public class UMassAnalysisPerformanceExper {

    @Test
    public void test() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "performance/smartgrid/umass/umass-analysis.xml");
            SmartGridEngine engine = (SmartGridEngine) ctx.getBean("analysisEngine");
            engine.excute();

        } catch (Exception e) {
            ExceptionUtil.caught(e, UMassOneTimePadExper.class + " 发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
