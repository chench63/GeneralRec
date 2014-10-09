/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;

/**
 * MovieLens 数据集拆分为：Training dataset and Testing dataset
 * 
 * @author chench
 * @version $Id: MovieLensDataSetSpliter.java, v 0.1 2014-10-8 上午10:37:12 chench Exp $
 */
public class MovieLensDataSetSpliter {

    /** 数据集载入文件*/
    protected final static String FILENAME              = "E:/MovieLens/ml-1m/ratings.dat";

    /** 训练集输出文件*/
    protected final static String TRAINING_DATASET_FILE = "E:/MovieLens/ml-1m/trainingset.dat";

    /** 测试集输出文件*/
    protected final static String TESTING_DATASET_FILE  = "E:/MovieLens/ml-1m/testingset.dat";

    /** 训练数据与测试数据比*/
    protected final static float  RATIO                 = 0.9f;

    /** logger */
    private final static Logger   logger                = Logger
                                                            .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        //1. 读取文件
        LoggerUtil.info(logger, "1. Starting to load source file.");
        Queue<String> contents = new LinkedList<>(Arrays.asList(FileUtil.readLines(FILENAME)));

        //2. 拆分训练集和测试集
        LoggerUtil.info(logger, "2. Starting to spliter source file.");
        StringBuilder trainingSet = new StringBuilder();
        StringBuilder testingSet = new StringBuilder();
        int totalNum = contents.size();
        int testingNum = 0;
        while (!contents.isEmpty()) {
            String content = contents.poll();

            if (Math.random() > RATIO) {
                testingNum++;
                testingSet.append(content).append(FileUtil.BREAK_LINE);
            } else {
                trainingSet.append(content).append(FileUtil.BREAK_LINE);
            }
        }
        StringBuilder logMessgr = (new StringBuilder()).append("3. Target Ratio: ")
            .append(String.format("%.3f", RATIO)).append(" Real Ratio: ")
            .append(String.format("%.3f", 1 - testingNum * 1.0 / totalNum));
        LoggerUtil.info(logger, logMessgr.toString());

        //3. 写入文件
        LoggerUtil.info(logger, "4. Starting to persist target file.");
        FileUtil.write(TRAINING_DATASET_FILE, trainingSet.toString());
        FileUtil.write(TESTING_DATASET_FILE, testingSet.toString());
    }
}
