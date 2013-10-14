/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.processor;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.context.AccRecommendationContext;
import edu.tongji.context.ProcessorContext;
import edu.tongji.dao.ValueOfItemsDAO;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.model.ValueOfItems;
import edu.tongji.stopper.Stopper;
import edu.tongji.stopper.TimestampStopper;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chench
 * @version $Id: AccRecommendationProcessor.java, v 0.1 17 Sep 2013 21:59:44 chench Exp $
 */
public class AccRecommendationProcessor implements Processor {

    /** 相似度DAO */
    private ValueOfItemsDAO     valueOfItemsDAO;

    /** stopper取个结束值*/
    private Stopper             stopper;

    /**  相似度名 */
    private final static String SIMULARITY_FUNCTION = "ACC_CorrelationBasedSimularityFunction";

    /** logger */
    private final static Logger logger              = Logger
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
        Map<String, Integer> userTableOfItemI = ((AccRecommendationContext) processorContext)
            .getUserTableOfItemI();
        Map<String, Integer> userTableOfItemJ = ((AccRecommendationContext) processorContext)
            .getUserTableOfItemJ();

        for (Rating rating : newCustomersOfItemI) {
            int indexOfSameUser = newCustomersOfItemJ.indexOf(rating);

            if (indexOfSameUser != -1) {
                //用户在本周期内，同时看过itemI和itemJ
                fullPairInsertItem(rating, newCustomersOfItemJ.get(indexOfSameUser),
                    (AccRecommendationContext) processorContext);
                newCustomersOfItemJ.remove(indexOfSameUser);
            } else if (userTableOfItemJ.containsKey(rating.getUsrId())) {
                //用户在以前看过itemI或者itemJ，本周期看剩余一个
                semiPairInsertItem(rating, userTableOfItemJ, true,
                    (AccRecommendationContext) processorContext);
            } else {
                //用户首次看itemI或者itemJ
                singleInsertItem(rating, true, (AccRecommendationContext) processorContext);
            }
        }

        for (Rating rating : newCustomersOfItemJ) {
            if (userTableOfItemI.containsKey(rating.getUsrId())) {
                //用户在以前看过itemI或者itemJ，本周期看剩余一个
                semiPairInsertItem(rating, userTableOfItemI, false,
                    (AccRecommendationContext) processorContext);
            } else {
                //用户首次看itemI或者itemJ
                singleInsertItem(rating, false, (AccRecommendationContext) processorContext);
            }
        }

        double sim = ((AccRecommendationContext) processorContext).getAccNumeratorValue()
                     / Math.sqrt(((AccRecommendationContext) processorContext)
                         .getAccNenominatorOfItemIValue()
                                 * ((AccRecommendationContext) processorContext)
                                     .getAccNenominatorOfItemJValue());
        ValueOfItems valueOfItem = new ValueOfItems();
        valueOfItem.setItemI(((AccRecommendationContext) processorContext).getItemI());
        valueOfItem.setItemJ(((AccRecommendationContext) processorContext).getItemJ());
        valueOfItem.setValue(sim);
        valueOfItem.setFunctionName(SIMULARITY_FUNCTION);
        valueOfItem.setGMT_CREATE(new Date(((TimestampStopper) stopper).getSeed().getTime()));

        LoggerUtil
            .debug(
                logger,
                "AccNumeratorValue: "
                        + ((AccRecommendationContext) processorContext).getAccNumeratorValue()
                        + "\nAccNenominatorOfItemIValue: "
                        + ((AccRecommendationContext) processorContext)
                            .getAccNenominatorOfItemIValue()
                        + "\nAccNenominatorOfItemJValue: "
                        + ((AccRecommendationContext) processorContext)
                            .getAccNenominatorOfItemJValue());
        LoggerUtil.info(logger,
            "版本: " + valueOfItem.getGMT_CREATE() + "ItemI: " + valueOfItem.getItemI() + "  ItemJ: "
                    + valueOfItem.getItemJ() + " 相似度: " + sim);

