/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package paper.sigir15.experiment;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: MoiveLensStandardSVDExper.java, v 0.1 2014-10-7 下午7:52:08 chench Exp $
 */
public final class WEMARecExper {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/wemarec/wemaRcmd.xml");
            Engine engine = (Engine) ctx.getBean("mixtureRcmd");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, WEMARecExper.class + " 发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
