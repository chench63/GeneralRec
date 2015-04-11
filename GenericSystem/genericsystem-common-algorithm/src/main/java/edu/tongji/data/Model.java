package edu.tongji.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import prea.util.EvaluationMetrics;
import prea.util.MatrixInformationUtil;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.ml.matrix.WeigtedRSVD;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: Model.java, v 0.1 2014-11-2 下午3:26:24 Exp $
 */
public class Model {

    /** matrix factorization */
    public WeigtedRSVD            recmder;

    /** included index of rows */
    private int[]                 rows;

    /** included index of columns */
    private int[]                 cols;

    /** the unique id of model*/
    private int                   id;

    /** the sparsity of the matrix*/
    private double                sparsity = Double.NaN;

    /** logger */
    protected final static Logger logger   = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    public Model() {
    }

    /**
     * 
     * @param settingFile
     * @param rowMappingFile
     * @param colMappingFile
     */
    public Model(int uc, int ic, double max, double min, int fc, double lr, double r, double m,
                 int iter, float b1, float b2) {
        recmder = new WeigtedRSVD(uc, ic, max, min, fc, lr, r, m, iter, b1, b2);
    }

    /**
     * compute the sparsity of the training matrix this model contains.
     * 
     * @param rateMatrix
     * @return
     */
    private void sparsity(final SparseRowMatrix rateMatrix) {
        double totalCount = (rows == null | cols == null) ? (rateMatrix.length()[0] * rateMatrix
            .length()[1]) : rows.length * cols.length;
        double itemCount = rateMatrix.itemCount();
        sparsity = itemCount / totalCount;

    }

    /**
     * build model with density limitation
     * 
     * @param rateMatrix
     */
    public boolean buildModel(final SparseRowMatrix rateMatrix, double densityThreshhold) {
        if (rows == null | cols == null) {
            double totalCount = rateMatrix.length()[0] * rateMatrix.length()[1];
            double itemCount = rateMatrix.itemCount();
            if (itemCount / totalCount < densityThreshhold) {
                LoggerUtil.info(logger, "Id: " + id + "\t[" + (itemCount / totalCount)
                                        + "] is too sparse to build the model");
                return false;
            }

            recmder.buildModel(rateMatrix);
            sparsity(rateMatrix);
        } else {
            SparseRowMatrix localMatrix = rateMatrix.partition(rows, cols);
            double totalCount = rows.length * cols.length;
            double itemCount = localMatrix.itemCount();
            if (itemCount / totalCount < densityThreshhold) {
                LoggerUtil.info(logger, "Id: " + id + "\t[" + (itemCount / totalCount)
                                        + "] is too sparse to build the model");
                return false;
            }

            recmder.buildModel(localMatrix);
            sparsity(localMatrix);
        }

        return true;
    }

    /**
     * build model
     * 
     * @param rateMatrix
     */
    public void buildModel(final SparseRowMatrix rateMatrix) {
        if (rows == null | cols == null) {
            recmder.buildModel(rateMatrix);
            sparsity(rateMatrix);
        } else {
            SparseRowMatrix localMatrix = rateMatrix.partition(rows, cols);
            recmder.buildModel(localMatrix);
            sparsity(localMatrix);

            // suggest JVM to gc
            //            System.gc();
        }
    }

    /**
     * evaluate the model
     * 
     * @param testMatrix
     *            the matrix with testing data
     * @param cumPrediction
     *            the cumulative prediction
     * @param cumWeight
     *            the cumulative weights
     */
    static Map<Integer, SparseRowMatrix> TEST = new HashMap<Integer, SparseRowMatrix>();

