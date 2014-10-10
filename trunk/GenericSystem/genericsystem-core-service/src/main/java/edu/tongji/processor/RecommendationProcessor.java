/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.processor;

import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.context.ProcessorContext;
import edu.tongji.context.RecommendationContext;
import edu.tongji.dao.ValueOfItemsDAO;
import edu.tongji.exception.OwnedException;
import edu.tongji.function.Function;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.ValueOfItems;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 推荐系统对应的业务逻辑处理模板
 * 
 * @author Hanke Chen
 * @version $Id: RecommendationProcessor.java, v 0.1 2013-9-7 下午8:23:24 chench Exp $
 */
public class RecommendationProcessor implements Processor {

    /** 相似度计算函数*/
    private Function            similarityFunction;

    /** item间相似度相关的DAO */
    private ValueOfItemsDAO     valueOfItemsDAO;

    private static final Logger logger    = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**  Proccessor上下文是否改变，优化内存模型 */
    private boolean             isChanged = true;

    /** 
     * @see edu.tongji.processor.Processor#process(edu.tongji.context.ProcessorContext)
     */
    @Override
    public void process(ProcessorContext processorContext) {
        List<Number> ratingsValusOfItemI = ((RecommendationContext) processorContext)
            .getRatingsValusOfItemI();
        List<Number> ratingsValusOfItemJ = ((RecommendationContext) processorContext)
            .getRatingsValusOfItemJ();

        //计算相似度的值
        double sim = 0.0;
        try {
            sim = similarityFunction.calculate(ratingsValusOfItemI, ratingsValusOfItemJ)
                .doubleValue();
            LoggerUtil.info(logger, "所得相似度：" + sim);
        } catch (OwnedException e) {
            sim = ProcessorConstant.BAD_VALUE;
            ExceptionUtil.caught(e, "List为空，该Item从未被评价过");
        }

        //在配置DAO的情况下，持久化至数据库
        if (valueOfItemsDAO != null) {
            ValueOfItems record = new ValueOfItems();
            record.setItemI(((RecommendationContext) processorContext).getItemI());
            record.setItemJ(((RecommendationContext) processorContext).getItemJ());
            record.setFunctionName(similarityFunction.toString());
            if (Double.isNaN(sim)) {
                record.setValue(ProcessorConstant.BAD_VALUE);
            } else {
                record.setValue(sim);
            }
            valueOfItemsDAO.insert(record);
        }
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
     * Setter method for property <tt>valueOfItemsDAO</tt>.
     * 
     * @param valueOfItemsDAO value to be assigned to property valueOfItemsDAO
     */
    public void setValueOfItemsDAO(ValueOfItemsDAO valueOfItemsDAO) {
        this.valueOfItemsDAO = valueOfItemsDAO;
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
     * Getter method for property <tt>isChanged</tt>.
     * 
     * @return property value of isChanged
     */
    public boolean isChanged() {
        return isChanged;
    }

    /**
     * Setter method for property <tt>isChanged</tt>.
     * 
     * @param isChanged value to be assigned to property isChanged
     */
    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

}
