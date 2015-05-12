package paper.sigir15.experiment;

import java.util.Date;

import prea.util.EvaluationMetrics;
import prea.util.RecResultUtil;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;

/**
 * 
 * @author Hanke
 * @version $Id: ClusteringeffctExperiment.java, v 0.1 2015-5-12 下午8:36:46 Exp $
 */
public final class ClusteringeffctExperiment {
    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]      rootDirs        = { "E:/MovieLens/zWarmStart/ml-10M100K/1/" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int     userCount       = 69878;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int     itemCount       = 10677;
    public final static double  maxValue        = 5.0d;
    public final static double  minValue        = 0.5d;
    public static int           modelNum        = 16;
    public final static int[]   groupIncluded   = {};

    public final static String  pFile           = "WEMAREC[20]_C25_IWEW_[23]x2_RWSVD";
    public final static boolean isLocalDistrbtn = false;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        modelNum = (groupIncluded == null | groupIncluded.length == 0) ? modelNum
            : groupIncluded.length;
    }

    public static void doEnsembleWithPr() {
        double[] beta0s = { 1.3d };

        for (String rootDir : rootDirs) {
            cmp(rootDir, beta0s, null, null);
            //            cmpWithSaving(rootDir, beta0s, null, null);
        }
    }

    public static void cmp(String rootDir, double[] beta0s, double[] beta1s, double[] beta2s) {
        //load prediction information
        String predctFile = rootDir + pFile;
        SparseRowMatrix testMatrix = new SparseRowMatrix(userCount, itemCount);
        SparseRowMatrix[] estMatrix = new SparseRowMatrix[modelNum];
        SparseRowMatrix[] prMatrix = new SparseRowMatrix[modelNum];
        for (int i = 0; i < modelNum; i++) {
            estMatrix[i] = new SparseRowMatrix(userCount, itemCount);
            prMatrix[i] = new SparseRowMatrix(userCount, itemCount);
        }

        RecResultUtil.readRec(predctFile, estMatrix, testMatrix, prMatrix, groupIncluded);

        // ensemble process
        for (double beta0 : beta0s) {
            SparseRowMatrix cumPrediction = new SparseRowMatrix(userCount, itemCount);
            SparseRowMatrix cumWeight = new SparseRowMatrix(userCount, itemCount);

            ensemble(beta0, 0.0d, 0.0d, cumPrediction, cumWeight, estMatrix, testMatrix, null,
                null, prMatrix);
            EvaluationMetrics metric = evaluate(cumPrediction, cumWeight, testMatrix);
            System.out.println("beta0: " + String.format("%.2f", beta0) + "\tRMSE: "
                               + metric.getRMSE() + "\t" + (new Date()));
        }
    }

    protected static void ensemble(double beta0, double beta1, double beta2,
                                   SparseRowMatrix cumPrediction, SparseRowMatrix cumWeight,
                                   SparseRowMatrix[] estMatrix, SparseRowMatrix testMatrix,
                                   SparseRowMatrix[] puMatrix, SparseRowMatrix[] piMatrix,
                                   SparseRowMatrix[] prMatrix) {
        for (int g = 0; g < modelNum; g++) {
            for (int u = 0; u < userCount; u++) {
                SparseVector Fu = testMatrix.getRowRef(u);
                int[] itemIndex = Fu.indexList();
                if (itemIndex == null) {
                    continue;
                }

                for (int i : itemIndex) {
                    double curPrediciton = estMatrix[g].getValue(u, i);
                    double curWeight = 0.0d;
                    double curPr = prMatrix[g].getValue(u, i);
                    curWeight = isLocalDistrbtn ? weightWithLocalPr(beta0, curPr)
                        : weightWithGlobalPr(beta0, curPrediciton);
                    double newCumPrediction = curPrediciton * curWeight
                                              + cumPrediction.getValue(u, i);
                    double newCumWeight = curWeight + cumWeight.getValue(u, i);

                    //update old values
                    cumPrediction.setValue(u, i, newCumPrediction);
                    cumWeight.setValue(u, i, newCumWeight);
                }
            }
        }
    }

    protected static EvaluationMetrics evaluate(SparseRowMatrix cumPrediction,
                                                SparseRowMatrix cumWeight,
                                                SparseRowMatrix testMatrix) {
        //compute predictions
        for (int u = 0; u < userCount; u++) {
            int[] itemList = testMatrix.getRowRef(u).indexList();
            if (itemList == null) {
                continue;
            }

            for (int i : itemList) {
                double cP = cumPrediction.getValue(u, i);
                double cW = cumWeight.getValue(u, i);

                double AuiEst = cP / cW;
                if (AuiEst > maxValue) {
                    AuiEst = maxValue;
                } else if (AuiEst < minValue) {
                    AuiEst = minValue;
                }
                cumWeight.setValue(u, i, AuiEst);
            }
        }

        //evaluation
        return new EvaluationMetrics(testMatrix, cumWeight, maxValue, minValue);
    }

    public static double weightWithLocalPr(double beta0, double Pr) {
        return 1.0d + beta0 * Pr;
    }

    protected final static double[] ml10m   = { 0.00940 + 0.03833, 0.01195 + 0.07892,
            0.03707 + 0.23649, 0.08748 + 0.28764, 0.05830 + 0.15444 };
    protected final static double[] netflix = { 0.04586, 0.10088, 0.28681, 0.33591, 0.23055 };

    public static double weightWithGlobalPr(double beta0, double AuiEstm) {
        int indx = (int) (AuiEstm / 1.0 - 1);
        return 1.0d + beta0 * ml10m[indx];
        //        return 1.0d + beta0 * netflix[indx];
    }

}
