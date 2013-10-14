/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author chenkh
 * @version $Id: TestRatingEngine.java, v 0.1 2013-10-8 下午3:52:18 chenkh Exp $
 */
public class TestRatingEngine {

    @Test
    public void test() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "META-INF/spring/application-context-netflix-statistic.xml");
            Engine engine = (Engine) ctx.getBean("engine");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, "edu.tongji.engine.TestEngine 测试用例发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
