package paper.sigir15.experiment;

import prea.util.EvaluationMetrics;
import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.ml.matrix.WeigtedRSVD;
import edu.tongji.util.FileUtil;

/**
 * 
 * @author Hanke
 * @version $Id: WeightsEffectExperimnt.java, v 0.1 2015-4-19 下午4:31:07 Exp $
 */
public class WeightsEffectExperiment {
    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]      rootDirs     = { "E:/MovieLens/zWarmStart/ml-10M100K/3/" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int     userCount    = 69878;
    /** The number of items. 1682 3706 10677 10677*/
    public final static int     itemCount    = 10677;
    public final static double  maxValue     = 5.0d;
    public final static double  minValue     = 0.5d;
    public final static int     featureCount = 20;
    public final static double  lrate        = 0.01d;
    public final static double  regularized  = 0.001d;
    public final static int     maxIteration = 100;
    public final static boolean showProgress = false;

    public final static String  resultDir    = "E:/MovieLens/zWarmStart/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        double[] beta0s = { 0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d, 1.0d };

        for (String rootDir : rootDirs) {
            WSVD(rootDir, beta0s);
        }
    }

    public static void WSVD(String rootDir, double[] beta0s) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainFile, userCount, itemCount, null);
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        for (double beta0 : beta0s) {
            WeigtedRSVD recmmd = new WeigtedRSVD(userCount, itemCount, maxValue, minValue,
                featureCount, lrate, regularized, 0, maxIteration, beta0, 0.0d, 0.0d, showProgress);
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(resultDir + "WSVD[3]", beta0 + "\t" + metric.printOneLine()
                                                          + "\n");
        }
    }

}
