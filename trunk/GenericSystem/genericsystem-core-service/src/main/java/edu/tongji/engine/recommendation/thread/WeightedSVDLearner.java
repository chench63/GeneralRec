package edu.tongji.engine.recommendation.thread;

import java.util.Queue;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import prea.util.EvaluationMetrics;
import prea.util.MatrixInformationUtil;
import edu.tongji.data.Model;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: WeightedSVDLearner.java, v 0.1 2014-11-2 下午3:48:46 Exp $
 */
public class WeightedSVDLearner extends Thread {

    /** concurrent task list*/
    public static Queue<Model>    models;

    /**  matrix with training data */
    public static SparseRowMatrix rateMatrix;

    /** matrix with testing data*/
    public static SparseRowMatrix testMatrix;

    /** cumulative prediction*/
    public static SparseRowMatrix cumPrediction;

    /** cumulative weights*/
    public static SparseRowMatrix cumWeight;

    /** current RMSE value*/
    public static double          curRMSE     = 0.0d;

    /** task mutex object*/
    private final static Object   mutexTask   = new Object();

    /** evaluate mutex object*/
    private final static Object   mutexMatrix = new Object();

    /** logger */
    protected final static Logger logger      = Logger
                                                  .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * assign the task
     * 
     * @return
     */
    public static Model task() {
        synchronized (mutexTask) {
            return models.poll();
        }
    }

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        Model task = null;
        while ((task = task()) != null) {
            //build the model and establish GC, once the model is builded.
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            task.buildModel(rateMatrix);
            stopWatch.stop();

            EvaluationMetrics metric = null;
            SparseRowMatrix prediction = null;
            synchronized (mutexMatrix) {
                //evaluate the model and establish GC at the same time.
                task.evaluate(testMatrix, cumPrediction, cumWeight);
                int userCount = testMatrix.length()[0];
                int itemCount = testMatrix.length()[1];

                prediction = new SparseRowMatrix(userCount, itemCount);
                for (int u = 0; u < userCount; u++) {
                    int[] indexList = testMatrix.getRowRef(u).indexList();
                    if (indexList == null) {
                        continue;
                    }

                    for (int v : indexList) {
                        double rateEsti = cumWeight.getValue(u, v) == 0.0 ? (task
                            .getDefaultRating()) : (cumPrediction.getValue(u, v) / cumWeight
                            .getValue(u, v));
                        prediction.setValue(u, v, rateEsti);
                    }
                }

                //update current RMSE
                //                curRMSE = metric.getRMSE();
            }

            //logger
            metric = new EvaluationMetrics(testMatrix, prediction, task.maxValue(), task.minValue());
            LoggerUtil.info(
                logger,
                (new StringBuilder("ThreadId: " + task.getId()))
                    .append("\tTime: " + stopWatch.getLastTaskTimeMillis()).append("\n")
                    .append(metric.printOneLine()));
            LoggerUtil.debug(logger, (new StringBuilder("ThreadId: " + task.getId())).append("\t")
                .append(MatrixInformationUtil.RMSEAnalysis(testMatrix, metric.getPrediction())));
        }
    }
}
