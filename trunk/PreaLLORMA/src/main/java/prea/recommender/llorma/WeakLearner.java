package prea.recommender.llorma;

import org.apache.log4j.Logger;

import prea.data.structure.FeatureMatrix;
import prea.data.structure.MatlabFasionSparseMatrix;
import prea.data.structure.SparseVector;
import prea.util.LoggerDefineConstant;
import prea.util.LoggerUtil;

/**
 * A class learning each local model used in singleton LLORMA.
 * Implementation is based on weighted-SVD.
 * Technical detail of the algorithm can be found in
 * Joonseok Lee and Seungyeon Kim and Guy Lebanon and Yoram Singer, Local Low-Rank Matrix Approximation,
 * Proceedings of the 30th International Conference on Machine Learning, 2013.
 * 
 * @author Joonseok Lee
 * @since 2013. 6. 11
 * @version 1.2
 */
public class WeakLearner extends Thread {
    /** The unique identifier of the thread. */
    private int                      threadId;
    /** The number of features. */
    private int                      rank;
    /** The number of users. */
    private int                      userCount;
    /** The number of items. */
    private int                      itemCount;
    /** The anchor user used to learn this local model. */
    private int                      anchorUser;
    /** The anchor item used to learn this local model. */
    private int                      anchorItem;
    /** Learning rate parameter. */
    public double                    learningRate;
    /** The maximum number of iteration. */
    public int                       maxIter;
    /** Regularization factor parameter. */
    public double                    regularizer;
    /** The vector containing each user's weight. */
    private SparseVector             w;
    /** The vector containing each item's weight. */
    private SparseVector             v;
    /** User profile in low-rank matrix form. */
    //    private SparseRowMatrix       userFeatures;
    /** Item profile in low-rank matrix form. */
    //    private SparseColumnMatrix    itemFeatures;
    /** The rating matrix used for learning. */
    private MatlabFasionSparseMatrix rateMatrix;
    /** The current train error. */
    private double                   trainErr;

    private FeatureMatrix            uDenseFeature;
    private FeatureMatrix            iDenseFeature;

    /** logger */
    protected final static Logger    logger = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

    /**
     * Construct a local model for singleton LLORMA.
     * 
     * @param id A unique thread ID.
     * @param rk The rank which will be used in this local model.
     * @param u The number of users.
     * @param i The number of items.
     * @param au The anchor user used to learn this local model.
     * @param ai The anchor item used to learn this local model.
     * @param lr Learning rate parameter.
     * @param r Regularization factor parameter.
     * @param w0 Initial vector containing each user's weight.
     * @param v0 Initial vector containing each item's weight.
     * @param rm The rating matrix used for learning.
     */
    public WeakLearner(int id, int rk, int u, int i, int au, int ai, double lr, double r, int iter,
                       SparseVector w0, SparseVector v0, MatlabFasionSparseMatrix rm) {
        threadId = id;
        rank = rk;
        userCount = u;
        itemCount = i;
        anchorUser = au;
        anchorItem = ai;
        learningRate = lr;
        regularizer = r;
        maxIter = iter;
        w = w0;
        v = v0;
        //        userFeatures = new SparseRowMatrix(userCount + 1, rank);
        //        itemFeatures = new SparseColumnMatrix(rank, itemCount + 1);
        uDenseFeature = new FeatureMatrix(userCount + 1, rank);
        iDenseFeature = new FeatureMatrix(rank, itemCount + 1);
        rateMatrix = rm;
    }

    /**
     * Getter method for thread ID.
     * 
     * @return The thread ID of this local model.
     */
    public int getThreadId() {
        return threadId;
    }

    /**
     * Getter method for rank of this local model.
     * 
     * @return The rank of this local model.
     */
    public int getRank() {
        return rank;
    }

    /**
     * Getter method for anchor user of this local model.
     * 
     * @return The anchor user ID of this local model.
     */
    public int getAnchorUser() {
        return anchorUser;
    }

    /**
     * Getter method for anchor item of this local model.
     * 
     * @return The anchor item ID of this local model.
     */
    public int getAnchorItem() {
        return anchorItem;
    }

