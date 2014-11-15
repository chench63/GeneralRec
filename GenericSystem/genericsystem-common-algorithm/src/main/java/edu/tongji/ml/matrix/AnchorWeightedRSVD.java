package edu.tongji.ml.matrix;

import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: AnchorWeightedRSVD.java, v 0.1 2014-11-4 下午3:53:04 Exp $
 */
public class AnchorWeightedRSVD extends MatrixFactorizationRecommender {

    /**  */
    private static final long serialVersionUID = 1L;

    /** The vector containing each user's weight. */
    private SparseVector      w;
    /** The vector containing each item's weight. */
    private SparseVector      v;

    //===================================
    //
    //===================================
    /** the rating distribution w.r.t each user*/
    private double[][]        userWeights;

    /** the rating distribution w.r.t each item*/
    private double[][]        itemWeights;

    private double            base1            = 1.55f;

    private double            base2            = 0.5f;

    /**
     * 
     * @param uc
     * @param ic
     * @param max
     * @param min
     * @param fc
     * @param lr
     * @param r
     * @param m
     * @param iter
     * @param b1
     * @param b2
     * @param w0
     * @param v0
     * @param rm
     * @param au
     * @param ai
     */
    public AnchorWeightedRSVD(int uc, int ic, double max, double min, int fc, double lr, double r,
                              double m, int iter, double b1, double b2, SparseVector w0,
                              SparseVector v0, int au, int ai) {
        super(uc, ic, max, min, fc, lr, r, m, iter);
        w = w0;
        v = v0;
        this.base1 = b1;
        this.base2 = b2;
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseMatrix)
     */
    @Override
    public void buildModel(SparseMatrix rateMatrix) {
        super.buildModel(rateMatrix);

        //initialize weights
        getUserWeights(rateMatrix);
        getItemWeights(rateMatrix);

        // Learn by Weighted RegSVD
        int round = 0;
        int rateCount = rateMatrix.itemCount();
        double prevErr = 99999;
        double currErr = 9999;

        while (Math.abs(prevErr - currErr) > 0.0001 && round < maxIter) {
            double sum = 0.0;
            for (int u = 0; u < userCount; u++) {
                SparseVector items = rateMatrix.getRowRef(u);
                int[] itemIndexList = items.indexList();

                if (itemIndexList != null) {
                    for (int i : itemIndexList) {
                        double RuiEst = 0.0;
                        for (int r = 0; r < featureCount; r++) {
                            RuiEst += userFeatures.getValue(u, r) * itemFeatures.getValue(r, i);
                        }
                        double RuiReal = rateMatrix.getValue(u, i);
                        double err = RuiReal - RuiEst;
                        sum += Math.pow(err, 2);

                        if ((w.getValue(u) * v.getValue(i)) == 0.0d) {
                            continue;
                        }

                        int weightIndx = Double.valueOf(RuiReal / minValue - 1).intValue();
                        double weight = getWeight(u, i, weightIndx);
                        for (int r = 0; r < featureCount; r++) {
                            double Fus = userFeatures.getValue(u, r);
                            double Gis = itemFeatures.getValue(r, i);

                            userFeatures.setValue(u, r, Fus + learningRate
                                                        * (err * Gis * weight - regularizer * Fus));
                            itemFeatures.setValue(r, i, Gis + learningRate
                                                        * (err * Fus * weight - regularizer * Gis));
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = sum / rateCount;

            round++;

            // Show progress:
            LoggerUtil.info(logger, round + "\t" + Math.sqrt(currErr));
        }
    }

    public void evaluate(SparseMatrix testMatrix, SparseMatrix cumPrediction, SparseMatrix cumWeight) {

        for (int u = 0; u < testMatrix.length()[0]; u++) {
            int[] indexList = testMatrix.getRowRef(u).indexList();
            if (indexList == null) {
                continue;
            }

            for (int i : indexList) {
                if ((w.getValue(u) * v.getValue(i)) == 0.0d) {
                    continue;
                }

                double prediction = getPrediction(u, i);
                double weight = getWeight(u, i, prediction);

                double newCumPrediction = prediction * weight + cumPrediction.getValue(u, i);
                double newCumWeight = weight + cumWeight.getValue(u, i);

                cumPrediction.setValue(u, i, newCumPrediction);
                cumWeight.setValue(u, i, newCumWeight);
            }
        }
    }

    public double getPrediction(int u, int v) {
        double prediction = userFeatures.getRowRef(u).innerProduct(itemFeatures.getColRef(v));

        if (prediction > maxValue) {
            return maxValue;
        } else if (prediction < minValue) {
            return minValue;
        } else {
            return prediction;
        }
    }

    public double getWeight(int u, int v, double prediction) {
        int weightIndex = Double.valueOf(prediction / minValue - 1).intValue();
        return getWeight(u, v, weightIndex);
    }

    public double getWeight(int u, int i, int weightIndx) {
        return (userWeights[u][weightIndx] * itemWeights[i][weightIndx] + base1);
    }

    public void getUserWeights(SparseMatrix rateMatrix) {
        int weightSize = Double.valueOf(maxValue / minValue).intValue();
        userWeights = new double[userCount][weightSize];
        for (int u = 0; u < userCount; u++) {
            SparseVector Fu = rateMatrix.getRowRef(u);
            int[] itemIndexList = Fu.indexList();

            if (itemIndexList == null) {
                for (int s = 0; s < weightSize; s++) {
                    userWeights[u][s] = base2 + 1.0f / weightSize;
                }
            } else {
                int total = weightSize;
                int[] rates = new int[weightSize];

                // count
                for (int i : itemIndexList) {
                    if ((w.getValue(u) * v.getValue(i)) == 0.0d) {
                        continue;
                    }

                    int indx = Double.valueOf(Fu.getValue(i) / minValue - 1).intValue();
                    rates[indx]++;
                    total++;
                }

                if (total == 0) {
                    for (int s = 0; s < weightSize; s++) {
                        userWeights[u][s] = base2 + 1.0f / weightSize;
                    }
                } else {
                    // compute weights
                    for (int s = 0; s < weightSize; s++) {
                        userWeights[u][s] = base2 + (rates[s] + 1.0f) / total;
                    }
                }
            }
        }
    }

    public double getDefaultRating() {
        return (maxValue + minValue) / 2.0;
    }

    public void getItemWeights(SparseMatrix rateMatrix) {
        int weightSize = Double.valueOf(maxValue / minValue).intValue();
        itemWeights = new double[itemCount][weightSize];
        for (int i = 0; i < itemCount; i++) {
            SparseVector Gi = rateMatrix.getColRef(i);
            int[] userIndexList = Gi.indexList();

            if (userIndexList == null) {
                for (int s = 0; s < weightSize; s++) {
                    itemWeights[i][s] = base2 + 1.0f / weightSize;
                }
            } else {
                int total = weightSize;
                int[] rates = new int[weightSize];

                // count
                for (int u : userIndexList) {
                    if ((w.getValue(u) * v.getValue(i)) == 0.0d) {
                        continue;
                    }

                    int indx = Double.valueOf(Gi.getValue(u) / minValue - 1).intValue();
                    rates[indx]++;
                    total++;
                }

                if (total == 0) {
                    for (int s = 0; s < weightSize; s++) {
                        itemWeights[i][s] = base2 + 1.0f / weightSize;
                    }
                } else {
                    // compute weights
                    for (int s = 0; s < weightSize; s++) {
                        itemWeights[i][s] = base2 + (rates[s] + 1.0f) / total;
                    }
                }
            }

        }
    }
}
