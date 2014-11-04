package edu.tongji.experiment.recommendation;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author Hanke
 * @version $Id: MovieLensAnchorSVDExper.java, v 0.1 2014-11-4 下午10:24:24 Exp $
 */
public final class MovieLensAnchorSVDExper {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/anchor/anchorRcmd.xml");
            Engine engine = (Engine) ctx.getBean("anchorRcmd");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, MoiveLensMixtureSVDExper.class + " 发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
