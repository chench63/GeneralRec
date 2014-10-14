/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import org.apache.log4j.Logger;

import edu.tongji.engine.recommendation.BlockSnglrValuDecmpsRcmdEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.matrix.ComplicatedMatrix;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author Hanke Chen
 * @version $Id: BlockMatrixDataSetLoader.java, v 0.1 2014-10-12 下午8:03:20 chench Exp $
 */
public class BlockMatrixDataSetLoader extends Thread {

    /** The parser to parse the dataset file  **/
    private Parser                parser;

    /** setting file*/
    private String                settingFile;

    /** training dataset file*/
    private String                trainingSetFile;

    /** testing dataset file*/
    private String                testingSetFile;

    /** The number of users. */
    private int                   userCount;

    /** The number of items. */
    private int                   itemCount;

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        //0. store setting file
        String[] contents = FileUtil.readLines(settingFile);
        String[] elements = contents[0].split("\\,");
        int[] boundRow = new int[elements.length];
        for (int i = 0; i < boundRow.length; i++) {
            boundRow[i] = Integer.valueOf(elements[i]);
        }
        elements = contents[1].split("\\,");
        int[] boundCol = new int[elements.length];
        for (int i = 0; i < boundCol.length; i++) {
            boundCol[i] = Integer.valueOf(elements[i]);
        }

        //1. store training dataset into memory
        BlockSnglrValuDecmpsRcmdEngine.rateBlockes = new ComplicatedMatrix(boundRow, boundCol);
        contents = FileUtil.readLines(trainingSetFile);
        for (String content : contents) {
            RatingVO rating = (RatingVO) parser.parse(content);
            if (rating != null) {
                BlockSnglrValuDecmpsRcmdEngine.rateBlockes.setValue(rating.getUsrId(),
                    rating.getMovieId(), rating.getRatingReal());
            }
        }

        //2. store testing dataset into memory
        BlockSnglrValuDecmpsRcmdEngine.testMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        contents = FileUtil.readLines(testingSetFile);
        for (String content : contents) {
            RatingVO rating = (RatingVO) parser.parse(content);
            if (rating != null) {
                BlockSnglrValuDecmpsRcmdEngine.testMatrix.setValue(rating.getUsrId(),
                    rating.getMovieId(), rating.getRatingReal());
            }
        }

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
     * Setter method for property <tt>parser</tt>.
     * 
     * @param parser value to be assigned to property parser
     */
    public void setParser(Parser parser) {
        this.parser = parser;
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
}