        valueOfItemsDAO.insert(valueOfItem);
    }

    /**
     * 新增itemI与itemJ，新增累计分母与累计分子
     * 
     * @param itemI
     * @param itemJ
     * @param processorContext
     */
    private void fullPairInsertItem(Rating itemI, Rating itemJ,
                                    AccRecommendationContext processorContext) {
        //新增累计分子
        double accNumeratorValue = processorContext.getAccNumeratorValue();
        double avgOfItemI = processorContext.getVersionsTableOfItemI()
            .get(processorContext.getCurrentVersionOfItemI()).getAvgOfRating();
        double avgOfItemJ = processorContext.getVersionsTableOfItemJ()
            .get(processorContext.getCurrentVersionOfItemJ()).getAvgOfRating();
        accNumeratorValue += (itemI.getRating() - avgOfItemI) * (itemJ.getRating() - avgOfItemJ);
        processorContext.setAccNumeratorValue(accNumeratorValue);

        //新增累计分母
        double accNenominatorOfItemIValue = processorContext.getAccNenominatorOfItemIValue();
        accNenominatorOfItemIValue += Math.pow(itemI.getRating() - avgOfItemI, 2);
        processorContext.setAccNenominatorOfItemIValue(accNenominatorOfItemIValue);

        double accNenominatorOfItemJValue = processorContext.getAccNenominatorOfItemJValue();
        accNenominatorOfItemJValue += Math.pow(itemJ.getRating() - avgOfItemJ, 2);
        processorContext.setAccNenominatorOfItemJValue(accNenominatorOfItemJValue);
    }

    /**
     * 用户之前看过itemI或者itemJ前提下，然后再插入新的item
     * 
     * @param itemI
     * @param userTable
     * @param isItemI
     * @param processorContext
     */
    private void semiPairInsertItem(Rating itemI, Map<String, Integer> userTable, boolean isItemI,
                                    AccRecommendationContext processorContext) {
        //新增累计分子
        double accNumeratorValue = processorContext.getAccNumeratorValue();
        double avgOfItemI = processorContext.getVersionsTableOfItemI()
            .get(processorContext.getCurrentVersionOfItemI()).getAvgOfRating();
        double avgOfItemJ = processorContext.getVersionsTableOfItemJ()
            .get(processorContext.getCurrentVersionOfItemJ()).getAvgOfRating();
        int item = userTable.get(itemI.getUsrId());
        if (isItemI) {
            accNumeratorValue += (item - avgOfItemJ) * itemI.getRating();
        } else {
            accNumeratorValue += (item - avgOfItemI) * itemI.getRating();
        }
        processorContext.setAccNumeratorValue(accNumeratorValue);

        //新增累计分母
        double accNenominatorOfItemIValue = processorContext.getAccNenominatorOfItemIValue();
        double accNenominatorOfItemJValue = processorContext.getAccNenominatorOfItemJValue();
        if (isItemI) {
            accNenominatorOfItemIValue += Math.pow(itemI.getRating(), 2) - 2 * avgOfItemI
                                          * itemI.getRating();
            processorContext.setAccNenominatorOfItemIValue(accNenominatorOfItemIValue);
        } else {
            accNenominatorOfItemJValue += Math.pow(itemI.getRating(), 2) - 2 * avgOfItemJ
                                          * itemI.getRating();
            processorContext.setAccNenominatorOfItemJValue(accNenominatorOfItemJValue);
        }

    }

    /**
     * 用户之前没有看过itemI和itemJ，新增item
     * 
     * @param item
     * @param isItemI
     * @param processorContext
     */
    private void singleInsertItem(Rating item, boolean isItemI,
                                  AccRecommendationContext processorContext) {
        //新增累计分子
        double accNumeratorValue = processorContext.getAccNumeratorValue();
        double avgOfItemI = processorContext.getVersionsTableOfItemI()
            .get(processorContext.getCurrentVersionOfItemI()).getAvgOfRating();
        double avgOfItemJ = processorContext.getVersionsTableOfItemJ()
            .get(processorContext.getCurrentVersionOfItemJ()).getAvgOfRating();
        if (isItemI) {
            accNumeratorValue += (0.0 - avgOfItemJ) * (item.getRating() - avgOfItemI);
        } else {
            accNumeratorValue += (0.0 - avgOfItemI) * (item.getRating() - avgOfItemJ);
        }
        processorContext.setAccNumeratorValue(accNumeratorValue);

        //新增累计分母
        double accNenominatorOfItemIValue = processorContext.getAccNenominatorOfItemIValue();
        double accNenominatorOfItemJValue = processorContext.getAccNenominatorOfItemJValue();
        if (isItemI) {
            accNenominatorOfItemIValue += Math.pow(item.getRating() - avgOfItemI, 2);
            accNenominatorOfItemJValue += Math.pow(avgOfItemJ, 2);
        } else {
            accNenominatorOfItemIValue += Math.pow(avgOfItemI, 2);
            accNenominatorOfItemJValue += Math.pow(item.getRating() - avgOfItemJ, 2);
        }
        processorContext.setAccNenominatorOfItemIValue(accNenominatorOfItemIValue);
        processorContext.setAccNenominatorOfItemJValue(accNenominatorOfItemJValue);
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
