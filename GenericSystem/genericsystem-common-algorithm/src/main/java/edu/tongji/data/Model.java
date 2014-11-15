package edu.tongji.data;

import edu.tongji.ml.matrix.WeigtedRSVD;

/**
 * 
 * @author Hanke
 * @version $Id: Model.java, v 0.1 2014-11-2 下午3:26:24 Exp $
 */
public class Model {

    /** matrix factorization*/
    private WeigtedRSVD recmder;

    /** included index of rows*/
    private int[]       rows;

    /** included index of columns*/
    private int[]       cols;

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
    public void buildModel(SparseRowMatrix rateMatrix) {
        if (rows == null | cols == null) {
            recmder.buildModel(rateMatrix);
        } else {
            SparseRowMatrix localMatrix = rateMatrix.partition(rows, cols);
            recmder.buildModel(localMatrix);
        }
    }

    /**
     * evaluate the model
     * 
     * @param testMatrix        the matrix with testing data
     * @param cumPrediction     the cumulative prediction
     * @param cumWeight         the cumulative weights
     */
    public void evaluate(SparseRowMatrix testMatrix, SparseRowMatrix cumPrediction,
                         SparseRowMatrix cumWeight) {
        SparseRowMatrix localMatrix = null;
        if (rows == null | cols == null) {
            localMatrix = testMatrix;
        } else {
            localMatrix = testMatrix.partition(rows, cols);
        }

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

        //release memory
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
     * @param rows value to be assigned to property rows
     */
    public void setRows(int[] rows) {
        this.rows = rows;
    }

    /**
     * Setter method for property <tt>cols</tt>.
     * 
     * @param cols value to be assigned to property cols
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

}