    /**
     * Getter method for user profile of this local model.
     * 
     * @return The user profile of this local model.
     */
    //    public SparseRowMatrix getUserFeatures() {
    //        return userFeatures;
    //    }

    /**
     * Getter method for item profile of this local model.
     * 
     * @return The item profile of this local model.
     */
    //    public SparseColumnMatrix getItemFeatures() {
    //        return itemFeatures;
    //    }

    /**
     * Getter method for current train error.
     * 
     * @return The current train error.
     */
    public double getTrainErr() {
        return trainErr;
    }

    /**
     * clear memory
     */
    public void explicitClear() {
        w.clear();
        v.clear();
        rateMatrix = null;
        w = null;
        v = null;
    }

    /** Learn this local model based on similar users to the anchor user
     * and similar items to the anchor item.
     * Implemented with gradient descent. */
    @Override
    public void run() {
        //System.out.println("[START] Learning thread " + threadId);

        trainErr = Double.MAX_VALUE;
        boolean showProgress = true;

        for (int u = 1; u <= userCount; u++) {
            for (int r = 0; r < rank; r++) {
                double rdm = Math.random();
                uDenseFeature.setValue(u, r, rdm);
            }
        }
        for (int i = 1; i <= itemCount; i++) {
            for (int r = 0; r < rank; r++) {
                double rdm = Math.random();
                iDenseFeature.setValue(r, i, rdm);
            }
        }

        // Learn by Weighted RegSVD
        int round = 0;
        int rateCount = rateMatrix.getNnz();
        double prevErr = 99999;
        double currErr = 9999;

        int[] uIndx = rateMatrix.getRowIndx();
        int[] iIndx = rateMatrix.getColIndx();
        double[] Auis = rateMatrix.getVals();
        while (Math.abs(prevErr - currErr) > 0.0001 && round < maxIter) {
            double sum = 0.0;

            for (int numSeq = 0; numSeq < rateCount; numSeq++) {
                int u = uIndx[numSeq];
                int i = iIndx[numSeq];
                double RuiReal = Auis[numSeq];

                double RuiEst = prediction(u, i);
                //                        for (int r = 0; r < rank; r++) {
                //                            RuiEst += userFeatures.getValue(u, r) * itemFeatures.getValue(r, i);
                //                        }
                double err = RuiReal - RuiEst;
                sum += Math.pow(err, 2);

                double weight = w.getValue(u) * v.getValue(i);

                for (int r = 0; r < rank; r++) {
                    double Fus = uDenseFeature.getValue(u, r);
                    double Gis = iDenseFeature.getValue(r, i);
                    //                            double Fus = userFeatures.getValue(u, r);
                    //                            double Gis = itemFeatures.getValue(r, i);

                    uDenseFeature.setValue(u, r, Fus + learningRate
                                                 * (err * Gis * weight - regularizer * Fus));
                    if (Double.isNaN(Fus + learningRate * (err * Gis * weight - regularizer * Fus))) {
                        System.out.println("a");
                    }
                    iDenseFeature.setValue(r, i, Gis + learningRate
                                                 * (err * Fus * weight - regularizer * Gis));
                    if (Double.isNaN(Gis + learningRate * (err * Fus * weight - regularizer * Gis))) {
                        System.out.println("b");
                    }
                }
            }

            prevErr = currErr;
            currErr = sum / rateCount;
            trainErr = Math.sqrt(currErr);

            round++;

            // Show progress:
            if (showProgress) {
                LoggerUtil.info(logger, "[" + threadId + "]\t" + round + "\t" + currErr);
                //                System.out.println(round + "\t" + currErr);
            }
        }

        //System.out.println("[END] Learning thread " + threadId);
    }

    public double prediction(int u, int i) {
        double AuiEst = 0.0d;
        for (int f = 0; f < rank; f++) {
            AuiEst += uDenseFeature.getValue(u, f) * iDenseFeature.getValue(f, i);
        }
        return AuiEst;
    }

}