package edu.tongji.engine.recommendation;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import prea.util.KernelSmoothing;
import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.engine.recommendation.thread.AnchorWeightedSVDLearner;
import edu.tongji.ml.matrix.AnchorWeightedRSVD;
import edu.tongji.ml.matrix.RegularizedSVD;
import edu.tongji.parser.Parser;

/**
 * 
 * @author Hanke
 * @version $Id: LLRAMixtureRcmdEngine.java, v 0.1 2014-11-4 下午3:26:38 Exp $
 */
public class LLRAMixtureRcmdEngine extends RcmdtnEngine {

    /** file with training data*/
    private String               trainingSetFile;

    /** file with testing data*/
    private String               testingSetFile;
    /** The number of users. */
    public int                   userCount;
    /** The number of items. */
    public int                   itemCount;
    /** Maximum value of rating, existing in the dataset. */
    public double                maxValue;
    /** Minimum value of rating, existing in the dataset. */
    public double                minValue;
    /** The number of features. */
    public int                   featureCount;
    /** Learning rate parameter. */
    public double                learningRate;
    /** Regularization factor parameter. */
    public double                regularizer;
    /** Momentum parameter. */
    public double                momentum;
    /** Maximum number of iteration. */
    public int                   maxIter;

    private double               base1;

    private double               base2;

    private int                  modelMax;

    /** the content parser w.r.t certain dataset*/
    private Parser               parser;

    /** Similarity matrix between users. */
    private static SparseMatrix  userSimilarity;
    /** Similarity matrix between items. */
    private static SparseMatrix  itemSimilarity;

    //====================================
    //
    //====================================
    /** A global SVD model used for calculating user/item similarity. */
    public static RegularizedSVD baseline;

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#excute()
     */
    @Override
    public void excute() {
        //===========================
        userSimilarity = new SparseMatrix(userCount, userCount);
        itemSimilarity = new SparseMatrix(itemCount, itemCount);
        SparseMatrix rateMatrix = MatrixFileUtil
            .read(trainingSetFile, userCount, itemCount, parser);
        SparseMatrix testMatrix = MatrixFileUtil.read(testingSetFile, userCount, itemCount, parser);
        baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue, 10, learningRate,
            regularizer, 0, maxIter);
        baseline.buildModel(rateMatrix);

        //=========================
        Queue<AnchorWeightedRSVD> models = new LinkedList<AnchorWeightedRSVD>();
        for (int modelCount = 0; modelCount < modelMax; modelCount++) {
            int u_t = (int) Math.floor(Math.random() * userCount);
            int[] itemList = rateMatrix.getRowRef(u_t).indexList();
            while (itemList == null) {
                u_t = (int) Math.floor(Math.random() * userCount);
                itemList = rateMatrix.getRowRef(u_t).indexList();
            }
            int idx = (int) Math.floor(Math.random() * itemList.length);
            int i_t = itemList[idx];

            // Preparing weight vectors:
            SparseVector w = kernelSmoothing(userCount, u_t, KernelSmoothing.EPANECHNIKOV_KERNEL,
                0.8, false);
            SparseVector v = kernelSmoothing(itemCount, i_t, KernelSmoothing.EPANECHNIKOV_KERNEL,
                0.8, true);

            //add model
            models.add(new AnchorWeightedRSVD(userCount, itemCount, maxValue, minValue,
                featureCount, learningRate, regularizer, momentum, maxIter, base1, base2, w, v,
                u_t, i_t));
        }

