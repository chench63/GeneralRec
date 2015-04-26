package prea.main;

import java.util.Date;

import org.apache.log4j.Logger;

import prea.data.structure.SparseRowMatrix;
import prea.recommender.matrix.RegularizedSVD;
import prea.util.EvaluationMetrics;
import prea.util.FileUtil;
import prea.util.LoggerDefineConstant;
import prea.util.LoggerUtil;
import prea.util.MatrixFileUtil;

public class RSVD {
    /** training dataset file output path   10M100K*/
    protected final static String[] rootDirs  = { "C:/netflix/1/" };
    /** the number of rows      6040 69878*/
    public static int               userCount = 69878;
    /** the number of cloumns   3706 10677*/
    public static int               itemCount = 10677;
    /** logger */
    protected final static Logger   logger    = Logger
                                                  .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    public final static String      resultDir = "C:/netflix/";

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
        System.out.println("1. load trainingset." + new Date());
        String train = rootDir + "trainingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(train, userCount + 1, itemCount + 1);

        double maxValue = 5.0;
        double minValue = 0.5;
        double lrate = 0.01;
        double regularized = 0.001;
        int maxIteration = 100;

        System.out.println("2. excute RSVD. " + new Date());
        RegularizedSVD recmmd = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
            featureCount, lrate, regularized, 0, maxIteration, true);
        recmmd.buildModel(rateMatrix);

        System.out.println("3. load testset." + new Date());
        String test = rootDir + "testingset";
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(test, userCount + 1, itemCount + 1);

        EvaluationMetrics metric = recmmd.evaluate(testMatrix);
        System.out.println(metric.printMultiLine());
        LoggerUtil.info(logger,
            "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());

        FileUtil.writeAsAppend(
            resultDir + "zSVD",
            "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\n"
                    + metric.printOneLine() + "\n");
    }

}
