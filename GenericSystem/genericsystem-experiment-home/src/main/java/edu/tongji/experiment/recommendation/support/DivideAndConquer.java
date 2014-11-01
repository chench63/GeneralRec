/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import prea.util.MatrixFileUtil;
import prea.util.MatrixInformationUtil;
import edu.tongji.data.BlockMatrix;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author Hanke Chen
 * @version $Id: BlockMatrixDivideAndConquer.java, v 0.1 2014-10-28 下午10:15:55 chench Exp $
 */
public class DivideAndConquer {

    /** file with setting data*/
    public static String        settingFile     = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/SETTING";

    /** file with row mapping data*/
    public static String        rowMappingFile  = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/RM";

    /** file with column mapping data*/
    public static String        colMappingFile  = "E:/MovieLens/ml-10M100K/3/KMeans/LeastSquare/CM";

    /** file with training data*/
    public static String        trainingSetFile = "E:/MovieLens/ml-10M100K/3/trainingset";

    /** file with testing data*/
    public static String        testingSetFile  = "E:/MovieLens/ml-10M100K/3/testingset";

//    /** file with setting data*/
//    public static String        settingFile     = "E:/MovieLens/ml-10M100K/Cocluster/IW/3/SETTING";
//
//    /** file with row mapping data*/
//    public static String        rowMappingFile  = "E:/MovieLens/ml-10M100K/Cocluster/IW/3/RM";
//
//    /** file with column mapping data*/
//    public static String        colMappingFile  = "E:/MovieLens/ml-10M100K/Cocluster/IW/3/CM";
//
//    /** file with training data*/
//    public static String        trainingSetFile = "E:/MovieLens/ml-10M100K/Cocluster/IW/3/trainingset";
//
//    /** file with testing data*/
//    public static String        testingSetFile  = "E:/MovieLens/ml-10M100K/Cocluster/IW/3/testingset";

    /** the content parser w.r.t certain dataset*/
    public static Parser        parser          = new MovielensRatingTemplateParser();                  ;

    /** logger */
    private final static Logger logger          = Logger
                                                    .getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        BlockMatrix rateMatrixes = new BlockMatrix();
        BlockMatrix testMatrixes = new BlockMatrix();

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
        }

        //write to disk
        MatrixFileUtil.write(trainingSetFile, rateMatrixes);
        MatrixFileUtil.write(testingSetFile, testMatrixes);
    }

}
