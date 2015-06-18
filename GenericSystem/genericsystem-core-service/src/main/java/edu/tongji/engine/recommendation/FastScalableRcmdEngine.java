package edu.tongji.engine.recommendation;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import prea.util.MatrixFileUtil;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.data.LocalModel;
import edu.tongji.data.ModelGroup;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.engine.RcmdtnEngine;
import edu.tongji.engine.recommendation.thread.ScalableSVDLearner;
import edu.tongji.ml.matrix.BorderFormConstraintSVD;
import edu.tongji.ml.matrix.MatrixFactorizationRecommender;
import edu.tongji.parser.Parser;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.SerializeUtil;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author Hanke
 * @version $Id: FastScalableRcmdEngine.java, v 0.1 2015-5-18 下午3:37:14 Exp $
 */
public class FastScalableRcmdEngine extends RcmdtnEngine {
    /** the directory contains training, test dataset and coclustering setting file*/
    private String                         rootDir;
    /** file with training data */
    private String                         trainingSetFile;
    /** file with testing data */
    private String                         testingSetFile;
    /** file with  auxiliary recommender serialized file*/
    private String                         auxRecIdentity;
    /** The number of users. */
    private int                            userCount;
    /** The number of items. */
    private int                            itemCount;

    /** The auxiliary recommender model*/
    private MatrixFactorizationRecommender auxRec;
    /** groups of model */
    private List<ModelGroup>               groups;
    /** the content parser w.r.t certain dataset */
    private Parser                         parser;

    /**
     * @see edu.tongji.engine.RcmdtnEngine#loadDataSet()
     */
    @Override
    protected void loadDataSet() {
        LoggerUtil.info(logger, "1. loading models. Groups: " + groups.size());
        // construct queue of models
        if (StringUtil.isBlank(trainingSetFile) && StringUtil.isBlank(testingSetFile)) {
            trainingSetFile = rootDir + "trainingset";
            testingSetFile = rootDir + "testingset";
        }
        ModelGroup.setRootDir(rootDir);
        joinGroup();
    }

    /**
     * join model of group to task list
     */
    protected void joinGroup() {
        LoggerUtil.info(logger, "\t\ta. loading model. ");
        Queue<LocalModel> models = new LinkedList<LocalModel>();
        for (int g = 0; g < groups.size(); g++) {
            groups.get(g).join(models, null, g);
        }
        ScalableSVDLearner.models = models;

        // suggest JVM to release memory
        if (userCount > 400 * 1000 & itemCount > 12 * 1000) {
            LoggerUtil.info(logger, "\t\tb. releasing mem. ");
            groups.clear();
            System.gc();
        }
    }

    /**
     * @see edu.tongji.engine.RcmdtnEngine#excuteInner()
     */
    @Override
    protected void excuteInner() {

    }

    /**
     * @see edu.tongji.engine.RcmdtnEngine#evaluate()
     */
    @Override
    protected void evaluate() {
        //load datasets
        LoggerUtil.info(logger, "3. loading rateMatrix and testMatrix. ");
        SparseRowMatrix rateMatrix = MatrixFileUtil.reads(trainingSetFile, userCount, itemCount,
            parser);
        MatlabFasionSparseMatrix tmMatrix = MatrixFileUtil.reads(testingSetFile, 20 * 1000 * 1000,
            parser);
        LoggerUtil
            .info(logger, "Train: " + rateMatrix.itemCount() + "\tTest: " + tmMatrix.getNnz());

        //BFSVD unique logics
        if (ScalableSVDLearner.models.element().getRecmmd() instanceof BorderFormConstraintSVD) {
            LoggerUtil.info(logger, "3+. entering BorderFormSVD unique process.");
            String auxRecFile = rootDir + "Model/" + auxRecIdentity;

            if (StringUtil.isNotBlank(auxRecIdentity) & FileUtil.exists(auxRecFile)) {
                LoggerUtil.info(logger, "\t\ta. loading auxiliary model: " + auxRecIdentity);
                auxRec = (MatrixFactorizationRecommender) SerializeUtil.readObject(auxRecFile);
            } else {
                LoggerUtil.info(logger, "\t\ta. building auxiliary model.");
                auxRec.buildModel(rateMatrix, null);
            }

            LoggerUtil.info(logger, "\t\tb. setting it to local models.");
            for (LocalModel model : ScalableSVDLearner.models) {
                BorderFormConstraintSVD lRecmmd = (BorderFormConstraintSVD) model.recmmd;
                lRecmmd.setAuxRec(auxRec);
            }
        }

        //main business logics
        LoggerUtil.info(logger, "4. starting engine. ");
        try {
            ScalableSVDLearner.cumPrediction = new SparseRowMatrix(userCount, itemCount);
            ScalableSVDLearner.cumWeight = new SparseRowMatrix(userCount, itemCount);

            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new ScalableSVDLearner(rateMatrix, tmMatrix));
            exec.execute(new ScalableSVDLearner(rateMatrix, tmMatrix));
            exec.execute(new ScalableSVDLearner(rateMatrix, tmMatrix));
            exec.execute(new ScalableSVDLearner(rateMatrix, tmMatrix));
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

    /**
     * Setter method for property <tt>auxRec</tt>.
     * 
     * @param auxRec value to be assigned to property auxRec
     */
    public void setAuxRec(MatrixFactorizationRecommender auxRec) {
        this.auxRec = auxRec;
    }

    /**
     * Setter method for property <tt>auxRecIdentity</tt>.
     * 
     * @param auxRecIdentity value to be assigned to property auxRecIdentity
     */
    public void setAuxRecIdentity(String auxRecIdentity) {
        this.auxRecIdentity = auxRecIdentity;
    }
}
