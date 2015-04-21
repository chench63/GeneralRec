package edu.tongji.data;

import org.apache.log4j.Logger;

import prea.util.EvaluationMetrics;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.ml.matrix.WeigtedRSVD;
import edu.tongji.util.FileUtil;
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
    /** the unique id of the model group */
    private int                   groupId;
    /** logger */
    protected final static Logger logger     = Logger
                                                 .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    protected final static String resultFile = "C:/netflix/WEMAREC";

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
     * build model
     * 
     * @param rateMatrix
     */
    public void buildModel(final SparseRowMatrix rateMatrix) {
        if (rows == null | cols == null) {
            recmder.buildModel(rateMatrix);
        } else {
            SparseRowMatrix localMatrix = rateMatrix.partition(rows, cols);
            recmder.buildModel(localMatrix);

            // suggest JVM to gc
            if (recmder.userCount > 400 * 1000 & recmder.itemCount > 12 * 1000) {
                localMatrix.clear();
                System.gc();
            }
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
    public void evaluate(final SparseRowMatrix testMatrix, SparseRowMatrix cumPrediction,
                         SparseRowMatrix cumWeight) {
        if (rows != null | cols != null) {
            // catch paralleled local model
            //            SparseRowMatrix ltestMatrix = testMatrix.partition(rows, cols);
            //            for (int u = 0; u < ltestMatrix.length()[0]; u++) {
            //                int[] itemList = ltestMatrix.getRowRef(u).indexList();
            //                if (itemList == null) {
            //                    continue;
            //                }
            //
            //                for (int i : itemList) {
            //                    double prediction = recmder.getPredictedRating(u, i);
            //                    double weight = getWeight(u, i, prediction);
            //
            //                    double newCumPrediction = prediction * weight + cumPrediction.getValue(u, i);
            //                    double newCumWeight = weight + cumWeight.getValue(u, i);
            //
            //                    cumPrediction.setValue(u, i, newCumPrediction);
            //                    cumWeight.setValue(u, i, newCumWeight);
            //                }
            //            }

            //for analyzing beta1 and beta2 
            SparseRowMatrix ltestMatrix = testMatrix.partition(rows, cols);

            int itemCount = 0;
            StringBuilder buffer = new StringBuilder();
            for (int u = 0; u < ltestMatrix.length()[0]; u++) {
                int[] indexList = ltestMatrix.getRowRef(u).indexList();
                if (indexList == null) {
                    continue;
                }

                for (int i : indexList) {
                    double AuiEst = recmder.getPredictedRating(u, i);
                    double weight = getWeight(u, i, AuiEst);

                    double newCumPrediction = AuiEst * weight + cumPrediction.getValue(u, i);
                    double newCumWeight = weight + cumWeight.getValue(u, i);

                    cumPrediction.setValue(u, i, newCumPrediction);
                    cumWeight.setValue(u, i, newCumWeight);

                    //record local prediction
                    //userId, itemId, AuiReal, AuiEst, Pu, Pi, GroupId
                    double AuiReal = testMatrix.getValue(u, i);
                    buffer.append(u).append(',').append(i).append(',').append(AuiReal).append(',')
                        .append(AuiEst).append(',').append(getPu(u, AuiEst)).append(',')
                        .append(getPi(i, AuiEst)).append(',').append(groupId).append('\n');
                    itemCount++;
                }

                // if greater than buffer size, then clear the buffer.
                if (itemCount >= 1000 * 1000) {
                    FileUtil.writeAsAppend(resultFile, buffer.toString());

                    //reset buffer
                    itemCount = 0;
                    buffer = new StringBuilder();
                }
            }
            FileUtil.writeAsAppend(resultFile, buffer.toString());

            // release local model memory
            if (recmder.userCount > 400 * 1000 & recmder.itemCount > 12 * 1000) {
                ltestMatrix.clear();
            }
        } else {
            //Specail Operation output Global result
            EvaluationMetrics globalResult = recmder.evaluate(testMatrix);
            LoggerUtil.info(logger, "Singleton Model : \n" + EvaluationMetrics.printTitle() + "\n"
                                    + globalResult.printOneLine());

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

    public double getPu(int u, double prediction) {
        int weightIndex = Double.valueOf(prediction / recmder.minValue - 1).intValue();
        return recmder.getPu(u, weightIndex);
    }

    public double getPi(int i, double prediction) {
        int weightIndex = Double.valueOf(prediction / recmder.minValue - 1).intValue();
        return recmder.getPi(i, weightIndex);
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
     * Getter method for property <tt>groupId</tt>.
     * 
     * @return property value of groupId
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Setter method for property <tt>groupId</tt>.
     * 
     * @param groupId value to be assigned to property groupId
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

}
