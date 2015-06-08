package prea.main;

import java.util.Date;

import org.apache.log4j.Logger;

import prea.data.structure.SparseMatrix;
import prea.recommender.matrix.BayesianPMF;
import prea.recommender.matrix.NMF;
import prea.util.FileUtil;
import prea.util.LoggerDefineConstant;
import prea.util.LoggerUtil;
import prea.util.MatrixFileUtil;
import prea.util.MatrixInformationUtil;

public class MFOfflineExp {

    /** training dataset file output path 1m 10M100K */
    protected final static String[] rootDirs  = { "C:/Netflix/Ratio2/1/", "C:/Netflix/Ratio2/2/",
            "C:/Netflix/Ratio2/3/"           };
    /** the number of rows 6040 69878 480189 */
    public final static int         userCount = 480189;
    /** the number of cloumns 3706 10677 17770 */
    public final static int         itemCount = 17770;
    public final static double      maxValue  = 5.0;
    public final static double      minValue  = 1.0;
    protected final static Logger   logger    = Logger
                                                  .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    public final static String      resultDir = "C:/Netflix/Ratio2/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        for (int i = 0; i < rootDirs.length; i++) {
            String train = rootDirs[i] + "trainingset";
            String test = rootDirs[i] + "testingset";

            // doNMF(train, test, 50);
            doBPMF(train, test, 20);
            System.gc();
        }
    }

    public static void doNMF(String trainFile, String testFile, int featureCount) {
        System.out.println("1. load netflix trainingset." + new Date());
        SparseMatrix rateMatrix = MatrixFileUtil.read(trainFile, userCount + 1, itemCount + 1);

        double lrate = 0.005;
        double regularized = 0.02;
        int maxIter = 20;
        System.out.println("2. excute NMF. " + new Date());
        NMF recmmd = new NMF(userCount, itemCount, maxValue, minValue, featureCount, 0,
            regularized, 0, maxIter, lrate, true);
        recmmd.buildModel(rateMatrix);
        rateMatrix.clear();

        double RMSE = MatrixInformationUtil.offlineRMSE(recmmd, testFile, userCount, itemCount);

        System.out.println("NMF: " + RMSE);
        LoggerUtil.info(logger, "NMF: Train: " + trainFile + "\tTest: " + testFile + "\n" + RMSE);
        FileUtil.writeAsAppend(resultDir + "NMF.log", "fc: " + featureCount + "\tlr: " + lrate
                                                      + "\tr: " + regularized + "\tRMSE: " + RMSE
                                                      + "\n");

    }

    public static void doBPMF(String trainFile, String testFile, int featureCount) {
        System.out.println("1. load netflix trainingset." + new Date());
        SparseMatrix rateMatrix = MatrixFileUtil.read(trainFile, userCount + 1, itemCount + 1);

        int maxIter = 10;
        System.out.println("2. excute BPMF. " + new Date());
        BayesianPMF recmmd = new BayesianPMF(userCount, itemCount, maxValue, minValue,
            featureCount, 0, 0, 0, maxIter, true);
        recmmd.buildModel(rateMatrix);
        rateMatrix.clear();

        double RMSE = MatrixInformationUtil.offlineRMSE(recmmd, testFile, userCount, itemCount);
        System.out.println("BPMF: " + RMSE);
        LoggerUtil.info(logger, "BPMF\tTrain: " + trainFile + "\tTest: " + testFile + "\n" + RMSE);
        FileUtil.writeAsAppend(resultDir + "BPMF.log", "fc: " + featureCount + "\tRMSE:  " + RMSE
                                                       + "\n");
    }

}
