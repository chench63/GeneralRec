package edu.tongji.ml.etc;

import prea.util.EvaluationMetrics;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.ml.Recommender;

/**
 * 
 * @author Hanke
 * @version $Id: SlopeOne.java, v 0.1 2015-6-22 下午12:45:52 Exp $
 */
public class SlopeOne extends Recommender {
    /*========================================
     * Common Variables
     *========================================*/
    /** Prepared difference matrix */
    private SparseRowMatrix diffMatrix;
    /** Prepared frequency matrix */
    private SparseRowMatrix freqMatrix;

    /** Rating matrix for each user (row) and item (column) */
    public SparseRowMatrix  rateMatrix;
    /** Prediction matrix for each user (row) and item (column) */
    public SparseRowMatrix  pMatrix;

    /** 
     * @see edu.tongji.ml.Recommender#localizedModel(edu.tongji.data.SparseMatrix, int[], int[])
     */
    @Override
    public void localizedModel(SparseMatrix rateMatrix, int[] rowInModel, int[] colInModel) {
    }

    /** 
     * @see edu.tongji.ml.Recommender#buildModel(edu.tongji.data.SparseRowMatrix, edu.tongji.data.SparseRowMatrix)
     */
    @Override
    public void buildModel(SparseRowMatrix rm, SparseRowMatrix testMatrix) {
        rateMatrix = rm;
        diffMatrix = new SparseRowMatrix(itemCount, itemCount);
        freqMatrix = new SparseRowMatrix(itemCount, itemCount);

        // build model
        for (int u = 0; u < userCount; u++) {
            SparseVector ratedItems = rateMatrix.getRowRef(u);
            int[] itemList = ratedItems.indexList();

            if (itemList != null) {
                for (int i : itemList) {
                    for (int j : itemList) {
                        double oldCount = freqMatrix.getValue(i, j);
                        double oldDiff = diffMatrix.getValue(i, j);
                        double observedDiff = rateMatrix.getValue(u, i) - rateMatrix.getValue(u, j);

                        freqMatrix.setValue(i, j, oldCount + 1);
                        diffMatrix.setValue(i, j, oldDiff + observedDiff);
                    }
                }
            }
        }

        for (int j = 0; j < itemCount; j++) {
            for (int i = 0; i < itemCount; i++) {
                double count = freqMatrix.getValue(j, i);

                if (count > 0) {
                    double oldvalue = diffMatrix.getValue(j, i);
                    diffMatrix.setValue(j, i, oldvalue / count);
                }
            }
        }

        // Produce predictions
        for (int u = 0; u < userCount; u++) {
            int[] itemList = testMatrix.getRowRef(u).indexList();
            if (itemList == null) {
                continue;
            }

            SparseVector Au = getEstimation(u, itemList);
            for (int i : itemList) {
                pMatrix.setValue(u, i, Au.getValue(i));
            }
        }
    }

    /** 
     * @see edu.tongji.ml.Recommender#buildModel(edu.tongji.data.SparseMatrix)
     */
    @Override
    public void buildModel(SparseMatrix rateMatrix) {
        throw new RuntimeException("buildModel for SparseMatrix requires implementation!");
    }

    /** 
     * @see edu.tongji.ml.Recommender#buildModel(edu.tongji.data.MatlabFasionSparseMatrix, edu.tongji.data.MatlabFasionSparseMatrix)
     */
    @Override
    public void buildModel(MatlabFasionSparseMatrix rateMatrix, MatlabFasionSparseMatrix tMatrix) {
        throw new RuntimeException(
            "buildModel for MatlabFasionSparseMatrix requires implementation!");
    }

    /** 
     * @see edu.tongji.ml.Recommender#evaluate(edu.tongji.data.SparseRowMatrix)
     */
    @Override
    public EvaluationMetrics evaluate(SparseRowMatrix testMatrix) {
        return null;
    }

    /** 
     * @see edu.tongji.ml.Recommender#predict(int, int)
     */
    @Override
    public double predict(int u, int i) {
        return pMatrix.getValue(u, i);
    }

    /**
     * Estimate of ratings for a given user and a set of test items.
     * 
     * @param u The user number.
     * @param testItems The list of items to be predicted.
     * 
     * @return A list containing predicted rating scores.
     */
    private SparseVector getEstimation(int u, int[] testItems) {
        SparseVector result = new SparseVector(itemCount);
        SparseVector predictions = new SparseVector(itemCount);
        SparseVector frequencies = new SparseVector(itemCount);

        int[] ratedItems = rateMatrix.getRowRef(u).indexList();

        if (ratedItems == null) {
            for (int t = 0; t < testItems.length; t++) {
                result.setValue(testItems[t], (maxValue + minValue) / 2);
            }
        } else {
            for (int i : ratedItems) {
                for (int j : testItems) {
                    double newValue = (diffMatrix.getValue(j, i) + rateMatrix.getValue(u, i))
                                      * freqMatrix.getValue(j, i);
                    predictions.setValue(j, predictions.getValue(j) + newValue);
                    frequencies.setValue(j, frequencies.getValue(j) + freqMatrix.getValue(j, i));
                }
            }

            for (int i : testItems) {
                if (predictions.getValue(i) > 0) {
                    result.setValue(i, predictions.getValue(i) / frequencies.getValue(i));
                } else {
                    result.setValue(i, (maxValue + minValue) / 2);
                }
            }
        }

        return result;
    }

    /** 
     * @see edu.tongji.ml.Recommender#ensnblWeight(int, int, double)
     */
    @Override
    public double ensnblWeight(int u, int i, double rating) {
        return 1.0;
    }

    /** 
     * @see edu.tongji.ml.Recommender#dropRef()
     */
    @Override
    public void dropRef() {
    }

}
