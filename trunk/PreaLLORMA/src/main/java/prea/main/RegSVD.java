package prea.main;

import java.util.Date;

import org.apache.log4j.Logger;

import prea.data.structure.MatlabFasionSparseMatrix;
import prea.recommender.matrix.RegularizedSVD;
import prea.util.FileUtil;
import prea.util.LoggerDefineConstant;
import prea.util.LoggerUtil;
import prea.util.MatrixFileUtil;

public class RegSVD {
    /** training dataset file output path   10M100K*/
    protected final static String[] rootDirs  = { "C:/netflix/1/" };
    /** the number of rows      6040 69878*/
    public static int               userCount = 69878;
    /** the number of cloumns   3706 10677*/
    public static int               itemCount = 10677;
    public final static double      maxValue  = 5.0;
    public final static double      minValue  = 1.0;
    /** logger */
    protected final static Logger   logger    = Logger
                                                  .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    public final static String      resultDir = "C:/netflix/1/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int featureCount = 20;
        for (String rootDir : rootDirs) {
            doRSVD(rootDir, featureCount);
        }
    }

    public static void doRSVD(String rootDir, int featureCount) {
        double lrate = 0.01;
        double regularized = 0.001;
        int maxIter = 100;

        System.out.println("1. load trainingset." + new Date());
        String train = rootDir + "trainingset";
        MatlabFasionSparseMatrix trainSeq = MatrixFileUtil.reads(train, 60 * 1000 * 1000);

        System.out.println("2. excute SVD to cmp sim. " + new Date());
        RegularizedSVD baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue, 20,
            lrate, regularized, 0, maxIter, true);
        baseline.buildModel(trainSeq, null);

        System.out.println("3. load testset." + new Date());
        String test = rootDir + "testingset";
        MatlabFasionSparseMatrix testSeq = MatrixFileUtil.reads(test, 20 * 1000 * 1000);

        double RMSE = baseline.evaluate(testSeq);
        LoggerUtil.info(logger, "Train: " + train + "\tTest: " + test + "\n" + RMSE);

        FileUtil.writeAsAppend(resultDir + "RSVD", "fc: " + featureCount + "\tlr: " + lrate
                                                   + "\tr: " + regularized + "\tRMSE: " + RMSE
                                                   + "\n");
    }

}
