package edu.tongji.ml.memory;

import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.ml.Recommender;

/**
 * 
 * @author Hanke
 * @version $Id: MemoryBasedRecommender.java, v 0.1 2015-6-18 下午5:54:42 Exp $
 */
public abstract class MemoryBasedRecommender extends Recommender {
    // similarity measure
    /** Similarity Measure Code for Pearson Correlation */
    public static final int PEARSON_CORR        = 101;
    /** Similarity Measure Code for Vector Cosine */
    public static final int VECTOR_COS          = 102;
    /** Similarity Measure Code for Mean Squared Difference (MSD) */
    public static final int MEAN_SQUARE_DIFF    = 103;
    /** Similarity Measure Code for Mean Absolute Difference (MAD) */
    public static final int MEAN_ABS_DIFF       = 104;
    /** Similarity Measure Code for Adjuested Cosine */
    public static final int ADJUSTED_COS        = 105;

    // estimation
    /** Estimation Method Code for Weighted Sum */
    public static final int WEIGHTED_SUM        = 201;
    /** Estimation Method Code for Simple Weighted Average */
    public static final int SIMPLE_WEIGHTED_AVG = 202;

    /*========================================
     * Common Variables
     *========================================*/
    /** Rating matrix for each user (row) and item (column) */
    public SparseRowMatrix  rateMatrix;
    /** Prediction matrix for each user (row) and item (column) */
    public SparseRowMatrix  pMatrix;
    /** Similarity matrix for each user (row) and item (column) */
    public SparseRowMatrix  simMatrix;
    /** The number of neighbors, used for estimation. */
    public int              neighborSize;
    /** The method code for similarity measure. */
    public int              similarityMethod;

    /** Indicating whether to use default vote value. */
    public boolean          defaultVote;
    /** The default voting value, if used. */
    public double           defaultValue;

    /*========================================
     * Constructors
     *========================================*/
    /**
     * Construct a memory-based model with the given data.
     * 
     * @param uc The number of users in the dataset.
     * @param ic The number of items in the dataset.
     * @param max The maximum rating value in the dataset.
     * @param min The minimum rating value in the dataset.
     * @param ns The neighborhood size.
     * @param sim The method code of similarity measure.
     * @param df Indicator whether to use default values.
     * @param dv Default value if used.
     */
    public MemoryBasedRecommender(int uc, int ic, double max, double min, int ns, int sim,
                                  boolean df, double dv) {
        userCount = uc;
        itemCount = ic;
        maxValue = max;
        minValue = min;

        neighborSize = ns;
        similarityMethod = sim;

        defaultVote = df;
        defaultValue = dv;
    }

    /*========================================
     * Possible options
     *========================================*/
    /**
     * Calculate similarity between two given vectors.
     * 
     * @param i1 The first vector to calculate similarity.
     * @param i2 The second vector to calculate similarity.
     * @param i1Avg The average of elements in the first vector.
     * @param i2Avg The average of elements in the second vector.
     * @param method The code of similarity measure to be used.
     * It can be one of the following: PEARSON_CORR, VECTOR_COS,
     * MEAN_SQUARE_DIFF, MEAN_ABS_DIFF, or INVERSE_USER_FREQUENCY.
     * @return The similarity value between two vectors i1 and i2.
     */
    public double similarity(SparseVector i1, SparseVector i2, double i1Avg, double i2Avg,
                             int method) {
        double result = 0.0;
        SparseVector v1, v2;

        if (defaultVote) {
            int[] i1ItemList = i1.indexList();
            int[] i2ItemList = i2.indexList();
            v1 = new SparseVector(i1.length());
            v2 = new SparseVector(i2.length());

            if (i1ItemList != null) {
                for (int t = 0; t < i1ItemList.length; t++) {
                    v1.setValue(i1ItemList[t], i1.getValue(i1ItemList[t]));
                    if (i2.getValue(i1ItemList[t]) == 0.0) {
                        v2.setValue(i1ItemList[t], defaultValue);
                    }
                }
            }

            if (i2ItemList != null) {
                for (int t = 0; t < i2ItemList.length; t++) {
                    v2.setValue(i2ItemList[t], i2.getValue(i2ItemList[t]));
                    if (i1.getValue(i2ItemList[t]) == 0.0) {
                        v1.setValue(i2ItemList[t], defaultValue);
                    }
                }
            }
        } else if (method == ADJUSTED_COS) {
            int[] i1ItemList = i1.indexList();
            int[] i2ItemList = i2.indexList();
            v1 = new SparseVector(i1.length());
            v2 = new SparseVector(i2.length());

            int[] leastList = i1ItemList.length > i2ItemList.length ? i2ItemList : i1ItemList;
            if (leastList != null) {
                for (int itemIndex : leastList) {
                    double i1Val = i1.getValue(itemIndex);
                    double i2Val = i2.getValue(itemIndex);
                    if (i1Val != 0.0 && i2Val != 0.0) {
                        v1.setValue(itemIndex, i1Val);
                        v2.setValue(itemIndex, i2Val);
                    }
                }
            }
        } else {
            v1 = i1;
            v2 = i2;
        }

        if (method == PEARSON_CORR) { // Pearson correlation
            SparseVector a = v1.sub(i1Avg);
            SparseVector b = v2.sub(i2Avg);

            result = a.innerProduct(b) / (a.norm() * b.norm());
        } else if (method == VECTOR_COS) { // Vector cosine
            result = v1.innerProduct(v2) / (v1.norm() * v2.norm());
        } else if (method == MEAN_SQUARE_DIFF) { // Mean Square Difference
            SparseVector a = v1.commonMinus(v2);
            a = a.power(2);
            result = a.sum() / a.itemCount();
        } else if (method == MEAN_ABS_DIFF) { // Mean Absolute Difference
            SparseVector a = v1.commonMinus(v2);
            result = a.absoluteSum() / a.itemCount();
        } else if (method == ADJUSTED_COS) {//Adjuested Cosine
            SparseVector s1 = v1.sub(i1Avg);
            SparseVector s2 = v2.sub(i2Avg);

            result = s1.innerProduct(s2) / (s1.norm() * s2.norm());
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
