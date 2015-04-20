package paper.sigir15.experiment;

import prea.util.EvaluationMetrics;
import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.ml.matrix.WRSVD;
import edu.tongji.util.FileUtil;

/**
 * 
 * @author Hanke
 * @version $Id: WeightsEffectExperimnt.java, v 0.1 2015-4-19 下午4:31:07 Exp $
 */
public class WeightsEffectExperiment {
    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]      rootDirs     = { "E:/MovieLens/ml-1m/1/" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int     userCount    = 6040;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int     itemCount    = 3706;
    public final static double  maxValue     = 5.0d;
    public final static double  minValue     = 1.0d;
    public final static int     featureCount = 5;
    public final static double  lrate        = 0.01d;
    public final static double  regularized  = 0.001d;
    public final static int     maxIteration = 100;
    public final static boolean showProgress = true;

    public final static String  resultDir    = "E:/[1m]";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        double[] beta0s = { 0.0d, 1.0d };

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
            WRSVD recmmd = new WRSVD(userCount, itemCount, maxValue, minValue, featureCount, lrate,
                regularized, 0, maxIteration, showProgress, beta0);
            recmmd.buildModel(rateMatrix);

            //evaluation
            EvaluationMetrics metric = recmmd.evaluate(testMatrix);
            System.out.println(metric.printMultiLine());
            FileUtil.writeAsAppend(resultDir + "WSVD", beta0 + "\t" + metric.printOneLine() + "\n");
        }
    }

}
