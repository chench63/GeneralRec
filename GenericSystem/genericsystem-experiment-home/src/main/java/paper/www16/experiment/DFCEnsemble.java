package paper.www16.experiment;

import org.apache.log4j.Logger;

import prea.util.ClusteringInformationUtil;
import prea.util.EvaluationMetrics;
import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.ml.matrix.BorderFormConstraintSVD;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.SerializeUtil;

public final class DFCEnsemble {

    public static String        rootDir   = "E:/MovieLens/ml-10M100K/1/";
    public static String[]      mFiles    = { "Model/Serial0.obj", "Model/Serial1.obj",
            "Model/Serial2.obj", "Model/Serial3.obj" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int     userCount = 69878;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int     itemCount = 10677;
    public final static double  maxValue  = 5.0;
    public final static double  minValue  = 0.5;

    private static final Logger logger    = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        LoggerUtil.info(logger, "1. load clustering configures.");
        String clusterDir = "Kmeanspp/KL_2_2/";
        int[] ua = new int[userCount];
        int[] ia = new int[itemCount];
        int[] dimnsn = ClusteringInformationUtil.readBiAssigmnt(ua, ia, clusterDir, rootDir);

        LoggerUtil.info(logger, "2. load latent factors.");
        SparseVector[][] userFeatures = new SparseVector[userCount][dimnsn[1]];
        SparseVector[][] itemFeatures = new SparseVector[itemCount][dimnsn[0]];
        //        loadFeatures(ua, ia, dimnsn, userFeatures, itemFeatures);
        bruteForceLoadFeatures(ua, ia, dimnsn, userFeatures, itemFeatures);

        LoggerUtil.info(logger, "3. evaluate project_ens.");
        evaluate(userFeatures, itemFeatures, dimnsn);
    }

    public static void loadFeatures(int[] ua, int[] ia, int[] dimnsn,
                                    SparseVector[][] userFeatures, SparseVector[][] itemFeatures) {
        // loading features
        BorderFormConstraintSVD[][] lRcmmd = new BorderFormConstraintSVD[dimnsn[0]][dimnsn[1]];
        for (int i = 0; i < mFiles.length; i++) {
            LoggerUtil.info(logger, "\t\t" + mFiles[i]);
            int Ri = i / dimnsn[0];
            int Ci = i % dimnsn[0];
            lRcmmd[Ri][Ci] = (BorderFormConstraintSVD) SerializeUtil
                .readObject(rootDir + mFiles[i]);
        }

        // loading user features
        for (int u = 0; u < userCount; u++) {
            int Ru = ua[u];
            for (int iIndx = 0; iIndx < dimnsn[1]; iIndx++) {
                userFeatures[u][iIndx] = lRcmmd[Ru][iIndx].getU().getRowRef(u);
            }
        }

        //loading item features
        for (int i = 0; i < itemCount; i++) {
            int Ci = ia[i];
            for (int uIndx = 0; uIndx < dimnsn[0]; uIndx++) {
                itemFeatures[i][uIndx] = lRcmmd[uIndx][Ci].getV().getColRef(i);
            }
        }
    }

    public static void bruteForceLoadFeatures(int[] ua, int[] ia, int[] dimnsn,
                                              SparseVector[][] userFeatures,
                                              SparseVector[][] itemFeatures) {
        // loading features
        BorderFormConstraintSVD[] lRcmmd = new BorderFormConstraintSVD[dimnsn[0] * dimnsn[1]];
        for (int i = 0; i < mFiles.length; i++) {
            LoggerUtil.info(logger, "\t\t" + mFiles[i]);
            lRcmmd[i] = (BorderFormConstraintSVD) SerializeUtil.readObject(rootDir + mFiles[i]);
        }

        for (BorderFormConstraintSVD rcmmd : lRcmmd) {
            // loading user features
            for (int u = 0; u < userCount; u++) {
                SparseVector Uu = rcmmd.getU().getRowRef(u);
                if (Uu.itemCount() != 0) {
                    for (int iIndx = 0; iIndx < dimnsn[1]; iIndx++) {
                        if (userFeatures[u][iIndx] == null) {
                            userFeatures[u][iIndx] = Uu;
                            break;
                        }
                    }
                }
            }

            //loading item features
            for (int i = 0; i < itemCount; i++) {
                SparseVector Ii = rcmmd.getV().getColRef(i);
                if (Ii.itemCount() != 0) {
                    for (int uIndx = 0; uIndx < dimnsn[0]; uIndx++) {
                        if (itemFeatures[i][uIndx] == null) {
                            itemFeatures[i][uIndx] = Ii;
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void evaluate(SparseVector[][] userFeatures, SparseVector[][] itemFeatures,
                                int[] dimnsn) {
        String testFile = rootDir + "testingset";
        SparseRowMatrix tMatrix = MatrixFileUtil.reads(testFile, userCount, itemCount, null);

        int ensembleCount = dimnsn[0] * dimnsn[1];
        SparseRowMatrix pMatrix = new SparseRowMatrix(userCount, itemCount);
        for (int u = 0; u < userCount; u++) {
            int[] testItems = tMatrix.getRowRef(u).indexList();
            if (testItems == null) {
                continue;
            }

            for (int i : testItems) {
                //prediction process
                double AuiAccu = 0.0d;
                for (SparseVector uFeature : userFeatures[u]) {
                    for (SparseVector iFeature : itemFeatures[i]) {
                        // check null
                        if (uFeature == null | iFeature == null) {
                            throw new RuntimeException("uFeature or iFeature is null. u: " + u
                                                       + "\ti: " + i);
                        } else if (uFeature.itemCount() == 0 | iFeature.itemCount() == 0) {
                            throw new RuntimeException("uFeature or iFeature is empty. u: " + u
                                                       + "\ti: " + i);
                        }

                        double AuiEstm = uFeature.innerProduct(iFeature);

                        if (AuiEstm > maxValue) {
                            AuiEstm = maxValue;
                        } else if (AuiEstm < minValue) {
                            AuiEstm = minValue;
                        }
                        AuiAccu += AuiEstm;
                    }
                }
                pMatrix.setValue(u, i, AuiAccu / ensembleCount);
            }
        }

        EvaluationMetrics metric = new EvaluationMetrics(tMatrix, pMatrix, maxValue, minValue);
        LoggerUtil.info(logger, "Project_Ens :\n" + metric.printOneLine());
    }
}
