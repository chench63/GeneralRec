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
public final class WeightedSVDParamAnalyzExper {

    /** result file*/
    public final static String    resultFilePrefix = "E:/MovieLens/ml-1m/Exper1_";

    /** base1 in weightedSVD*/
    public final static float[]   param_b1         = { 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f,
            0.7f, 0.8f, 0.9f, 1.0f                };

    /** base2 in weightedSVD*/
    public final static float[]   param_b2         = { 0.0f, 0.1f, 0.2f, 0.3f };

    /** rank of the Solution */
    public final static int[]     param_r          = { 25 };

    public final static String[]  trainingSetFiles = { "E:/MovieLens/ml-1m/1/trainingset",
            "E:/MovieLens/ml-1m/3/trainingset"    };

    public final static String[]  testingSetFiles  = { "E:/MovieLens/ml-1m/1/testingset",
            "E:/MovieLens/ml-1m/3/testingset"     };

    /** logger */
    protected final static Logger logger           = Logger
                                                       .getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int round = trainingSetFiles.length;

        LoggerUtil.info(logger, "1. Start Main Engine.");
        for (int r : param_r) {
            LoggerUtil.info(logger, "2+. Rank = " + r);

            //result file 
            String file = resultFilePrefix + r;
            for (float b1 : param_b1) {
                for (float b2 : param_b2) {

                    //every experiment 3 rounds
                    double RMSE = 0.0d;
                    for (int i = 0; i < round; i++) {
                        LoggerUtil.info(logger, "3+. Repeat: \n\t\tTrian: " + trainingSetFiles[i]
                                                + "\n\t\tTest: " + testingSetFiles[i]);

                        ClassPathXmlApplicationContext ctx = null;
                        try {
                            ctx = new ClassPathXmlApplicationContext(
                                "experiment/recommendation/mixture/mixtureRcmd.xml");
                            MixtureWLRARcmdEngine engine = (MixtureWLRARcmdEngine) ctx
                                .getBean("mixtureRcmd");

                            //modify the test and training file
                            engine.setTrainingSetFile(trainingSetFiles[i]);
                            engine.setTestingSetFile(testingSetFiles[i]);

                            //modify the parameters of modl
                            List<ModelGroup> groups = engine.getGroups();
                            for (ModelGroup group : groups) {
                                for (Model model : group.getModels()) {
                                    model.recmder.base1 = b1;
                                    model.recmder.base2 = b2;
                                    model.recmder.featureCount = r;
                                }
                            }
                            engine.excute();

                            //record RMSE
                            RMSE += WeightedSVDLearner.curRMSE;
                            LoggerUtil.info(logger, "3+. Rank: " + r + "\tRepeat: RMSE: "
                                                    + WeightedSVDLearner.curRMSE);
                        } catch (Exception e) {
                            ExceptionUtil.caught(e, WeightedSVDParamAnalyzExper.class + " 发生致命错误");
                        } finally {
                            if (ctx != null) {
                                ctx.close();
                            }
                        }
                    }

                    //record file
                    LoggerUtil.info(logger, "4+. Rank: " + r + "\tB1: " + b1 + "\tB2: " + b2
                                            + "\tR: " + RMSE / round + "\n\n");
                    StringBuilder record = (new StringBuilder()).append(b1).append('\t').append(b2)
                        .append('\t').append(RMSE / round).append('\n');
                    FileUtil.writeAsAppend(file, record.toString());

                }
            }
        }

    }
}
