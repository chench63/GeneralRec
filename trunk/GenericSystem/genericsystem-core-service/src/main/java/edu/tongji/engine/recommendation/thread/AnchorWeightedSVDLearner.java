package edu.tongji.engine.recommendation.thread;

import java.util.Queue;

import org.apache.log4j.Logger;

import prea.util.MatrixInformationUtil;
import prea.util.SimpleEvaluationMetrics;
import edu.tongji.data.SparseMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.ml.matrix.AnchorWeightedRSVD;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: AnchorWeightedSVDLearner.java, v 0.1 2014-11-4 下午9:43:25 Exp $
 */
public class AnchorWeightedSVDLearner extends Thread {

    public static Queue<AnchorWeightedRSVD> models;

    /**  matrix with training data */
    public static SparseMatrix              rateMatrix;

    /** matrix with testing data*/
    public static SparseMatrix              testMatrix;

    public static SparseMatrix              cumPrediction;

    public static SparseMatrix              cumWeight;

    private static Object                   mutex       = new Object();

    private static Object                   mutexMatrix = new Object();

    /** logger */
    protected final static Logger           logger      = Logger
                                                            .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    public static AnchorWeightedRSVD task() {
        synchronized (mutex) {
            return models.poll();
        }
    }

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        AnchorWeightedRSVD task = null;
        while ((task = task()) != null) {
            task.buildModel(rateMatrix);
            SimpleEvaluationMetrics metric = null;
            SparseMatrix prediction = null;

            //block
            synchronized (mutexMatrix) {
                task.evaluate(testMatrix, cumPrediction, cumWeight);
                int userCount = testMatrix.length()[0];
                prediction = new SparseMatrix(testMatrix);
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
                metric = new SimpleEvaluationMetrics(testMatrix, prediction, task.maxValue,
                    task.minValue);
            }

            //===========
            LoggerUtil.info(logger, metric.printOneLine());
            LoggerUtil.info(logger, MatrixInformationUtil.RMSEAnalysis(testMatrix, prediction));
        }
    }

}
