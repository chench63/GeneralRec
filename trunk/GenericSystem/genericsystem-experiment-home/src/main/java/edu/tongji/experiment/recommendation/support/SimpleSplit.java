/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.log4j.Logger;

import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * Split dataset into Training dataset and Testing dataset
 * 
 * @author Hanke Chen
 * @version $Id: MovieLensDataSetSpliter.java, v 0.1 2014-10-8 上午10:37:12 chench
 *          Exp $
 */
public class SimpleSplit {

    /** source dataset file input path */
    protected final static String FILENAME              = "C:/Netflix/ratings.dat";

    /** training dataset file output path */
    protected final static String TRAINING_DATASET_FILE = "C:/Netflix/1/trainingset";

    /** testing dataset file output path */
    protected final static String TESTING_DATASET_FILE  = "C:/Netflix/1/testingset";

    /** the number of rows */
    public final static int       rowCount              = 480189;

    /** the number of columns */
    public final static int       colCount              = 17770;

    /** training data/ total data */
    protected final static float  RATIO                 = 0.9f;

    /** the content parser w.r.t certain dataset */
    public static Parser          parser                = new MovielensRatingTemplateParser();

    /** logger */
    private final static Logger   logger                = Logger
                                                            .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        // 1. read data
        LoggerUtil.info(logger, "1. Starting to load source file.");
        Queue<String> contents = new LinkedList<>(Arrays.asList(FileUtil.readLines(FILENAME)));

        // 2. split testset and trainset
        LoggerUtil.info(logger, "2. Starting to spliter source file. N：" + contents.size());
        SparseMatrix rateMatrix = new SparseMatrix(rowCount, colCount);
        SparseMatrix testMatrix = new SparseMatrix(rowCount, colCount);

        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        while (!contents.isEmpty()) {
            String content = contents.poll();
            RatingVO rating = (RatingVO) parser.parse(content);

            if (uniform.sample() >= RATIO) {
                testMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            } else {
                rateMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            }
        }

        // 3. ensure every user has one rating, and every item is rated
        LoggerUtil.info(logger, "3. ensure every user has one rating, and every item is rated.");
        for (int u = 0; u < rowCount; u++) {
            SparseVector Ru = rateMatrix.getRowRef(u);
            if (Ru.indexList() != null) {
                continue;
            }

            SparseVector Tu = testMatrix.getRowRef(u);
            int[] indexList = Tu.indexList();
            int index = 0;
            rateMatrix.setValue(u, indexList[index], Tu.getValue(indexList[index]));
            testMatrix.setValue(u, indexList[index], 0.0);
        }

        for (int v = 0; v < colCount; v++) {
            SparseVector Rv = rateMatrix.getColRef(v);
            if (Rv.indexList() != null) {
                continue;
            }

            SparseVector Tv = testMatrix.getColRef(v);
            int[] indexList = Tv.indexList();
            int index = 0;

            rateMatrix.setValue(indexList[index], v, Tv.getValue(indexList[index]));
            testMatrix.setValue(indexList[index], v, 0.0);
        }

        // 4. write to disk
        LoggerUtil.info(logger, "4. write to disk. Train: " + rateMatrix.itemCount() + "\tTest: "
                                + testMatrix.itemCount());
        MatrixFileUtil.write(TRAINING_DATASET_FILE, rateMatrix);
        MatrixFileUtil.write(TESTING_DATASET_FILE, testMatrix);

    }
}
