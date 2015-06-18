package paper.tkde.exp;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import paper.sigir15.exp.WEMARecExp;
import edu.tongji.engine.Engine;
import edu.tongji.util.ExceptionUtil;

public class AccuracyFrmwrkExp {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //        doWEMAREC();
        doUserBased();
    }

    public static void doWEMAREC() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext("experiment/recommendation/tkde/wemaRcmd.xml");
            Engine engine = (Engine) ctx.getBean("mixtureRcmd");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, WEMARecExp.class + " 发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public static void doUserBased() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/tkde/fUserBasedRcmd.xml");
            Engine engine = (Engine) ctx.getBean("mixtureRcmd");
            engine.excute();
        } catch (Exception e) {
            ExceptionUtil.caught(e, WEMARecExp.class + " 发生致命错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