        //==========================
        AnchorWeightedSVDLearner.models = models;
        AnchorWeightedSVDLearner.rateMatrix = rateMatrix;
        AnchorWeightedSVDLearner.testMatrix = testMatrix;
        AnchorWeightedSVDLearner.cumPrediction = new SparseMatrix(userCount, itemCount);
        AnchorWeightedSVDLearner.cumWeight = new SparseMatrix(userCount, itemCount);

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new AnchorWeightedSVDLearner());
        exec.execute(new AnchorWeightedSVDLearner());
        exec.execute(new AnchorWeightedSVDLearner());
        exec.execute(new AnchorWeightedSVDLearner());
        exec.shutdown();

    }

    //=============================================
    //
    //=============================================

    /**
     * Calculate similarity between two users, based on the global base SVD.
     * 
     * @param idx1 The first user's ID.
     * @param idx2 The second user's ID.
     * 
     * @return The similarity value between two users idx1 and idx2.
     */
    private double getUserSimilarity(int idx1, int idx2) {
        double sim;
        if (idx1 <= idx2) {
            sim = userSimilarity.getValue(idx1, idx2);
        } else {
            sim = userSimilarity.getValue(idx2, idx1);
        }

        if (sim == 0.0) {
            SparseVector u_vec = baseline.getU().getRowRef(idx1);
            SparseVector v_vec = baseline.getU().getRowRef(idx2);

            sim = 1 - 2.0 / Math.PI
                  * Math.acos(u_vec.innerProduct(v_vec) / (u_vec.norm() * v_vec.norm()));

            if (Double.isNaN(sim)) {
                sim = 0.0;
            }

            if (idx1 <= idx2) {
                userSimilarity.setValue(idx1, idx2, sim);
            } else {
                userSimilarity.setValue(idx2, idx1, sim);
            }
        }

        return sim;
    }

    /**
     * Calculate similarity between two items, based on the global base SVD.
     * 
     * @param idx1 The first item's ID.
     * @param idx2 The second item's ID.
     * 
     * @return The similarity value between two items idx1 and idx2.
     */
    private double getItemSimilarity(int idx1, int idx2) {
        double sim;
        if (idx1 <= idx2) {
            sim = itemSimilarity.getValue(idx1, idx2);
        } else {
            sim = itemSimilarity.getValue(idx2, idx1);
        }

        if (sim == 0.0) {
            SparseVector i_vec = baseline.getV().getColRef(idx1);
            SparseVector j_vec = baseline.getV().getColRef(idx2);

            sim = 1 - 2.0 / Math.PI
                  * Math.acos(i_vec.innerProduct(j_vec) / (i_vec.norm() * j_vec.norm()));

            if (Double.isNaN(sim)) {
                sim = 0.0;
            }

            if (idx1 <= idx2) {
                itemSimilarity.setValue(idx1, idx2, sim);
            } else {
                itemSimilarity.setValue(idx2, idx1, sim);
            }
        }

        return sim;
    }

    /**
     * Given the similarity, it applies the given kernel.
     * This is done either for all users or for all items.
     * 
     * @param size The length of user or item vector.
     * @param id The identifier of anchor point.
     * @param kernelType The type of kernel.
     * @param width Kernel width.
     * @param isItemFeature return item kernel if yes, return user kernel otherwise.
     * 
     * @return The kernel-smoothed values for all users or all items.
     */
    private SparseVector kernelSmoothing(int size, int id, int kernelType, double width,
                                         boolean isItemFeature) {
        SparseVector newFeatureVector = new SparseVector(size);
        newFeatureVector.setValue(id, 1.0);

        for (int i = 1; i < size; i++) {
            double sim;
            if (isItemFeature) {
                sim = getItemSimilarity(i, id);
            } else { // userFeature
                sim = getUserSimilarity(i, id);
            }

            newFeatureVector.setValue(i, KernelSmoothing.kernelize(sim, width, kernelType));
        }

        return newFeatureVector;
    }

    /**
     * Setter method for property <tt>trainingSetFile</tt>.
     * 
     * @param trainingSetFile value to be assigned to property trainingSetFile
     */
    public void setTrainingSetFile(String trainingSetFile) {
        this.trainingSetFile = trainingSetFile;
    }

    /**
     * Setter method for property <tt>testingSetFile</tt>.
     * 
     * @param testingSetFile value to be assigned to property testingSetFile
     */
    public void setTestingSetFile(String testingSetFile) {
        this.testingSetFile = testingSetFile;
    }

    /**
     * Setter method for property <tt>userCount</tt>.
     * 
     * @param userCount value to be assigned to property userCount
     */
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    /**
     * Setter method for property <tt>itemCount</tt>.
     * 
     * @param itemCount value to be assigned to property itemCount
     */
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * Setter method for property <tt>maxValue</tt>.
     * 
     * @param maxValue value to be assigned to property maxValue
     */
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Setter method for property <tt>minValue</tt>.
     * 
     * @param minValue value to be assigned to property minValue
     */
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    /**
     * Setter method for property <tt>featureCount</tt>.
     * 
     * @param featureCount value to be assigned to property featureCount
     */
    public void setFeatureCount(int featureCount) {
        this.featureCount = featureCount;
    }

    /**
     * Setter method for property <tt>learningRate</tt>.
     * 
     * @param learningRate value to be assigned to property learningRate
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    /**
     * Setter method for property <tt>regularizer</tt>.
     * 
     * @param regularizer value to be assigned to property regularizer
     */
    public void setRegularizer(double regularizer) {
        this.regularizer = regularizer;
    }

    /**
     * Setter method for property <tt>momentum</tt>.
     * 
     * @param momentum value to be assigned to property momentum
     */
    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    /**
     * Setter method for property <tt>maxIter</tt>.
     * 
     * @param maxIter value to be assigned to property maxIter
     */
    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    /**
     * Setter method for property <tt>base1</tt>.
     * 
     * @param base1 value to be assigned to property base1
     */
    public void setBase1(double base1) {
        this.base1 = base1;
    }

    /**
     * Setter method for property <tt>base2</tt>.
     * 
     * @param base2 value to be assigned to property base2
     */
    public void setBase2(double base2) {
        this.base2 = base2;
    }

    /**
     * Setter method for property <tt>modelMax</tt>.
     * 
     * @param modelMax value to be assigned to property modelMax
     */
    public void setModelMax(int modelMax) {
        this.modelMax = modelMax;
    }

    /**
     * Setter method for property <tt>parser</tt>.
     * 
     * @param parser value to be assigned to property parser
     */
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    /**
     * Setter method for property <tt>baseline</tt>.
     * 
     * @param baseline value to be assigned to property baseline
     */
    public static void setBaseline(RegularizedSVD baseline) {
        LLRAMixtureRcmdEngine.baseline = baseline;
    }

}
