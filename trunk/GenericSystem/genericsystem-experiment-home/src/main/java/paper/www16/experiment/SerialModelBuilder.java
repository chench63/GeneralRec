package paper.www16.experiment;

import prea.util.MatrixFileUtil;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.ml.matrix.RegularizedSVD;
import edu.tongji.ml.matrix.WeigtedSVD;
import edu.tongji.util.SerializeUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: SerialModelBuilder.java, v 0.1 Aug 18, 2015 1:59:26 PM chench Exp $
 */
public class SerialModelBuilder {
    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String        rootDir      = "E:/MovieLens/zWarmStart/ml-10M100K/4/";
    /** The number of users. 943 6040 69878*/
    public final static int     userCount    = 69878;
    /** The number of items. 1682 3706 10677*/
    public final static int     itemCount    = 10677;
    public final static double  maxValue     = 5.0;
    public final static double  minValue     = 0.5;
    public final static double  lrate        = 0.001;
    public final static double  regularized  = 0.01;
    public final static int     maxIteration = 100;
    public final static boolean showProgress = false;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int[] featureCounts = { 20 };
        RSVD(featureCounts);
        //        WSVD(featureCounts);
    }

    public static void RSVD(int[] featureCounts) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        MatlabFasionSparseMatrix rateMatrix = MatrixFileUtil.reads(trainFile, 10 * 1000 * 1000,
            null);
        MatlabFasionSparseMatrix testMatrix = MatrixFileUtil
            .reads(testFile, 10 * 1000 * 1000, null);

        //build model
        for (int featureCount : featureCounts) {
            RegularizedSVD recmmd = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIteration, showProgress);
            recmmd.buildModel(rateMatrix, null);
            double rmse = recmmd.evaluate(testMatrix);
            System.out.print(rmse);

            //serialize 
            SerializeUtil
                .writeObject(recmmd, rootDir + "Model/RSVD_Fast[" + featureCount + "].OBJ");
        }
    }

    public static void WSVD(int[] featureCounts) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        MatlabFasionSparseMatrix rateMatrix = MatrixFileUtil.reads(trainFile, 10 * 1000 * 1000,
            null);
        MatlabFasionSparseMatrix testMatrix = MatrixFileUtil
            .reads(testFile, 10 * 1000 * 1000, null);

        //build model
        for (int featureCount : featureCounts) {
            WeigtedSVD recmmd = new WeigtedSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIteration, 0.4d, 0.0d, 0.0d);
            recmmd.buildModel(rateMatrix, null);
            double rmse = recmmd.evaluate(testMatrix);
            System.out.print(rmse);

            //serialize 
            SerializeUtil
                .writeObject(recmmd, rootDir + "Model/WSVD_Fast[" + featureCount + "].OBJ");
        }
    }
}
