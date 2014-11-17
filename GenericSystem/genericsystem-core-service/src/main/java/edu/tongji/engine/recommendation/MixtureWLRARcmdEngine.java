package edu.tongji.engine.recommendation;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import prea.util.MatrixFileUtil;
import edu.tongji.data.Model;
import edu.tongji.data.ModelGroup;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.engine.recommendation.thread.WeightedSVDLearner;
import edu.tongji.parser.Parser;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: MixtureWLRARcmdEngine.java, v 0.1 2014-11-2 下午2:37:12 Exp $
 */
public class MixtureWLRARcmdEngine extends RcmdtnEngine {

    /** file with training data*/
    private String           trainingSetFile;

    /** file with testing data*/
    private String           testingSetFile;

    /** The number of users. */
    private int              userCount;

    /** The number of items. */
    private int              itemCount;

    /** groups of model */
    private List<ModelGroup> groups;

    /** the content parser w.r.t certain dataset*/
    private Parser           parser;

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#loadDataSet()
     */
    @Override
    protected void loadDataSet() {
        LoggerUtil.info(logger, "1. loading data set. Groups: " + groups.size());

        // construct queue of models
        SparseMatrix rateMatrix = MatrixFileUtil
            .read(trainingSetFile, userCount, itemCount, parser);
        Queue<Model> models = new LinkedList<Model>();
        for (ModelGroup group : groups) {
            group.join(models, rateMatrix);
        }
        groups.clear();
        groups = null;
        WeightedSVDLearner.models = models;

        // construct training matrix
        SparseRowMatrix rowMatrix = new SparseRowMatrix(userCount, itemCount);
        for (int u = 0; u < userCount; u++) {
            SparseVector Fu = rateMatrix.getRowRef(u);
            int[] indexList = Fu.indexList();
            if (indexList == null) {
                continue;
            }

            for (int i : indexList) {
                rowMatrix.setValue(u, i, rateMatrix.getValue(u, i));
                rateMatrix.setValue(u, i, 0.0d);
            }
        }
        rateMatrix = null;
        WeightedSVDLearner.rateMatrix = rowMatrix;

        // construct test matrix
        WeightedSVDLearner.testMatrix = MatrixFileUtil.reads(testingSetFile, userCount, itemCount,
            parser);
        WeightedSVDLearner.cumPrediction = new SparseRowMatrix(userCount, itemCount);
        WeightedSVDLearner.cumWeight = new SparseRowMatrix(userCount, itemCount);
        LoggerUtil.info(logger, "Train: " + WeightedSVDLearner.rateMatrix.itemCount() + "\tTest: "
                                + WeightedSVDLearner.testMatrix.itemCount());
    }

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#excuteInner()
     */
    @Override
    protected void excuteInner() {
    }

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#evaluate()
     */
    @Override
    protected void evaluate() {
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new WeightedSVDLearner());
        exec.execute(new WeightedSVDLearner());
        exec.execute(new WeightedSVDLearner());
        exec.execute(new WeightedSVDLearner());
        exec.shutdown();
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
     * Setter method for property <tt>groups</tt>.
     * 
     * @param groups value to be assigned to property groups
     */
    public void setGroups(List<ModelGroup> groups) {
        this.groups = groups;
    }

    /**
     * Setter method for property <tt>parser</tt>.
     * 
     * @param parser value to be assigned to property parser
     */
    public void setParser(Parser parser) {
        this.parser = parser;
    }

}
