package paper.sigir15.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import prea.util.EvaluationMetrics;
import prea.util.MatrixFileUtil;
import prea.util.RecResultUtil;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;

public class EnsmblEffctWitPuPiExperiment {
    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]     rootDirs  = { "E:/MovieLens/zWarmStart/ml-10M100K/1/" };
    /** The number of users. 943 6040 69878  69878*/
    public final static int    userCount = 480189;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int    itemCount = 10677;
    public final static double maxValue  = 5.0d;
    public final static double minValue  = 0.5d;
    public final static int    modelNum  = 8;

    public final static String pFile     = "WEMAREC[20]_C25_IWEW_[23]x2_RWSVD";
    public final static String rFile     = "EnsmblHMP";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        doEnsembleWithPiPu();
    }

    public static void doEnsembleWithPiPu() {
        double[] beta1s = new double[21];
        for (int i = 0; i <= 20; i++) {
            beta1s[i] = i * 0.1;
        }
        double[] beta2s = beta1s;

        for (String rootDir : rootDirs) {
            cmp(rootDir, null, beta1s, beta2s);
            //            cmpWithSaving(rootDir, null, beta1s, beta2s);
        }
    }

    public static void cmp(String rootDir, double[] beta0s, double[] beta1s, double[] beta2s) {
        //load prediction information
        String predctFile = rootDir + pFile;
        String resltFile = rootDir + rFile;
        StringBuilder content = new StringBuilder();

        SparseRowMatrix testMatrix = new SparseRowMatrix(userCount, itemCount);
        SparseRowMatrix[] estMatrix = new SparseRowMatrix[modelNum];
        SparseRowMatrix[] puMatrix = new SparseRowMatrix[modelNum];
        SparseRowMatrix[] piMatrix = new SparseRowMatrix[modelNum];
        for (int i = 0; i < modelNum; i++) {
            estMatrix[i] = new SparseRowMatrix(userCount, itemCount);
            puMatrix[i] = new SparseRowMatrix(userCount, itemCount);
            piMatrix[i] = new SparseRowMatrix(userCount, itemCount);
        }

        RecResultUtil.readRec(predctFile, estMatrix, testMatrix, puMatrix, piMatrix);

        // ensemble process
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
                                   + String.format("%.2f", beta2) + "\tRMSE: " + metric.getRMSE()
                                   + "\t" + (new Date()));
            }
            content.append('\n');
        }

        FileUtil.write(resltFile, content.toString());
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
                    curWeight = weightWithPuPi(beta1, beta2, curPu, curPi);
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

    public static void cmpWithSaving(String rootDir, double[] beta0s, double[] beta1s,
                                     double[] beta2s) {
        //read testing set
        System.out.println("1. load testing set. " + (new Date()));
        String testFile = rootDir + "testingset";
        SparseRowMatrix testMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        String predctFile = rootDir + pFile;
        String resltFile = rootDir + rFile;
        StringBuilder content = new StringBuilder();
        for (double beta1 : beta1s) {
            for (double beta2 : beta2s) {
                //ensemble & evaluate 
                EvaluationMetrics metric = evlWithSaving(predctFile, 0.0d, beta1, beta2, testMatrix);
                content.append(String.format("%.2f", beta1)).append('\t')
                    .append(String.format("%.2f", beta2)).append('\t')
                    .append(metric.printOneLine()).append('\n');
                System.out.println("beta1: " + String.format("%.2f", beta1) + "\tbeta2: "
                                   + String.format("%.2f", beta2) + "\tRMSE: " + metric.getRMSE()
                                   + "\t" + (new Date()));
            }
            content.append('\n');
        }

        FileUtil.write(resltFile, content.toString());
    }

    protected static EvaluationMetrics evlWithSaving(String predctFile, double beta0, double beta1,
                                                     double beta2, SparseRowMatrix testMatrix) {
        BufferedReader reader = null;
        try {
            //Ensemble steps
            SparseRowMatrix cumPrediction = new SparseRowMatrix(userCount, itemCount);
            SparseRowMatrix cumWeight = new SparseRowMatrix(userCount, itemCount);

            File file = new File(predctFile);
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                //userId, itemId, AuiReal, AuiEst, Pu, Pi, Pr, GroupId
                String[] elemnts = line.split("\\,");
                int u = Integer.valueOf(elemnts[0]);
                int i = Integer.valueOf(elemnts[1]);
                double curPrediciton = Double.valueOf(elemnts[3]);
                double curWeight = 0.0d;
                double curPu = Double.valueOf(elemnts[4]);
                double curPi = Double.valueOf(elemnts[5]);
                curWeight = weightWithPuPi(beta1, beta2, curPu, curPi);

                double newCumPrediction = curPrediciton * curWeight + cumPrediction.getValue(u, i);
                double newCumWeight = curWeight + cumWeight.getValue(u, i);

                //update old values
                cumPrediction.setValue(u, i, newCumPrediction);
                cumWeight.setValue(u, i, newCumWeight);
            }

            //Evaluation steps
            EvaluationMetrics metric = evaluate(cumPrediction, cumWeight, testMatrix);
            return metric;
        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + predctFile);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return null;
    }

    public static double weightWithPuPi(double beta1, double beta2, double Pu, double Pi) {
        return 1.0d + beta1 * Pu + beta2 * Pi;
    }

    public static double weightWithPr(double beta0, double Pr) {
        return 1.0d + beta0 * Pr;
    }
}
