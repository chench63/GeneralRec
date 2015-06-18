package paper.icdm15.exp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import prea.util.ClusteringInformationUtil;
import prea.util.MatrixFileUtil;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.ml.matrix.BiSVD;
import edu.tongji.util.ExceptionUtil;

public class BiSVDMT {

    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]      rootDirs     = { "E:/MovieLens/zWarmStart/ml-10M100K/2/",
            "E:/MovieLens/zWarmStart/ml-10M100K/3/", "E:/MovieLens/zWarmStart/ml-10M100K/4/",
            "E:/MovieLens/zWarmStart/ml-10M100K/5/" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int     userCount    = 69878;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int     itemCount    = 10677;
    public final static double  maxValue     = 5.0;
    public final static double  minValue     = 0.5;
    public final static double  lrate        = 0.001;
    public final static double  regularized  = 0.06;
    public final static int     maxIteration = 400;
    public final static boolean showProgress = true;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

    }

    public static void BiConstrainedRSVDSaving(int featureCount, String[] clusterDirs,
                                               String rootDir) {
        //loading dataset
        String trainFile = rootDir + "trainingset";
        String testFile = rootDir + "testingset";
        MatlabFasionSparseMatrix rateMatrix = MatrixFileUtil.reads(trainFile, 20 * 1000 * 1000,
            null);
        MatlabFasionSparseMatrix testMatrix = MatrixFileUtil
            .reads(testFile, 20 * 1000 * 1000, null);

        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            for (String clusterDir : clusterDirs) {
                exec.execute(new BiSVDWorker(rateMatrix, testMatrix, featureCount, clusterDir,
                    rootDir));
            }
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService await crush! ");
        }

    }

    protected static class BiSVDWorker extends Thread {
        MatlabFasionSparseMatrix rateMatrix;
        MatlabFasionSparseMatrix testMatrix;
        int                      featureCount;
        String                   clusterDir;
        String                   rootDir;

        /**
         * @param rateMatrix
         * @param testMatrix
         * @param featureCount
         * @param clusterDir
         * @param rootDir
         */
        public BiSVDWorker(MatlabFasionSparseMatrix rateMatrix,
                           MatlabFasionSparseMatrix testMatrix, int featureCount,
                           String clusterDir, String rootDir) {
            super();
            this.rateMatrix = rateMatrix;
            this.testMatrix = testMatrix;
            this.featureCount = featureCount;
            this.clusterDir = clusterDir;
            this.rootDir = rootDir;
        }

        /** 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            int[] ua = new int[userCount];
            int[] ia = new int[itemCount];
            int[] dimnsn = ClusteringInformationUtil.readBiAssigmnt(ua, ia, clusterDir, rootDir);

            //build model
            BiSVD recmmd = new BiSVD(userCount, itemCount, maxValue, minValue, featureCount,
                lrate, regularized, 0, maxIteration, dimnsn[0], dimnsn[1], ua, ia, showProgress);
            recmmd.buildModel(rateMatrix, testMatrix);

            //evaluation
            double rmse = recmmd.evaluate(testMatrix);
            System.out.println(rmse);
        }

    }

}
