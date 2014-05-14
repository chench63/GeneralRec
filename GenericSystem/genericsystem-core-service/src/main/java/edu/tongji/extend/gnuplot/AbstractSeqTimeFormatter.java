/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;

/**
 * 
 * @author chench
 * @version $Id: AbstractSeqTimeFormatter.java, v 0.1 15 Apr 2014 09:24:07 chench Exp $
 */
public abstract class AbstractSeqTimeFormatter implements FigureFormatter {

    /** 总小时数*/
    protected final static int    HOUR_RANGE    = 24;

    /** 总时刻数*/
    protected final static int    QUARTER_RANGE = 4;

    /** 是否输出均值*/
    protected boolean             mean          = true;

    /** logger */
    protected final static Logger logger        = Logger
                                                    .getLogger(LoggerDefineConstant.SERVICE_CORE);

    /**
     * Getter method for property <tt>mean</tt>.
     * 
     * @return property value of mean
     */
    public boolean isMean() {
        return mean;
    }

    /**
     * Setter method for property <tt>mean</tt>.
     * 
     * @param mean value to be assigned to property mean
     */
    public void setMean(boolean mean) {
        this.mean = mean;
    }

}
