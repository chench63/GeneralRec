package paper.sigir15.experiment;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import prea.util.EvaluationMetrics;
import prea.util.MatrixFileUtil;
import prea.util.RecResultUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;

public class EnsmblEffctWitPuPiExperiment {
    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]       rootDirs      = { "E:/MovieLens/zWarmStart/ml-10M100K/1/" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int      userCount     = 69878;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int      itemCount     = 10677;
    public final static double   maxValue      = 5.0d;
    public final static double   minValue      = 0.5d;
    public static int            modelNum      = 8;
    public static int            offset        = 0;
    public final static int[]    groupIncluded = {};

    public final static String   pFile         = "WEMAREC[20]_C25_IWEW_[23]x2_RWSVD";
    public final static String[] rFiles        = { "EnsmblHMP[20]_GlobalPuPi_Step1",
            "EnsmblHMP[20]_GlobalPuPi_Step2", "EnsmblHMP[20]_GlobalPuPi_Step3",
            "EnsmblHMP[20]_GlobalPuPi_Step4"  };
    public static double[][]     gInU;
    public static double[][]     gInI;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        doEnsembleWithPiPu();
    }

    public static void doEnsembleWithPiPu() {

        for (String rootDir : rootDirs) {
            //1. load prediction information
            preproc(rootDir);
            SparseRowMatrix testMatrix = new SparseRowMatrix(userCount, itemCount);
            SparseRowMatrix[] estMatrix = new SparseRowMatrix[modelNum];
            SparseRowMatrix[] puMatrix = new SparseRowMatrix[modelNum];
            SparseRowMatrix[] piMatrix = new SparseRowMatrix[modelNum];
            for (int i = 0; i < modelNum; i++) {
                estMatrix[i] = new SparseRowMatrix(userCount, itemCount);
                puMatrix[i] = new SparseRowMatrix(userCount, itemCount);
                piMatrix[i] = new SparseRowMatrix(userCount, itemCount);
            }
            RecResultUtil.readRec(rootDir + pFile, estMatrix, testMatrix, puMatrix, piMatrix,
                groupIncluded);

            //2. create multiple thread settings
            double[][] beta1s = new double[rFiles.length][50];
            for (int i = 0; i < rFiles.length; i++) {
                for (int j = 0; j < 10; j++) {
                    beta1s[i][j] = offset + i + j * 0.1;
                }
            }

            double[] beta2s = new double[51];
            for (int i = 0; i <= 50; i++) {
                beta2s[i] = i * 0.1;
            }

            //3. ensemble in parallel
            try {
                ExecutorService exec = Executors.newCachedThreadPool();
                exec.execute(new EnsembleWorker(testMatrix, estMatrix, puMatrix, piMatrix,
                    beta1s[0], beta2s, rootDir + rFiles[0]));
                exec.execute(new EnsembleWorker(testMatrix, estMatrix, puMatrix, piMatrix,
                    beta1s[1], beta2s, rootDir + rFiles[1]));
                exec.execute(new EnsembleWorker(testMatrix, estMatrix, puMatrix, piMatrix,
                    beta1s[2], beta2s, rootDir + rFiles[2]));
                exec.execute(new EnsembleWorker(testMatrix, estMatrix, puMatrix, piMatrix,
                    beta1s[3], beta2s, rootDir + rFiles[3]));
                exec.shutdown();
                exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                ExceptionUtil.caught(e, "ExecutorService await crush! ");
            }
        }

    }

    public static void preproc(String rootDir) {
        // set the number of models in Ensemble Process
        modelNum = (groupIncluded == null | groupIncluded.length == 0) ? modelNum
            : groupIncluded.length;
        SparseMatrix tMatrix = MatrixFileUtil.read(rootDir + "testingset", userCount, itemCount,
            null);
        gInU = tMatrix.probability(null, null, maxValue, 1.0d, true);
        gInI = tMatrix.probability(null, null, maxValue, 1.0d, false);
        tMatrix.clear();
        System.gc();
    }

    protected static class EnsembleWorker extends Thread {
        SparseRowMatrix   testMatrix;
        SparseRowMatrix[] estMatrix;
        SparseRowMatrix[] puMatrix;
        SparseRowMatrix[] piMatrix;
        double[]          beta1s;
        double[]          beta2s;
        String            resltFile;

        /**
         * @param testMatrix
         * @param estMatrix
         * @param puMatrix
         * @param piMatrix
         * @param beta1s
         * @param beta2s
         * @param resltFile
         */
        public EnsembleWorker(SparseRowMatrix testMatrix, SparseRowMatrix[] estMatrix,
                              SparseRowMatrix[] puMatrix, SparseRowMatrix[] piMatrix,
                              double[] beta1s, double[] beta2s, String resltFile) {
            super();
            this.testMatrix = testMatrix;
            this.estMatrix = estMatrix;
            this.puMatrix = puMatrix;
            this.piMatrix = piMatrix;
            this.beta1s = beta1s;
            this.beta2s = beta2s;
            this.resltFile = resltFile;
        }

        /** 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            // ensemble process
            StringBuilder content = new StringBuilder();
            for (double beta1 : beta1s) {
                for (double beta2 : beta2s) {

                    SparseRowMatrix cumPrediction = new SparseRowMatrix(userCount, itemCount);
                    SparseRowMatrix cumWeight = new SparseRowMatrix(userCount, itemCount);

                    ensemble(0.0d, beta1, beta2, cumPrediction, cumWeight, estMatrix, testMatrix,
                        puMatrix, piMatrix, null);
                    EvaluationMetrics metric = evaluate(cumPrediction, cumWeight, testMatrix);
                    content.append(String.format("%.2f", beta1)).append('\t')
                        .append(String.format("%.2f", beta2)).append('\t')
                        .append(metric.printOneLine()).append('\n');
                    System.out.println("beta1: " + String.format("%.2f", beta1) + "\tbeta2: "
                                       + String.format("%.2f", beta2) + "\tRMSE: "
                                       + metric.getRMSE() + "\t" + (new Date()));
                }
                content.append('\n');
            }

            FileUtil.write(resltFile, content.toString());
        }

    }

    protected static void ensemble(double beta0, double beta1, double beta2,
                                   SparseRowMatrix cumPrediction, SparseRowMatrix cumWeight,
                                   SparseRowMatrix[] estMatrix, SparseRowMatrix testMatrix,
                                   SparseRowMatrix[] puMatrix, SparseRowMatrix[] piMatrix,
                                   SparseRowMatrix[] prMatrix) {
        for (int g = 0; g < modelNum; g++) {
            for (int u = 0; u < userCount; u++) {
                SparseVector Fu = testMatrix.getRowRef(u);
                int[] itemIndex = Fu.indexList();
                if (itemIndex == null) {
                    continue;
                }

                for (int i : itemIndex) {
                    double curPrediciton = estMatrix[g].getValue(u, i);
                    double curWeight = 0.0d;
                    double curPu = puMatrix[g].getValue(u, i);
                    double curPi = piMatrix[g].getValue(u, i);
                    curWeight = weightWithPuPi(u, i, beta1, beta2, curPu, curPi, curPrediciton);
                    double newCumPrediction = curPrediciton * curWeight
                                              + cumPrediction.getValue(u, i);
                    double newCumWeight = curWeight + cumWeight.getValue(u, i);

                    //update old values
                    cumPrediction.setValue(u, i, newCumPrediction);
                    cumWeight.setValue(u, i, newCumWeight);
                }
            }
        }
    }

    protected static EvaluationMetrics evaluate(SparseRowMatrix cumPrediction,
                                                SparseRowMatrix cumWeight,
                                                SparseRowMatrix testMatrix) {
        //compute predictions
        for (int u = 0; u < userCount; u++) {
            int[] itemList = testMatrix.getRowRef(u).indexList();
            if (itemList == null) {
                continue;
            }

            for (int i : itemList) {
                double cP = cumPrediction.getValue(u, i);
                double cW = cumWeight.getValue(u, i);

                double AuiEst = cP / cW;
                if (AuiEst > maxValue) {
                    AuiEst = maxValue;
                } else if (AuiEst < minValue) {
                    AuiEst = minValue;
                }
                cumWeight.setValue(u, i, AuiEst);
            }
        }

        //evaluation
        return new EvaluationMetrics(testMatrix, cumWeight, maxValue, minValue);
    }

    public static double weightWithPuPi(int u, int i, double beta1, double beta2, double Pu,
                                        double Pi, double AuiEsm) {
        double gPu = gInU[u][(int) (AuiEsm / 1.0 - 1)];
        double gPi = gInI[i][(int) (AuiEsm / 1.0 - 1)];
        return 1.0d + beta1 * gPu + beta2 * gPi;
    }

    //    public static void cmpWithSaving(String rootDir, double[] beta0s, double[] beta1s,
    //                                     double[] beta2s) {
    //        //read testing set
    //        System.out.println("1. load testing set. " + (new Date()));
    //        String testFile = rootDir + "testingset";
    //        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);
    //
    //        String predctFile = rootDir + pFile;
    //        String resltFile = rootDir + rFile;
    //        StringBuilder content = new StringBuilder();
    //        for (double beta1 : beta1s) {
    //            for (double beta2 : beta2s) {
    //                //ensemble & evaluate 
    //                EvaluationMetrics metric = evlWithSaving(predctFile, 0.0d, beta1, beta2, testMatrix);
    //                content.append(String.format("%.2f", beta1)).append('\t')
    //                    .append(String.format("%.2f", beta2)).append('\t')
    //                    .append(metric.printOneLine()).append('\n');
    //                System.out.println("beta1: " + String.format("%.2f", beta1) + "\tbeta2: "
    //                                   + String.format("%.2f", beta2) + "\tRMSE: " + metric.getRMSE()
    //                                   + "\t" + (new Date()));
    //            }
    //            content.append('\n');
    //        }
    //
    //        FileUtil.write(resltFile, content.toString());
    //    }
    //
    //    protected static EvaluationMetrics evlWithSaving(String predctFile, double beta0, double beta1,
    //                                                     double beta2, SparseRowMatrix testMatrix) {
    //        BufferedReader reader = null;
    //        try {
    //            //Ensemble steps
    //            SparseRowMatrix cumPrediction = new SparseRowMatrix(userCount, itemCount);
    //            SparseRowMatrix cumWeight = new SparseRowMatrix(userCount, itemCount);
    //
    //            File file = new File(predctFile);
    //            reader = new BufferedReader(new FileReader(file));
    //            String line = null;
    //            while ((line = reader.readLine()) != null) {
    //                //userId, itemId, AuiReal, AuiEst, Pu, Pi, Pr, GroupId
    //                String[] elemnts = line.split("\\,");
    //                int u = Integer.valueOf(elemnts[0]);
    //                int i = Integer.valueOf(elemnts[1]);
    //                double curPrediciton = Double.valueOf(elemnts[3]);
    //                double curWeight = 0.0d;
    //                double curPu = Double.valueOf(elemnts[4]);
    //                double curPi = Double.valueOf(elemnts[5]);
    //                curWeight = weightWithPuPi(u, i, beta1, beta2, curPu, curPi, curPrediciton);
    //
    //                double newCumPrediction = curPrediciton * curWeight + cumPrediction.getValue(u, i);
    //                double newCumWeight = curWeight + cumWeight.getValue(u, i);
    //
    //                //update old values
    //                cumPrediction.setValue(u, i, newCumPrediction);
    //                cumWeight.setValue(u, i, newCumWeight);
    //            }
    //
    //            //Evaluation steps
    //            EvaluationMetrics metric = evaluate(cumPrediction, cumWeight, testMatrix);
    //            return metric;
    //        } catch (FileNotFoundException e) {
    //            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + predctFile);
    //        } catch (IOException e) {
    //            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
    //        } finally {
    //            IOUtils.closeQuietly(reader);
    //        }
    //
    //        return null;
    //    }
}
