/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import prea.util.MatrixInformationUtil;
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

    /** file with row mapping data*/
    private String              rowMappingFile;

    /** file with column mapping data*/
    private String              colMappingFile;

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
        int[] rowBound = new int[lines.length];
        int[][] coclusterStructure = new int[lines.length][0];
        for (int i = 0; i < lines.length; i++) {
            String[] rc = lines[i].split("\\:");
            rowBound[i] = Integer.valueOf(rc[0].trim());

            String[] cs = rc[1].split("\\,");
            int[] rowStructure = new int[cs.length];
            for (int j = 0; j < cs.length; j++) {
                rowStructure[j] = Integer.valueOf(cs[j].trim());
            }
            coclusterStructure[i] = rowStructure;
        }

        //read row mapping file
        lines = FileUtil.readLines(rowMappingFile);
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        for (String line : lines) {
            String[] elmnts = line.split("\\:");
            int key = Integer.valueOf(elmnts[0].trim());
            int val = Integer.valueOf(elmnts[1].trim());
            rowAssig.put(key, val);
        }

        //read col mapping file
        lines = FileUtil.readLines(colMappingFile);
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        for (String line : lines) {
            String[] elmnts = line.split("\\:");
            int key = Integer.valueOf(elmnts[0].trim());
            int val = Integer.valueOf(elmnts[1].trim());
            colAssig.put(key, val);
        }

        //reading training file
        rateMatrixes.initialize(rowBound, coclusterStructure);
        lines = FileUtil.readLines(trainingSetFile);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            int row = rowAssig.get(rating.getUsrId());
            int col = colAssig.get(rating.getMovieId());

            rateMatrixes.setValue(row, col, rating.getRatingReal());
        }
        LoggerUtil.info(logger, MatrixInformationUtil.sparsity(rateMatrixes));

        //reading testing file
        testMatrixes.initialize(rowBound, coclusterStructure);
        lines = FileUtil.readLines(testingSetFile);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            int row = rowAssig.get(rating.getUsrId());
            int col = colAssig.get(rating.getMovieId());

            testMatrixes.setValue(row, col, rating.getRatingReal());
            testMatrix.setValue(row, col, rating.getRatingReal());
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
     * Setter method for property <tt>rowMappingFile</tt>.
     * 
     * @param rowMappingFile value to be assigned to property rowMappingFile
     */
    public void setRowMappingFile(String rowMappingFile) {
        this.rowMappingFile = rowMappingFile;
    }

    /**
     * Setter method for property <tt>colMappingFile</tt>.
     * 
     * @param colMappingFile value to be assigned to property colMappingFile
     */
    public void setColMappingFile(String colMappingFile) {
        this.colMappingFile = colMappingFile;
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
