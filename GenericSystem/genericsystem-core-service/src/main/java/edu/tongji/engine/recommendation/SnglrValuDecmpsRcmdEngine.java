/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.matrix.ComplicatedMatrix;
import edu.tongji.model.Rating;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: SnglrValuDecmpsRcmdEngine.java, v 0.1 2014-10-8 上午9:03:36 chench Exp $
 */
public class SnglrValuDecmpsRcmdEngine extends RcmdtnEngine {

    /** SVD分解线程*/
    protected List<Runnable>           svdDecmposr;

    /** 优化行矩阵线程*/
    protected Runnable                 rowPrmutatnOptmzr;

    /** 优化列矩阵线程*/
    protected Runnable                 colPrmutatnOptmzr;

    /** Permutation matrix w.r.t row*/
    public static int[]                pr;

    /** Permutation matrix w.r.t column*/
    public static int[]                pc;

    /** 目标子矩阵群*/
    public static ComplicatedMatrix    matricesEstimt;

    /** 测试集*/
    public static List<Rating>         testingSet;

    /** 用户平均rating数据 : UserId, mean rating*/
    public static Map<Integer, Double> avgUsrRating = new HashMap<Integer, Double>();

    /** 当前迭代次数*/
    private int                        iterator     = 1;

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#assembleDataSet()
     */
    @Override
    protected void assembleDataSet() {

        LoggerUtil.info(logger, "2. loading testing data set.");

        //初始化目标矩阵
        matricesEstimt = new ComplicatedMatrix(ConfigurationConstant.ROW,
            ConfigurationConstant.COLUMN);
        pr = new int[ConfigurationConstant.ROW];
        pc = new int[ConfigurationConstant.COLUMN];

        //载入测试集
        testingSet = new ArrayList<Rating>();
        String[] contents = FileUtil.readLines(this.testingDataSet);
        for (String content : contents) {
            ParserTemplate template = new ParserTemplate(content);
            //TODO: 动态配置
            Rating rating = (Rating) TemplateType.MOVIELENS_RATING_TEMPLATE.parser(template);
            if (rating == null) {
                //异常处理
                continue;
            }
            testingSet.add(rating);
        }
    }

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#excuteInner()
     */
    @Override
    protected void excuteInner() {

        LoggerUtil.info(logger, "3. performing bussiness logics.");

        //优化目标矩阵和排列矩阵
        boolean shouldStop = false;
        while (!shouldStop) {

            //1)Maximize Loss Function by Optimizing U^(i,j),V^(i,j)
            //  按配置划分矩阵，进行SVD    
            blockSVDInner();

            //2)Maximize Loss Function by Optimizing P^(r),P^(c)
            //  Deepest Gradient Descent
            shouldStop = deepestGradientDescentInner();

            //3)Evaluate Current Estimation 
            evaluate();
        }

    }

    /**
     * SVD，求解最优rank k的Matrix Decomposition
     */
    protected void blockSVDInner() {
        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            for (Runnable runnable : svdDecmposr) {
                exec.execute(runnable);
            }
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService in blockSVDInner() await crush! ");
        }
    }

    /**
     * 梯度下降，优化 Permutation Matrix
     * 
     * @return
     */
    protected boolean deepestGradientDescentInner() {

        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            if (rowPrmutatnOptmzr != null) {
                exec.execute(rowPrmutatnOptmzr);
            } else if (colPrmutatnOptmzr != null) {
                exec.execute(colPrmutatnOptmzr);
            }
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e,
                "ExecutorService in deepestGradientDescentInner() await crush! ");
        }

        return true;
    }

    /**
     * 评价实验结果
     */
    protected void evaluate() {

        //计算RMSE
        double rmse = 0.0d;
        double mae = 0.0d;
        for (Rating rating : testingSet) {
            int row = rating.getUsrId() - 1;
            int col = rating.getMovieId() - 1;

            Double mean = ((mean = avgUsrRating.get(rating.getUsrId())) == null) ? 3.0d : mean;
            double predict = ((predict = matricesEstimt.get(row, col) + mean.doubleValue()) > 0) ? predict
                : 1;
            predict = predict > 5.0 ? 5.0 : predict;

            double orignl = rating.getRating();
            LoggerUtil.debug(loggerCore,
                "O: " + String.format("%.2f", orignl) + " P: " + String.format("%.2f", predict));

            rmse += Math.pow(predict - orignl, 2.0d);
            mae += Math.abs(predict - orignl);
        }
        rmse /= testingSet.size();
        mae /= testingSet.size();

        //输出日志
        StringBuilder log = (new StringBuilder()).append("Iterator: ")
            .append(StringUtil.alignRight(String.valueOf(this.iterator++), 4)).append("  RMSE: ")
            .append(StringUtil.alignRight(String.format("%.5f", Math.sqrt(rmse)), 10))
            .append("  MAE: ").append(StringUtil.alignRight(String.format("%.5f", mae), 10))
            .append(" K: ").append(ConfigurationConstant.PARAM_K);
        LoggerUtil.info(logger, log);
    }

    /**
     * Setter method for property <tt>svdDecmposr</tt>.
     * 
     * @param svdDecmposr value to be assigned to property svdDecmposr
     */
    public void setSvdDecmposr(List<Runnable> svdDecmposr) {
        this.svdDecmposr = svdDecmposr;
    }

    /**
     * Setter method for property <tt>rowPrmutatnOptmzr</tt>.
     * 
     * @param rowPrmutatnOptmzr value to be assigned to property rowPrmutatnOptmzr
     */
    public void setRowPrmutatnOptmzr(Runnable rowPrmutatnOptmzr) {
        this.rowPrmutatnOptmzr = rowPrmutatnOptmzr;
    }

    /**
     * Setter method for property <tt>colPrmutatnOptmzr</tt>.
     * 
     * @param colPrmutatnOptmzr value to be assigned to property colPrmutatnOptmzr
     */
    public void setColPrmutatnOptmzr(Runnable colPrmutatnOptmzr) {
        this.colPrmutatnOptmzr = colPrmutatnOptmzr;
    }

}