    public void evaluate(final SparseRowMatrix testMatrix, SparseRowMatrix cumPrediction,
                         SparseRowMatrix cumWeight) {
        if (rows != null | cols != null) {
            // catch paralleled local model
            SparseRowMatrix localMatrix = testMatrix.partition(rows, cols);
            for (int u = 0; u < localMatrix.length()[0]; u++) {
                int[] indexList = localMatrix.getRowRef(u).indexList();
                if (indexList == null) {
                    continue;
                }

                for (int v : indexList) {
                    double prediction = recmder.getPredictedRating(u, v);
                    double weight = getWeight(u, v, prediction);

                    double newCumPrediction = prediction * weight + cumPrediction.getValue(u, v);
                    double newCumWeight = weight + cumWeight.getValue(u, v);

                    cumPrediction.setValue(u, v, newCumPrediction);
                    cumWeight.setValue(u, v, newCumWeight);
                }
            }

            EvaluationMetrics metric = recmder.evaluate(localMatrix);

            //TODO:
            if (logger.isDebugEnabled()) {
                TEST.put(getId(), localMatrix);
                LoggerUtil.debug(
                    logger,
                    (new StringBuilder("ThreadId: " + getId())).append("\t").append(
                        MatrixInformationUtil.RMSEAnalysis(localMatrix, metric.getPrediction())));
            }

        } else {
            //Specail Operation output Global result

            //            EvaluationMetrics globalResult = recmder.evaluate(testMatrix);
            //            SparseRowMatrix wTest = new SparseRowMatrix(testMatrix.length()[0],
            //                testMatrix.length()[1]);
            //            SparseRowMatrix wPredicted = globalResult.getPrediction();
            //            for (int u = 0; u < testMatrix.length()[0]; u++) {
            //                int[] indexList = testMatrix.getRowRef(u).indexList();
            //                if (indexList == null) {
            //                    continue;
            //                }
            //
            //                for (int v : indexList) {
            //                    double real = testMatrix.getValue(u, v);
            //                    double realWeight = getWeight(u, v, real);
            //                    wTest.setValue(u, v, realWeight * real);
            //
            //                    double prediction = wPredicted.getValue(u, v);
            //                    double estWeight = getWeight(u, v, prediction);
            //                    wPredicted.setValue(u, v, estWeight * prediction);
            //                }
            //            }
            //            globalResult = recmder.evaluate(testMatrix);
            //            LoggerUtil.info(logger, MatrixInformationUtil.PredictionReliabilityAnalysis(
            //                globalResult.getPrediction(), wTest, wPredicted));

            //            LoggerUtil.info(logger, "Singleton Model : \n" + EvaluationMetrics.printTitle() + "\n"
            //                                    + globalResult.printOneLine());

            // catch global model
            for (int u = 0; u < testMatrix.length()[0]; u++) {
                int[] indexList = testMatrix.getRowRef(u).indexList();
                if (indexList == null) {
                    continue;
                }

                for (int v : indexList) {
                    double prediction = recmder.getPredictedRating(u, v);
                    double weight = getWeight(u, v, prediction);

                    double newCumPrediction = prediction * weight + cumPrediction.getValue(u, v);
                    double newCumWeight = weight + cumWeight.getValue(u, v);

                    cumPrediction.setValue(u, v, newCumPrediction);
                    cumWeight.setValue(u, v, newCumWeight);

                }
            }

            //TODO
            if (logger.isDebugEnabled()) {
                for (Entry<Integer, SparseRowMatrix> entry : TEST.entrySet()) {
                    EvaluationMetrics eResult = recmder.evaluate(entry.getValue());
                    LoggerUtil.debug(
                        logger,
                        (new StringBuilder())
                            .append("ThreadId: ")
                            .append(entry.getKey())
                            .append("\t")
                            .append(
                                MatrixInformationUtil.RMSEAnalysis(entry.getValue(),
                                    eResult.getPrediction())));
                }
            }

        }

        // release recommender memory
        rows = null;
        cols = null;
        recmder.explicitClear();
    }

    public double getWeight(int u, int v, double prediction) {
        int weightIndex = Double.valueOf(prediction / recmder.minValue - 1).intValue();
        return recmder.getWeight(u, v, weightIndex);
    }

    public double getDefaultRating() {
        return (recmder.maxValue + recmder.minValue) / 2.0;
    }

    public double maxValue() {
        return recmder.maxValue;
    }

    public double minValue() {
        return recmder.minValue;
    }

    public void setWeights(float[][] userWeights, float[][] itemWeights) {
        recmder.setUserWeights(userWeights);
        recmder.setItemWeights(itemWeights);
    }

    /**
     * Setter method for property <tt>rows</tt>.
     * 
     * @param rows
     *            value to be assigned to property rows
     */
    public void setRows(int[] rows) {
        this.rows = rows;
    }

    /**
     * Setter method for property <tt>cols</tt>.
     * 
     * @param cols
     *            value to be assigned to property cols
     */
    public void setCols(int[] cols) {
        this.cols = cols;
    }

    /**
     * Getter method for property <tt>rows</tt>.
     * 
     * @return property value of rows
     */
    public int[] getRows() {
        return rows;
    }

    /**
     * Getter method for property <tt>cols</tt>.
     * 
     * @return property value of cols
     */
    public int[] getCols() {
        return cols;
    }

    /**
     * Getter method for property <tt>id</tt>.
     * 
     * @return property value of id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter method for property <tt>id</tt>.
     * 
     * @param id value to be assigned to property id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter method for property <tt>sparsity</tt>.
     * 
     * @return property value of sparsity
     */
    public double getSparsity() {
        return sparsity;
    }

}
