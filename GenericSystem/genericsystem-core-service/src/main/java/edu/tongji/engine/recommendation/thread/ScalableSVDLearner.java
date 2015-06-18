package edu.tongji.engine.recommendation.thread;

import java.util.Queue;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import prea.util.EvaluationMetrics;
import prea.util.MatrixInformationUtil;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.data.LocalModel;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: WeightedSVDLearner.java, v 0.1 2014-11-2 下午3:48:46 Exp $
 */
public class ScalableSVDLearner extends Thread {
    /**  matrix with training data */
    protected SparseRowMatrix          rateMatrix;
    /** matrix with testing data*/
    protected SparseRowMatrix          testMatrix;
    /** matrix with testing data*/
    protected MatlabFasionSparseMatrix tmMatrix;

    /** concurrent task list*/
    public static Queue<LocalModel>    models;
    /** cumulative prediction*/
    public static SparseRowMatrix      cumPrediction;
    /** cumulative weights*/
    public static SparseRowMatrix      cumWeight;

    /** task mutex object*/
    private final static Object        mutexTask   = new Object();
    /** evaluate mutex object*/
    private final static Object        mutexMatrix = new Object();
    /** logger */
    protected final static Logger      logger      = Logger
                                                       .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * @param rateMatrix
     * @param testMatrix
     */
    public ScalableSVDLearner(SparseRowMatrix rateMatrix, SparseRowMatrix testMatrix) {
        super();
        this.rateMatrix = rateMatrix;
        this.testMatrix = testMatrix;
    }

    /**
     * @param rateMatrix
     * @param tmMatrix
     */
    public ScalableSVDLearner(SparseRowMatrix rateMatrix, MatlabFasionSparseMatrix tmMatrix) {
        super();
        this.rateMatrix = rateMatrix;
        this.tmMatrix = tmMatrix;
    }

    /** 
     * assign the task
     * 
     * @return
     */
    public static LocalModel task() {
        synchronized (mutexTask) {
            return models.poll();
        }
    }

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        if (testMatrix != null) {
            LocalModel task = null;
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
                }

                //logger
                metric = new EvaluationMetrics(testMatrix, prediction, task.recmmd.maxValue,
                    task.recmmd.minValue);
                LoggerUtil.info(
                    logger,
                    (new StringBuilder("ThreadId: " + task.getId()))
                        .append("\tTime: " + stopWatch.getLastTaskTimeMillis()).append("\n")
                        .append(metric.printOneLine()));
                LoggerUtil.debug(
                    logger,
                    (new StringBuilder("ThreadId: " + task.getId())).append("\t").append(
                        MatrixInformationUtil.RMSEAnalysis(testMatrix, metric.getPrediction())));
            }
        } else if (tmMatrix != null) {
            LocalModel task = null;
            while ((task = task()) != null) {
                //build the model and establish GC, once the model is builded.
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                task.buildModel(rateMatrix);
                stopWatch.stop();

                synchronized (mutexMatrix) {
                    //evaluate the model and establish GC at the same time.
                    task.evaluate(tmMatrix);
                }

                //logger
                LoggerUtil.info(logger, (new StringBuilder("ThreadId: " + task.getId()))
                    .append("\tTime: " + stopWatch.getLastTaskTimeMillis()));
            }
        }
    }
}
