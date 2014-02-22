/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

import org.apache.log4j.Logger;
import edu.tongji.extend.statistics.StatisticianFactory;
import edu.tongji.log4j.LoggerDefineConstant;

/**
 * 期望累破解器抽象类
 * 
 * @author chench
 * @version $Id: ExpectationCracker.java, v 0.1 2014-2-22 下午12:39:58 chench Exp $
 */
public abstract class ExpectationCracker implements PrivacyCracker {

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

    /** 统计器*/
    protected StatisticianFactory statisticianFactory;

    /**
     * Getter method for property <tt>statisticianFactory</tt>.
     * 
     * @return property value of statisticianFactory
     */
    public StatisticianFactory getStatisticianFactory() {
        return statisticianFactory;
    }

    /**
     * Setter method for property <tt>statisticianFactory</tt>.
     * 
     * @param statisticianFactory value to be assigned to property statisticianFactory
     */
    public void setStatisticianFactory(StatisticianFactory statisticianFactory) {
        this.statisticianFactory = statisticianFactory;
    }

}
