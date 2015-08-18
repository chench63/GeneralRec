package paper.www16.experiment;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

public class BFRecExp {
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //        runBFC();
        runFastBFC();
    }

    public static void runBFC() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext("experiment/recommendation/bfcrec/bfcRcmd.xml");
            Engine engine = (Engine) ctx.getBean("mixtureRcmd");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, BFRecExp.class + " 发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public static void runFastBFC() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext("experiment/recommendation/bfcrec/bfcRcmd.xml");
            Engine engine = (Engine) ctx.getBean("fastRcmd");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, BFRecExp.class + " 发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
