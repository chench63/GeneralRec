package edu.tongji.engine.recommendation;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import prea.util.MatrixFileUtil;
import edu.tongji.data.Cocluster;
import edu.tongji.data.Model;
import edu.tongji.data.SparseMatrix;
import edu.tongji.engine.recommendation.thread.WeightedSVDLearner;
import edu.tongji.parser.Parser;

/**
 * 
 * @author Hanke
 * @version $Id: MixtureWLRARcmdEngine.java, v 0.1 2014-11-2 下午2:37:12 Exp $
 */
public class MixtureWLRARcmdEngine extends RcmdtnEngine {

    /** file with training data*/
    private String          trainingSetFile;

    /** file with testing data*/
    private String          testingSetFile;

    private int             userCount;

    private int             itemCount;

    private List<Cocluster> clusters;

    /** the content parser w.r.t certain dataset*/
    private Parser          parser;

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#loadDataSet()
     */
    @Override
    protected void loadDataSet() {
        WeightedSVDLearner.rateMatrix = MatrixFileUtil.read(trainingSetFile, userCount, itemCount,
            parser);
        WeightedSVDLearner.testMatrix = MatrixFileUtil.read(testingSetFile, userCount, itemCount,
            parser);
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
        Queue<Model> models = new LinkedList<Model>();
        for (Cocluster cluster : clusters) {
            models.addAll(cluster.assembleModels());
        }
        clusters.clear();
        WeightedSVDLearner.models = models;
        WeightedSVDLearner.cumPrediction = new SparseMatrix(userCount, itemCount);
        WeightedSVDLearner.cumWeight = new SparseMatrix(userCount, itemCount);

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
     * Setter method for property <tt>clusters</tt>.
     * 
     * @param clusters value to be assigned to property clusters
     */
    public void setClusters(List<Cocluster> clusters) {
        this.clusters = clusters;
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
