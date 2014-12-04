/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.data.Model;
import edu.tongji.data.ModelGroup;
import edu.tongji.engine.recommendation.MixtureWLRARcmdEngine;
import edu.tongji.engine.recommendation.thread.WeightedSVDLearner;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: MoiveLensStandardSVDExper.java, v 0.1 2014-10-7 下午7:52:08 chench Exp $
 */
public final class WeightedSVDCoclusterAnalyzExper {

    /** result file*/
    public final static String    resultFilePrefix = "E:/MovieLens/ml-1m/Exper2_";

    /** */
    public final static String[]  param_divergence = { "cocluter_IW", "cocluter_EW" };

    /** */
    public final static String[]  param_C          = { "2", "5" };

    /** cocluster numbers*/
    public final static int[]     param_k          = { 2, 3, 4, 5 };

    /** rank of the Solution */
    public final static int[]     param_r          = { 5, 10, 15, 20 };

    public final static String[]  rootDirs         = { "E:/MovieLens/ml-1m/1/",
            "E:/MovieLens/ml-1m/2/", "E:/MovieLens/ml-1m/3/" };

    public final static boolean   isolation        = true;

    /** logger */
    protected final static Logger logger           = Logger
                                                       .getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        LoggerUtil.info(logger, "1. Start Main Engine.");
        for (String divergence : param_divergence) {
            //result file
            String file = resultFilePrefix + divergence;

            for (int k : param_k) {
                //result RMSE
                double[] RMSEs = new double[param_r.length];
                if (isolation) {
                    conductForRanks(divergence, k, RMSEs);
                } else {
                    conductForAccumCoclusters(divergence, k, RMSEs);
                }

                //record file
                StringBuilder record = (new StringBuilder()).append(k);
                for (double RMSE : RMSEs) {
                    record.append('\t').append(RMSE);
                }
                record.append('\n');
                FileUtil.writeAsAppend(file, record.toString());
            }
        }
    }

    public static void conductForRanks(String divergence, int k, double[] RMSEs) {
        int round = rootDirs.length;

        for (int index = 0; index < param_r.length; index++) {
            final int r = param_r[index];
            double RMSE = 0.0d;
            LoggerUtil.info(logger, "2+. K: " + k + "\tRank = " + r);

            for (String rootDir : rootDirs) {
                LoggerUtil.info(logger, "3+. Repeat: Root: " + rootDir);

                ClassPathXmlApplicationContext ctx = null;
                try {
                    ctx = new ClassPathXmlApplicationContext(
                        "experiment/recommendation/mixture/mixtureRcmd.xml");
                    MixtureWLRARcmdEngine engine = (MixtureWLRARcmdEngine) ctx
                        .getBean("mixtureRcmd");

                    //modify the test and training file
                    engine.setRootDir(rootDir);

                    //configure required models
                    List<ModelGroup> groups = engine.getGroups();
                    for (String constrain : param_C) {
                        String groupName = divergence + constrain + '_' + k + '_' + k;
                        ModelGroup group = (ModelGroup) ctx.getBean(groupName);
                        groups.add(group);
                    }

                    //modify the parameters of model
                    for (ModelGroup group : groups) {
                        for (Model model : group.getModels()) {
                            model.recmder.featureCount = r;
                        }
                    }
                    engine.excute();

                    //record RMSE
                    RMSE += WeightedSVDLearner.curRMSE;
                    LoggerUtil.info(logger, "3+. Divergence: " + divergence + "\tk: " + k + "\tr:"
                                            + r + "\tRepeat: RMSE: " + WeightedSVDLearner.curRMSE);
                } catch (Exception e) {
                    ExceptionUtil.caught(e, WeightedSVDCoclusterAnalyzExper.class + " 发生致命错误");
                } finally {
                    if (ctx != null) {
                        ctx.close();
                    }
                }
            }

            LoggerUtil.info(logger, "4+. Divergence: " + divergence + "\tk: " + k + "\tr:" + r
                                    + "\tRMSE: " + RMSE / round);
            RMSEs[index] = RMSE / round;
        }
    }

    public static void conductForAccumCoclusters(String divergence, int k, double[] RMSEs) {
        int round = rootDirs.length;

        for (int index = 0; index < param_r.length; index++) {
            final int r = param_r[index];
            double RMSE = 0.0d;
            LoggerUtil.info(logger, "2+. K: " + k + "\tRank = " + r);

            for (String rootDir : rootDirs) {
                LoggerUtil.info(logger, "3+. Repeat: Root: " + rootDir);

                ClassPathXmlApplicationContext ctx = null;
                try {
                    ctx = new ClassPathXmlApplicationContext(
                        "experiment/recommendation/mixture/mixtureRcmd.xml");
                    MixtureWLRARcmdEngine engine = (MixtureWLRARcmdEngine) ctx
                        .getBean("mixtureRcmd");

                    //modify the test and training file
                    engine.setRootDir(rootDir);

                    //configure required models
                    List<ModelGroup> groups = engine.getGroups();
                    for (String constrain : param_C) {
                        for (int eK = 2; eK <= k; eK++) {
                            String groupName = divergence + constrain + '_' + eK + '_' + eK;
                            ModelGroup group = (ModelGroup) ctx.getBean(groupName);
                            groups.add(group);
                        }
                    }

                    //modify the parameters of model
                    for (ModelGroup group : groups) {
                        for (Model model : group.getModels()) {
                            model.recmder.featureCount = r;
                        }
                    }
                    engine.excute();

                    //record RMSE
                    RMSE += WeightedSVDLearner.curRMSE;
                    LoggerUtil.info(logger, "3+. Divergence: " + divergence + "\tk: " + k + "\tr:"
                                            + r + "\tRepeat: RMSE: " + WeightedSVDLearner.curRMSE);
                } catch (Exception e) {
                    ExceptionUtil.caught(e, WeightedSVDCoclusterAnalyzExper.class + " 发生致命错误");
                } finally {
                    if (ctx != null) {
                        ctx.close();
                    }
                }
            }

            LoggerUtil.info(logger, "4+. Divergence: " + divergence + "\tk: " + k + "\tr:" + r
                                    + "\tRMSE: " + RMSE / round);
            RMSEs[index] = RMSE / round;
        }
    }
}
