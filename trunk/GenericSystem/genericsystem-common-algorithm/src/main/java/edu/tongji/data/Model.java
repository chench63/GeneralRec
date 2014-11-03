package edu.tongji.data;

import edu.tongji.ml.matrix.WeigtedRSVD;

/**
 * 
 * @author Hanke
 * @version $Id: Model.java, v 0.1 2014-11-2 下午3:26:24 Exp $
 */
public class Model {

    private WeigtedRSVD recmder;

    private int[]       rows;

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

    public void buildModel(SparseMatrix rateMatrix) {
        SparseMatrix localMatrix = null;
        if (rows == null | cols == null) {
            localMatrix = rateMatrix;
        } else {
            localMatrix = rateMatrix.partition(rows, cols);
        }
        recmder.buildModel(localMatrix);
    }

    public void evaluate(SparseMatrix testMatrix, SparseMatrix cumPrediction, SparseMatrix cumWeight) {
        SparseMatrix localMatrix = testMatrix.partition(rows, cols);

        for (int u : rows) {
            int[] indexList = localMatrix.getRowRef(u).indexList();
            if (indexList == null) {
                continue;
            }

            for (int v : indexList) {
                double prediction = getPrediction(u, v);
                double weight = getWeight(u, v, prediction);

                cumPrediction.setValue(u, v, prediction * weight);
                cumWeight.setValue(u, v, weight);
            }
        }

        //release memory
        rows = null;
        cols = null;
        recmder.userWeights = null;
        recmder.itemWeights = null;
    }

    public double getPrediction(int u, int v) {
        double prediction = recmder.getU().getRowRef(u).innerProduct(recmder.getV().getColRef(v));

        if (prediction > recmder.maxValue) {
            return recmder.maxValue;
        } else if (prediction < recmder.minValue) {
            return recmder.minValue;
        } else {
            return prediction;
        }
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

}
