package paper.icdm15.experiment;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import prea.util.MatrixFileUtil;
import prea.util.MatrixInformationUtil;
import edu.tongji.data.BlockMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.ml.matrix.BiSVD;
import edu.tongji.ml.matrix.RegularizedSVD;
import edu.tongji.ml.matrix.variant.ISVD;
import edu.tongji.ml.matrix.variant.USVD;
import edu.tongji.util.FileUtil;

public class BiSVDRatingAnalysis {

    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String        rootDir      = "E:/MovieLens/ml-1m/1/";
    /** The number of users. 943 6040 69878*/
    public final static int     userCount    = 6040;
    /** The number of items. 1682 3706 10677*/
    public final static int     itemCount    = 3706;
    public final static double  maxValue     = 5.0;
    public final static double  minValue     = 1.0;
    public final static double  lrate        = 0.001;
    public final static double  regularized  = 0.08;
    public final static int     maxIteration = 100;
    public final static boolean showProgress = false;

    public final static String  resultDir    = "E:/";

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
        double[] userThreshhold = { 20, 40, 80, 160, 320, Double.MAX_VALUE };
        double[] itemThreshhold = { 10, 20, 40, 80, 160, 320, 640, Double.MAX_VALUE };

        RSVD(featureCounts, userThreshhold, itemThreshhold);
        //        UserConstrainedRSVD(featureCounts, clusterDir, userThreshhold, itemThreshhold);
        //        ItemConstrainedRSVD(featureCounts, clusterDir, userThreshhold, itemThreshhold);
        BiConstrainedRSVD(featureCounts, clusterDir, userThreshhold, itemThreshhold);
    }

    /*========================================
     * Algorithms
     *========================================*/

    public static void RSVD(int[] featureCounts, double[] userThreshhold, double[] itemThreshhold) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseMatrix rateMatrix = MatrixFileUtil.read(trainFile, userCount, itemCount, null);
        SparseMatrix testMatrix = MatrixFileUtil.read(testFile, userCount, itemCount, null);

        //build model
        for (int featureCount : featureCounts) {
            RegularizedSVD recmmd = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIteration, showProgress);
            recmmd.buildModel(rateMatrix);

            //evaluation
            double[] RMSEperUser = MatrixInformationUtil.RMSEAnalysisOfObservedRatingsPerUser(
                rateMatrix, testMatrix, recmmd, userThreshhold);
            StringBuilder usrMsg = new StringBuilder();
            for (int i = 0; i < userThreshhold.length; i++) {
                usrMsg.append(i + 1).append('\t').append(RMSEperUser[i]).append('\n');
            }
            FileUtil.write(resultDir + "zRSVD_User_RMSE", usrMsg.toString());

            StringBuilder itemMsg = new StringBuilder();
            double[] RMSEperItem = MatrixInformationUtil.RMSEAnalysisOfObservedRatingsPerItem(
                rateMatrix, testMatrix, recmmd, itemThreshhold);
            for (int i = 0; i < itemThreshhold.length; i++) {
                itemMsg.append(i + 1).append('\t').append(RMSEperItem[i]).append('\n');
            }
            FileUtil.write(resultDir + "zRSVD_Item_RMSE", itemMsg.toString());
        }
    }

    public static void UserConstrainedRSVD(int[] featureCounts, String clusterDir,
                                           double[] userThreshhold, double[] itemThreshhold) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseMatrix rateMatrix = MatrixFileUtil.read(trainFile, userCount, itemCount, null);
        SparseMatrix testMatrix = MatrixFileUtil.read(testFile, userCount, itemCount, null);

        int[] ua = new int[userCount];
        int[] dimnsn = readBiAssigmnt(ua, null, clusterDir);

        //build model
        for (int featureCount : featureCounts) {
            USVD recmmd = new USVD(userCount, itemCount, maxValue,
                minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[0], ua,
                showProgress);
            recmmd.buildModel(rateMatrix);

            //evaluation
            double[] RMSEperUser = MatrixInformationUtil.RMSEAnalysisOfObservedRatingsPerUser(
                rateMatrix, testMatrix, recmmd, userThreshhold);
            StringBuilder usrMsg = new StringBuilder();
            for (int i = 0; i < userThreshhold.length; i++) {
                usrMsg.append(i + 1).append('\t').append(RMSEperUser[i]).append('\n');
            }
            FileUtil.write(resultDir + "zUCSVD_User_RMSE", usrMsg.toString());

            StringBuilder itemMsg = new StringBuilder();
            double[] RMSEperItem = MatrixInformationUtil.RMSEAnalysisOfObservedRatingsPerItem(
                rateMatrix, testMatrix, recmmd, itemThreshhold);
            for (int i = 0; i < itemThreshhold.length; i++) {
                itemMsg.append(i + 1).append('\t').append(RMSEperItem[i]).append('\n');
            }
            FileUtil.write(resultDir + "zUCSVD_Item_RMSE", itemMsg.toString());
        }
    }

    public static void ItemConstrainedRSVD(int[] featureCounts, String clusterDir,
                                           double[] userThreshhold, double[] itemThreshhold) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseMatrix rateMatrix = MatrixFileUtil.read(trainFile, userCount, itemCount, null);
        SparseMatrix testMatrix = MatrixFileUtil.read(testFile, userCount, itemCount, null);

        int[] ia = new int[itemCount];
        int[] dimnsn = readBiAssigmnt(null, ia, clusterDir);

        //build model
        for (int featureCount : featureCounts) {
            ISVD recmmd = new ISVD(userCount, itemCount, maxValue,
                minValue, featureCount, lrate, regularized, 0, maxIteration, dimnsn[1], ia,
                showProgress);
            recmmd.buildModel(rateMatrix);

            //evaluation
            double[] RMSEperUser = MatrixInformationUtil.RMSEAnalysisOfObservedRatingsPerUser(
                rateMatrix, testMatrix, recmmd, userThreshhold);
            StringBuilder usrMsg = new StringBuilder();
            for (int i = 0; i < userThreshhold.length; i++) {
                usrMsg.append(i + 1).append('\t').append(RMSEperUser[i]).append('\n');
            }
            FileUtil.write(resultDir + "zICSVD_User_RMSE", usrMsg.toString());

            StringBuilder itemMsg = new StringBuilder();
            double[] RMSEperItem = MatrixInformationUtil.RMSEAnalysisOfObservedRatingsPerItem(
                rateMatrix, testMatrix, recmmd, itemThreshhold);
            for (int i = 0; i < itemThreshhold.length; i++) {
                itemMsg.append(i + 1).append('\t').append(RMSEperItem[i]).append('\n');
            }
            FileUtil.write(resultDir + "zICSVD_Item_RMSE", itemMsg.toString());
        }
    }

    public static void BiConstrainedRSVD(int[] featureCounts, String clusterDir,
                                         double[] userThreshhold, double[] itemThreshhold) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseMatrix rateMatrix = MatrixFileUtil.read(trainFile, userCount, itemCount, null);
        SparseMatrix testMatrix = MatrixFileUtil.read(testFile, userCount, itemCount, null);

        int[] ua = new int[userCount];
        int[] ia = new int[itemCount];
        int[] dimnsn = readBiAssigmnt(ua, ia, clusterDir);

        //build model
        for (int featureCount : featureCounts) {
            BiSVD recmmd = new BiSVD(userCount, itemCount, maxValue, minValue, featureCount,
                lrate, regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ua, ia, showProgress);
            recmmd.buildModel(rateMatrix);

            //evaluation
            double[] RMSEperUser = MatrixInformationUtil.RMSEAnalysisOfObservedRatingsPerUser(
                rateMatrix, testMatrix, recmmd, userThreshhold);
            StringBuilder usrMsg = new StringBuilder();
            for (int i = 0; i < userThreshhold.length; i++) {
                usrMsg.append(i + 1).append('\t').append(RMSEperUser[i]).append('\n');
            }
            FileUtil.write(resultDir + "zBCSVD_User_RMSE", usrMsg.toString());

            StringBuilder itemMsg = new StringBuilder();
            double[] RMSEperItem = MatrixInformationUtil.RMSEAnalysisOfObservedRatingsPerItem(
                rateMatrix, testMatrix, recmmd, itemThreshhold);
            for (int i = 0; i < itemThreshhold.length; i++) {
                itemMsg.append(i + 1).append('\t').append(RMSEperItem[i]).append('\n');
            }
            FileUtil.write(resultDir + "zBCSVD_Item_RMSE", itemMsg.toString());
        }
    }

    public static int[] readBiAssigmnt(int[] ua, int[] ia, String clusterDir) {
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
