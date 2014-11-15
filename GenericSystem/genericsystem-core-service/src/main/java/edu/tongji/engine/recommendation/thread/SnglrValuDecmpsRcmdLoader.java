/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation.thread;

import org.apache.log4j.Logger;

import prea.util.MatrixInformationUtil;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * read data in the memory
 * 
 * @author Hanke Chen
 * @version $Id: SnglrValuDecmpsRcmdLoader.java, v 0.1 2014-10-14 下午3:31:24 chench Exp $
 */
public class SnglrValuDecmpsRcmdLoader extends Thread {

    /** matrix with training data*/
    private SparseRowMatrix       rateMatrix;

    /** matrix with testing data*/
    private SparseRowMatrix       testMatrix;

    /** file with training data*/
    private String                trainingSetFile;

    /** file with testing data*/
    private String                testingSetFile;

    /** the content parser w.r.t certain dataset*/
    private Parser                parser;

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        //reading training data
        String[] lines = FileUtil.readLines(trainingSetFile);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            rateMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            //            rateMatrix.setValue(rating.getUsrId() - 1, rating.getMovieId() - 1,
            //                rating.getRatingReal());
        }
        LoggerUtil.info(logger, MatrixInformationUtil.hierarchy(rateMatrix));

        //reading testing data
        lines = FileUtil.readLines(testingSetFile);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            testMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            //            testMatrix.setValue(rating.getUsrId() - 1, rating.getMovieId() - 1,
            //                rating.getRatingReal());
        }

    }

    /**
     * Setter method for property <tt>rateMatrix</tt>.
     * 
     * @param rateMatrix value to be assigned to property rateMatrix
     */
    public void setRateMatrix(SparseRowMatrix rateMatrix) {
        this.rateMatrix = rateMatrix;
    }

    /**
     * Setter method for property <tt>testingMatrix</tt>.
     * 
     * @param testingMatrix value to be assigned to property testingMatrix
     */
    public void setTestMatrix(SparseRowMatrix testMatrix) {
        this.testMatrix = testMatrix;
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
     * Setter method for property <tt>parse</tt>.
     * 
     * @param parse value to be assigned to property parse
     */
    public void setParser(Parser parser) {
        this.parser = parser;
    }

}
