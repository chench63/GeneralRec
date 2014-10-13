/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import org.apache.log4j.Logger;

import edu.tongji.engine.recommendation.SnglrValuDecmpsRcmdEngine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.model.Rating;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.vo.RatingVO;

/**
 * 文件中读取数据
 * 
 * @author Hanke Chen
 * @version $Id: MovieLensMatrixReader.java, v 0.1 2014-10-7 下午7:38:27 chench Exp $
 */
public final class SparseMatrixDatasetLoader extends Thread {

    /** The parser to parse the dataset file  **/
    private Parser                parser;

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
        //1. store training dataset into memory
        SnglrValuDecmpsRcmdEngine.rateMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        String[] contents = FileUtil.readLines(trainingSetFile);
        for (String content : contents) {
            RatingVO rating = (RatingVO) parser.parse(content);
            if (rating != null) {
                SnglrValuDecmpsRcmdEngine.rateMatrix.setValue(rating.getUsrId(),
                    rating.getMovieId(), rating.getRatingReal());
            }
        }

        //2. store testing dataset into memory
        SnglrValuDecmpsRcmdEngine.testMatrix = new SparseMatrix(userCount + 1, itemCount + 1);
        contents = FileUtil.readLines(testingSetFile);
        for (String content : contents) {
            Rating rating = (Rating) parser.parse(content);
            if (rating != null) {
                SnglrValuDecmpsRcmdEngine.testMatrix.setValue(rating.getUsrId(),
                    rating.getMovieId(), rating.getRating());
            }
        }

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
