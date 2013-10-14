/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.processor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.context.AccRecommendationContext;
import edu.tongji.context.ProcessorContext;
import edu.tongji.context.ProcessorContextHelper;
import edu.tongji.dao.ValueOfItemsDAO;
import edu.tongji.exception.OwnedException;
import edu.tongji.function.Function;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.model.ValueOfItems;
import edu.tongji.stopper.Stopper;
import edu.tongji.stopper.TimestampStopper;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: FastAccRecommendationProcessor.java, v 0.1 2013-9-24 下午10:07:11 chenkh Exp $
 */
public class FastAccRecommendationProcessor implements Processor {

    /** 相似度计算函数*/
    private Function            similarityFunction;

    /** stopper取个结束值*/
    private Stopper             stopper;

    /** item间相似度相关的DAO */
    private ValueOfItemsDAO     valueOfItemsDAO;

    /**  相似度名 */
    private final static String SIMULARITY_FUNCTION = "ACC_CorrelationBasedSimularityFunction";

    /** logger */
    private static final Logger logger              = Logger
                                                        .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.processor.Processor#process(edu.tongji.context.ProcessorContext)
     */
    @Override
    public void process(ProcessorContext processorContext) {
        List<Rating> newCustomersOfItemI = ((AccRecommendationContext) processorContext)
            .getNewCustomersOfItemI();
        List<Rating> newCustomersOfItemJ = ((AccRecommendationContext) processorContext)
            .getNewCustomersOfItemJ();

        List<Integer> ratingsValusOfItemI = new ArrayList<Integer>();
        List<Integer> ratingsValusOfItemJ = new ArrayList<Integer>();
        ProcessorContextHelper.forgeRatingValues(newCustomersOfItemI, newCustomersOfItemJ,
            ratingsValusOfItemI, ratingsValusOfItemJ);

        //计算相似度的值
        double sim = 0.0;
        try {
            sim = similarityFunction.calculate(ratingsValusOfItemI, ratingsValusOfItemJ)
                .doubleValue();
        } catch (OwnedException e) {
            sim = ProcessorConstant.BAD_VALUE;
            ExceptionUtil.caught(e, "List为空，该Item从未被评价过");
        }

        //在配置DAO的情况下，持久化至数据库
        if (valueOfItemsDAO != null) {
            ValueOfItems record = new ValueOfItems();
            record.setItemI(((AccRecommendationContext) processorContext).getItemI());
            record.setItemJ(((AccRecommendationContext) processorContext).getItemJ());
            record.setFunctionName(SIMULARITY_FUNCTION);
            record.setGMT_CREATE(new Date(((TimestampStopper) stopper).getSeed().getTime()));
            if (Double.isNaN(sim)) {
                record.setValue(ProcessorConstant.BAD_VALUE);
            } else {
                record.setValue(sim);
            }
            valueOfItemsDAO.insert(record);
            LoggerUtil.info(logger, "版本: " + record.getGMT_CREATE() + " ItemI: " + record.getItemI()
                                    + "  ItemJ: " + record.getItemJ() + " 相似度: " + sim);
        }
    }

    /**
     * Getter method for property <tt>similarityFunction</tt>.
     * 
     * @return property value of similarityFunction
     */
    public Function getSimilarityFunction() {
        return similarityFunction;
    }

    /**
     * Setter method for property <tt>similarityFunction</tt>.
     * 
     * @param similarityFunction value to be assigned to property similarityFunction
     */
    public void setSimilarityFunction(Function similarityFunction) {
        this.similarityFunction = similarityFunction;
    }

    /**
     * Getter method for property <tt>valueOfItemsDAO</tt>.
     * 
     * @return property value of valueOfItemsDAO
     */
    public ValueOfItemsDAO getValueOfItemsDAO() {
        return valueOfItemsDAO;
    }

    /**
     * Setter method for property <tt>valueOfItemsDAO</tt>.
     * 
     * @param valueOfItemsDAO value to be assigned to property valueOfItemsDAO
     */
    public void setValueOfItemsDAO(ValueOfItemsDAO valueOfItemsDAO) {
        this.valueOfItemsDAO = valueOfItemsDAO;
    }

    /**
     * Getter method for property <tt>stopper</tt>.
     * 
     * @return property value of stopper
     */
    public Stopper getStopper() {
        return stopper;
    }

    /**
     * Setter method for property <tt>stopper</tt>.
     * 
     * @param stopper value to be assigned to property stopper
     */
    public void setStopper(Stopper stopper) {
        this.stopper = stopper;
    }

}
