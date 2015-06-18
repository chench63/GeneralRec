package edu.tongji.ml.memory;

import prea.util.EvaluationMetrics;
import prea.util.Sort;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;

/**
 * 
 * @author Hanke
 * @version $Id: UserBased.java, v 0.1 2015-6-18 下午4:51:26 Exp $
 */
public class UserBased extends MemoryBasedRecommender {
    /** Average of ratings for each user. */
    public double[] userRateAverage;

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
    public UserBased(int uc, int ic, double max, double min, int ns, int sim, boolean df, double dv) {
        super(uc, ic, max, min, ns, sim, df, dv);
    }

    /** 
     * @see edu.tongji.ml.Recommender#buildModel(edu.tongji.data.SparseRowMatrix)
     */
    @Override
    public void buildModel(SparseRowMatrix rateMatrix, SparseRowMatrix testMatrix) {
        this.rateMatrix = rateMatrix;
        pMatrix = new SparseRowMatrix(userCount, itemCount);
        userRateAverage = new double[userCount];
        for (int u = 0; u < userCount; u++) {
            userRateAverage[u] = rateMatrix.getRowRef(u).average();
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
    public double predict(int uNo, int iNo) {
        double prediction = pMatrix.getValue(uNo, iNo);
        if (prediction != 0.0d) {
            return prediction;
        }

        SparseVector a = rateMatrix.getRow(uNo);
        double a_avg = a.average();

        // calculate similarity with every user:
        double[] sim = new double[userCount];
        int[] index = new int[userCount];
        int tmpIdx = 0;
        for (int uIndx = 0; uIndx < userCount; uIndx++) {
            SparseVector b = rateMatrix.getRowRef(uIndx);
            double similarityMeasure = similarity(a, b, a_avg, userRateAverage[uIndx],
                similarityMethod);

            if (similarityMeasure > 0.0 && b.getValue(iNo) > 0.0) {
                sim[tmpIdx] = similarityMeasure;
                index[tmpIdx] = uIndx;
                tmpIdx++;
            }
        }

        // Estimate rating:
        // find k most similar users:
        Sort.kLargest(sim, index, 0, tmpIdx - 1, neighborSize);
        int[] similarUsers = new int[neighborSize];
        int similarUserCount = 0;
        for (int i = 0; i < neighborSize; i++) {
            if (sim[i] > 0) { // sim[i] is already sorted!
                similarUsers[i] = index[i];
                similarUserCount++;
            }
        }

        if (similarUserCount > 0) {
            double estimated = estimation(uNo, iNo, similarUsers, similarUserCount, sim,
                WEIGHTED_SUM);

            // NaN check: it happens that no similar user has rated on item i, then the estimate is NaN.
            if (!Double.isNaN(estimated)) {
                return estimated;
            } else {
                return (maxValue + minValue) / 2;
            }
        } else {
            return (maxValue + minValue) / 2;
        }
    }

    /**
     * Estimate a rating based on neighborhood data.
     * 
     * @param activeIndex The active user index for user-based CF; The item index for item-based CF.
     * @param targetIndex The target item index for user-based CF; The user index for item-based CF.
     * @param ref The indices of neighborhood, which will be used for estimation.
     * @param refCount The number of neighborhood, which will be used for estimation.
     * @param refWeight The weight of each neighborhood.
     * @param method The code of estimation method. It can be one of the following: WEIGHTED_SUM or SIMPLE_WEIGHTED_AVG.
     * 
     * @return The estimated rating value.
     */
    private double estimation(int activeIndex, int targetIndex, int[] ref, int refCount,
                              double[] refWeight, int method) {
        double sum = 0.0;
        double weightSum = 0.0;
        double result = 0.0;

        if (method == WEIGHTED_SUM) { // Weighted Sum of Others' rating
            double activeAvg = userRateAverage[activeIndex];

            for (int u = 0; u < refCount; u++) {
                double refAvg = userRateAverage[ref[u]];
                double ratedValue = rateMatrix.getValue(ref[u], targetIndex);

                if (ratedValue > 0.0) {
                    sum += ((ratedValue - refAvg) * refWeight[u]);
                    weightSum += refWeight[u];
                }
            }

            result = activeAvg + sum / weightSum;
        } else if (method == SIMPLE_WEIGHTED_AVG) { // Simple Weighted Average
            for (int u = 0; u < refCount; u++) {
                double ratedValue = rateMatrix.getValue(ref[u], targetIndex);

                if (ratedValue > 0.0) {
                    sum += (ratedValue * refWeight[u]);
                    weightSum += refWeight[u];
                }
            }

            result = sum / weightSum;
        }

        // rating should be located between minValue and maxValue:
        if (result < minValue) {
            result = minValue;
        } else if (result > maxValue) {
            result = maxValue;
        }

        return result;
    }

}
