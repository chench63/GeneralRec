/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import org.apache.log4j.Logger;

import edu.tongji.data.BlockMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * Block Low-Rank Approximation
 * 
 * @author Hanke Chen
 * @version $Id: BlockLowRankApproximationRcmdLoader.java, v 0.1 2014-10-15 上午11:26:32 chench Exp $
 */
public class BlockLowRankApproximationRcmdLoader extends Thread {

    /**  matrix with training data */
    private BlockMatrix         rateMatrixes;

    /**  matrix with testing data */
    private BlockMatrix         testMatrixes;

    /** matrix with testing data*/
    private SparseMatrix        testMatrix;

    /** file with setting data*/
    private String              settingFile;

    /** file with training data*/
    private String              trainingSetFile;

    /** file with testing data*/
    private String              testingSetFile;

    /** the content parser w.r.t certain dataset*/
    private Parser              parser;

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        //read setting file
        String[] lines = FileUtil.readLines(settingFile);
        String[] elemnts = lines[0].split("\\,");
        int[] rowBound = new int[elemnts.length];
        int indx = 0;
        for (String elemnt : elemnts) {
            rowBound[indx] = Integer.valueOf(elemnt);
            indx++;
        }
        elemnts = lines[1].split("\\,");
        int[] colBound = new int[elemnts.length];
        indx = 0;
        for (String elemnt : elemnts) {
            colBound[indx] = Integer.valueOf(elemnt);
            indx++;
        }

        //reading training file
        rateMatrixes.initialize(rowBound, colBound);
        lines = FileUtil.readLines(trainingSetFile);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            rateMatrixes.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
        }
        LoggerUtil.info(logger, "==========================================");
        int[] bound = rateMatrixes.bound();
        for (int i = 0; i < bound[0]; i++) {
            for (int j = 0; j < bound[1]; j++) {
                LoggerUtil.info(logger,
                    "Matrix[" + i + ", " + j + "] : " + rateMatrixes.getSparsity(i, j));
            }
        }
        LoggerUtil.info(logger, "Block Matrix : " + rateMatrixes.getSparsity());

        //reading testing file
        testMatrixes.initialize(rowBound, colBound);
        lines = FileUtil.readLines(testingSetFile);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            testMatrixes.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            testMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
        }
    }

    /**
     * Setter method for property <tt>rateMatrixes</tt>.
     * 
     * @param rateMatrixes value to be assigned to property rateMatrixes
     */
    public void setRateMatrixes(BlockMatrix rateMatrixes) {
        this.rateMatrixes = rateMatrixes;
    }

    /**
     * Setter method for property <tt>testMatrixes</tt>.
     * 
     * @param testMatrixes value to be assigned to property testMatrixes
     */
    public void setTestMatrixes(BlockMatrix testMatrixes) {
        this.testMatrixes = testMatrixes;
    }

    /**
     * Setter method for property <tt>testMatrix</tt>.
     * 
     * @param testMatrix value to be assigned to property testMatrix
     */
    public void setTestMatrix(SparseMatrix testMatrix) {
        this.testMatrix = testMatrix;
    }

    /**
     * Setter method for property <tt>settingFile</tt>.
     * 
     * @param settingFile value to be assigned to property settingFile
     */
    public void setSettingFile(String settingFile) {
        this.settingFile = settingFile;
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
     * Setter method for property <tt>parser</tt>.
     * 
     * @param parser value to be assigned to property parser
     */
    public void setParser(Parser parser) {
        this.parser = parser;
    }

}
