package prea.main;

import java.util.Date;
import org.apache.log4j.Logger;
import prea.data.structure.SparseMatrix;
import prea.recommender.matrix.BayesianPMF;
import prea.recommender.matrix.NMF;
import prea.recommender.matrix.PMF;
import prea.recommender.matrix.RegularizedSVD;
import prea.util.EvaluationMetrics;
import prea.util.FileUtil;
import prea.util.LoggerDefineConstant;
import prea.util.LoggerUtil;
import prea.util.MatrixFileUtil;

public class MFFastExp {

    /** training dataset file output path 1m 10M100K*/
    protected final static String[] rootDirs  = { "C:/netflix/1/", "C:/netflix/2/",
            "C:/netflix/3/", "C:/netflix/4/", "C:/netflix/5/" };
    /** the number of rows      6040    69878 480189*/
    public final static int         userCount = 480189;
    /** the number of cloumns   3706    10677 17770*/
    public final static int         itemCount = 17770;
    public final static double      maxValue  = 5.0;
    public final static double      minValue  = 1.0;
    protected final static Logger   logger    = Logger
                                                  .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    public final static String      resultDir = "C:/netflix/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int[] featureCounts = { 50 };
        for (int i = 0; i < rootDirs.length; i++) {
            String train = rootDirs[i] + "trainingset";
            String test = rootDirs[i] + "testingset";

            doNMF(train, test, featureCounts);
            //            doRSVD(train, test, featureCounts);
            //            doBPMF(train, test, featureCounts);
            System.gc();
        }

    }

    public static void doNMF(String train, String test, int[] featureCounts) {
        System.out.println("1. load trainingset." + new Date());
        SparseMatrix rateMatrix = MatrixFileUtil.read(train, userCount + 1, itemCount + 1);

        System.out.println("2. load testset." + new Date());
        SparseMatrix testMatrix = MatrixFileUtil.read(test, itemCount + 1, itemCount + 1);

        double lrate = 0.005;
        double regularized = 0.02;
        int maxIter = 200;
        for (int featureCount : featureCounts) {
            System.out.println("3. excute NMF. rank: " + featureCount + "\t" + new Date());
            NMF baseline = new NMF(userCount, itemCount, maxValue, minValue, featureCount, 0,
                regularized, 0, maxIter, lrate, true);
            baseline.buildModel(rateMatrix);

            EvaluationMetrics metric = baseline.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            LoggerUtil.info(logger,
                "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());

            FileUtil.writeAsAppend(
                resultDir + "zNMF",
                "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\n"
                        + metric.printOneLine() + "\n");
        }

    }

    public static void doPMF(String train, String test, int[] featureCounts) {
        System.out.println("1. load trainingset." + new Date());
        SparseMatrix rateMatrix = MatrixFileUtil.read(train, userCount + 1, itemCount + 1);

        System.out.println("2. load testset." + new Date());
        SparseMatrix testMatrix = MatrixFileUtil.read(test, itemCount + 1, itemCount + 1);

        int maxIter = 200;
        for (int featureCount : featureCounts) {
            System.out.println("3. excute PMF. rank: " + featureCount + "\t" + new Date());
            PMF baseline = new PMF(userCount, itemCount, maxValue, minValue, featureCount, 50, 0.4,
                0.8, maxIter, true);
            baseline.buildModel(rateMatrix);

            EvaluationMetrics metric = baseline.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            LoggerUtil.info(logger,
                "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());

            FileUtil.writeAsAppend(resultDir + "zPMF",
                "fc: " + featureCount + "\n" + metric.printOneLine() + "\n");
        }
    }

    public static void doBPMF(String train, String test, int[] featureCounts) {
        System.out.println("1. load trainingset." + new Date());
        SparseMatrix rateMatrix = MatrixFileUtil.read(train, userCount + 1, itemCount + 1);

        System.out.println("2. load testset." + new Date());
        SparseMatrix testMatrix = MatrixFileUtil.read(test, itemCount + 1, itemCount + 1);

        int maxIter = 20;
        for (int featureCount : featureCounts) {
            System.out.println("3. excute BPMF. rank: " + featureCount + "\t" + new Date());
            BayesianPMF baseline = new BayesianPMF(userCount, itemCount, maxValue, minValue,
                featureCount, 0, 0, 0, maxIter, true);
            baseline.buildModel(rateMatrix);

            EvaluationMetrics metric = baseline.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            LoggerUtil.info(logger,
                "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());

            FileUtil.writeAsAppend(resultDir + "zBPMF",
                "fc: " + featureCount + "\n" + metric.printOneLine() + "\n");
        }
    }

    public static void doRSVD(String train, String test, int[] featureCounts) {
        System.out.println("1. load trainingset." + new Date());
        SparseMatrix rateMatrix = MatrixFileUtil.read(train, userCount + 1, itemCount + 1);

        System.out.println("2. load netflix testset." + new Date());
        SparseMatrix testMatrix = MatrixFileUtil.read(test, itemCount + 1, itemCount + 1);

        double lrate = 0.001;
        double regularized = 0.08;
        int maxIter = 250;

        for (int featureCount : featureCounts) {
            System.out.println("3. excute RSVD. rank: " + featureCount + "\t" + new Date());
            RegularizedSVD baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIter, true);
            baseline.buildModel(rateMatrix);

            EvaluationMetrics metric = baseline.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            LoggerUtil.info(logger,
                "Train: " + train + "\tTest: " + test + "\n" + metric.printMultiLine());

            FileUtil.writeAsAppend(
                resultDir + "zSVD",
                "fc: " + featureCount + "\tlr: " + lrate + "\tr: " + regularized + "\n"
                        + metric.printOneLine() + "\n");
        }
    }

}
