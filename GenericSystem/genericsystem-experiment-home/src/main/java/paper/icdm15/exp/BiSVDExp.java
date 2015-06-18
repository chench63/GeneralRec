package paper.icdm15.exp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import prea.util.MatrixFileUtil;
import prea.util.MatrixInformationUtil;
import edu.tongji.data.BlockMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.ml.matrix.BiSVD;
import edu.tongji.ml.matrix.RegularizedSVD;
import edu.tongji.ml.matrix.variant.ISVD;
import edu.tongji.ml.matrix.variant.USVD;
import edu.tongji.util.FileUtil;

public class BiSVDExp {

    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]      rootDirs     = { "E:/Netflix/Fetch50/2/", "E:/Netflix/Fetch50/3/" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int     userCount    = 480189;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int     itemCount    = 17770;
    public final static double  maxValue     = 5.0;
    public final static double  minValue     = 1.0;
    public final static double  lrate        = 0.001;
    public final static double  regularized  = 0.15;
    public final static int     maxIteration = 400;
    public final static boolean showProgress = false;

    public final static String  resultDir    = "E:/Netflix/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        rankExp();
    }

    /*========================================
     * Experiments
     *========================================*/
    public static void rankExp() {
        String clusterDir = "Kmeanspp/KL_2_2/";

        int[] featureCounts = { 20 };
        for (String rootDir : rootDirs) {
            //            RSVD(featureCounts, rootDir);
            //            UserConstrainedRSVD(featureCounts, clusterDir, rootDir);
            //            ItemConstrainedRSVD(featureCounts, clusterDir, rootDir);
            BiConstrainedRSVD(featureCounts, clusterDir, rootDir);
            System.gc();
        }
    }

    public static void clusteringExp() {
        String[] clusterDirs = { "Kmeanspp/KL_2_2/" };
        int[] featureCounts = { 20 };

        for (String rootDir : rootDirs) {
            for (String clusterDir : clusterDirs) {
                BiConstrainedRSVD(featureCounts, clusterDir, rootDir);
            }
        }
    }

    /*========================================
     * Algorithms
     *========================================*/

    public static void RSVD(int[] featureCounts, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);

        //build model
        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int featureCount : featureCounts) {
            RegularizedSVD recmmd = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIteration, showProgress);
            recmmd.buildModel(rateMatrix);

            //evaluation
            double RMSE = MatrixInformationUtil.offlineRMSE(recmmd, testFile, userCount, itemCount,
                null);
            System.out.println("SVD\tfc: " + featureCount + "\tlr: " + lrate + "\tr: "
                               + regularized + "\n" + RMSE + "\n");
            FileUtil.writeAsAppend(resultDir + "zRSVD", "fc: " + featureCount + "\tlr: " + lrate
                                                        + "\tr: " + regularized + "\n" + RMSE
                                                        + "\n");
            stat.addValue(RMSE);
        }
        FileUtil.writeAsAppend(
            resultDir + "zRSVD",
            "Mean: " + stat.getMean() + "\tSD:"
                    + String.format("%.6f", stat.getStandardDeviation()) + "\n");
    }

    public static void UserConstrainedRSVD(int[] featureCounts, String clusterDir, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);

        int[] ua = new int[userCount];
        int[] dimnsn = readBiAssigmnt(ua, null, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            USVD recmmd = new USVD(userCount, itemCount, maxValue,
                minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[0], ua,
                showProgress);
            recmmd.buildModel(rateMatrix);

            //evaluation
            double RMSE = MatrixInformationUtil.offlineRMSE(recmmd, testFile, userCount, itemCount,
                null);
            System.out.println("UC\tfc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized
                               + "\n" + RMSE + "\n");
            FileUtil.writeAsAppend(resultDir + "zUC", "fc: " + featureCount + "\tlr: " + lrate
                                                      + "\tr: " + regularized + "\n" + RMSE + "\n");
        }
    }

    public static void ItemConstrainedRSVD(int[] featureCounts, String clusterDir, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int[] ia = new int[itemCount];
        int[] dimnsn = readBiAssigmnt(null, ia, clusterDir, rootDir);

        //build model
        for (int featureCount : featureCounts) {
            ISVD recmmd = new ISVD(userCount, itemCount, maxValue,
                minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[1], ia,
                showProgress);
            recmmd.tMatrix = testMatrix;
            recmmd.buildModel(rateMatrix);

            //evaluation
            double RMSE = MatrixInformationUtil.offlineRMSE(recmmd, testFile, userCount, itemCount,
                null);
            System.out.println("IC\tfc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized
                               + "\n" + RMSE + "\n");
            FileUtil.writeAsAppend(resultDir + "zIC", "fc: " + featureCount + "\tlr: " + lrate
                                                      + "\tr: " + regularized + "\n" + RMSE + "\n");
        }
    }

    public static void BiConstrainedRSVD(int[] featureCounts, String clusterDir, String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);

        int[] ua = new int[userCount];
        int[] ia = new int[itemCount];
        int[] dimnsn = readBiAssigmnt(ua, ia, clusterDir, rootDir);

        //build model
        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (int featureCount : featureCounts) {
            BiSVD recmmd = new BiSVD(userCount, itemCount, maxValue, minValue, featureCount,
                lrate, regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ua, ia, showProgress);
            recmmd.buildModel(rateMatrix);

            //evaluation
            double RMSE = MatrixInformationUtil.offlineRMSE(recmmd, testFile, userCount, itemCount,
                null);
            System.out.println("BC\tfc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized
                               + "\tk: " + dimnsn[0] + "\tl: " + dimnsn[1] + "\n" + RMSE + "\n");
            FileUtil.writeAsAppend(resultDir + "zBC[50]", "fc: " + featureCount + "\tlr: " + lrate
                                                          + "\tr: " + regularized + "\tk: "
                                                          + dimnsn[0] + "\tl: " + dimnsn[1] + "\n"
                                                          + RMSE + "\n");
            stat.addValue(RMSE);
        }
        //        FileUtil.writeAsAppend(
        //            resultDir + "zBC",
        //            "Mean: " + stat.getMean() + "\tSD:"
        //                    + String.format("%.6f", stat.getStandardDeviation()) + "\n");
    }

    public static int[] readBiAssigmnt(int[] ua, int[] ia, String clusterDir, String rootDir) {
        String settingFile = rootDir + clusterDir + "SETTING";
        String rowMappingFile = rootDir + clusterDir + "RM";
        String colMappingFile = rootDir + clusterDir + "CM";

        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        BlockMatrix blockMatrix = MatrixFileUtil.readEmptyBlock(settingFile, rowMappingFile,
            colMappingFile, rowAssig, colAssig);
        int[] biSize = new int[2];

        if (ua != null) {
            int[] userClusterBounds = blockMatrix.rowBound();
            for (Entry<Integer, Integer> uIndex : rowAssig.entrySet()) {
                for (int i = 0; i < userClusterBounds.length; i++) {
                    if (uIndex.getValue() < userClusterBounds[i]) {
                        ua[uIndex.getKey()] = i;
                        break;
                    }
                }
            }
            biSize[0] = userClusterBounds.length;
        }

        if (ia != null) {
            int[] itemClusterBounds = blockMatrix.structure()[0];
            for (Entry<Integer, Integer> iIndex : colAssig.entrySet()) {
                for (int i = 0; i < itemClusterBounds.length; i++) {
                    if (iIndex.getValue() < itemClusterBounds[i]) {
                        ia[iIndex.getKey()] = i;
                        break;
                    }
                }
            }
            biSize[1] = itemClusterBounds.length;
        }

        return biSize;
    }

}
