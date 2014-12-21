package edu.tongji.engine.recommendation;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import prea.util.MatrixFileUtil;
import edu.tongji.data.Model;
import edu.tongji.data.ModelGroup;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.engine.recommendation.thread.WeightedSVDLearner;
import edu.tongji.parser.Parser;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author Hanke
 * @version $Id: MixtureWLRARcmdEngine.java, v 0.1 2014-11-2 下午2:37:12 Exp $
 */
public class MixtureWLRARcmdEngine extends RcmdtnEngine {

    /** the directory contains training, test dataset and coclustering setting file*/
    private String           rootDir;

    /** file with training data */
    private String           trainingSetFile;

    /** file with testing data */
    private String           testingSetFile;

    /** The number of users. */
    private int              userCount;

    /** The number of items. */
    private int              itemCount;

    /** groups of model */
    private List<ModelGroup> groups;

    /** the content parser w.r.t certain dataset */
    private Parser           parser;

    /**
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#loadDataSet()
     */
    @Override
    protected void loadDataSet() {
        LoggerUtil.info(logger, "1. loading data set. Groups: " + groups.size());
        // construct queue of models
        if (StringUtil.isBlank(trainingSetFile) && StringUtil.isBlank(testingSetFile)) {
            trainingSetFile = rootDir + "trainingset";
            testingSetFile = rootDir + "testingset";
        }
        ModelGroup.setRootDir(rootDir);
        joinGroup();

        // construct training matrix
        LoggerUtil.info(logger, "\t\td. loading rateMatrix and testMatrix. ");
        WeightedSVDLearner.rateMatrix = MatrixFileUtil.reads(trainingSetFile, userCount, itemCount,
            parser);
        // construct test matrix
        WeightedSVDLearner.testMatrix = MatrixFileUtil.reads(testingSetFile, userCount, itemCount,
            parser);
        WeightedSVDLearner.cumPrediction = new SparseRowMatrix(userCount, itemCount);
        WeightedSVDLearner.cumWeight = new SparseRowMatrix(userCount, itemCount);
        WeightedSVDLearner.curRMSE = 0;
        LoggerUtil.info(logger, "Train: " + WeightedSVDLearner.rateMatrix.itemCount() + "\tTest: "
                                + WeightedSVDLearner.testMatrix.itemCount());
    }

    /**
     * join model of group to task list
     */
    protected void joinGroup() {
        LoggerUtil.info(logger, "\t\ta. loading trainingset. ");
        SparseMatrix rateMatrix = MatrixFileUtil
            .read(trainingSetFile, userCount, itemCount, parser);

        LoggerUtil.info(logger, "\t\tb. loading model. ");
        Queue<Model> models = new LinkedList<Model>();
        for (ModelGroup group : groups) {
            group.join(models, rateMatrix);
        }

        // suggest JVM to release memory
        LoggerUtil.info(logger, "\t\tc. releasing mem. ");
        rateMatrix.clear();
//        System.gc();
        groups.clear();
        groups = null;
        WeightedSVDLearner.models = models;
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
        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new WeightedSVDLearner());
            exec.execute(new WeightedSVDLearner());
            exec.execute(new WeightedSVDLearner());
            exec.execute(new WeightedSVDLearner());
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService await crush! ");
        }
    }

    /**
     * Setter method for property <tt>trainingSetFile</tt>.
     * 
     * @param trainingSetFile
     *            value to be assigned to property trainingSetFile
     */
    public void setTrainingSetFile(String trainingSetFile) {
        this.trainingSetFile = trainingSetFile;
    }

    /**
     * Setter method for property <tt>testingSetFile</tt>.
     * 
     * @param testingSetFile
     *            value to be assigned to property testingSetFile
     */
    public void setTestingSetFile(String testingSetFile) {
        this.testingSetFile = testingSetFile;
    }

    /**
     * Setter method for property <tt>userCount</tt>.
     * 
     * @param userCount
     *            value to be assigned to property userCount
     */
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    /**
     * Setter method for property <tt>itemCount</tt>.
     * 
     * @param itemCount
     *            value to be assigned to property itemCount
     */
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * Setter method for property <tt>groups</tt>.
     * 
     * @param groups
     *            value to be assigned to property groups
     */
    public void setGroups(List<ModelGroup> groups) {
        this.groups = groups;
    }

    /**
     * Setter method for property <tt>parser</tt>.
     * 
     * @param parser
     *            value to be assigned to property parser
     */
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    /**
     * Setter method for property <tt>rootDir</tt>.
     * 
     * @param rootDir value to be assigned to property rootDir
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Getter method for property <tt>groups</tt>.
     * 
     * @return property value of groups
     */
    public List<ModelGroup> getGroups() {
        return groups;
    }

}
