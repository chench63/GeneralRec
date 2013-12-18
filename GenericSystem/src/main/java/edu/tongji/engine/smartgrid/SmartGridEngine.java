/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import org.apache.log4j.Logger;

import edu.tongji.engine.Engine;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.orm.SmartGridDataSource;

/**
 * 
 * @author chenkh
 * @version $Id: SmartGridEngine.java, v 0.1 2013-12-18 上午11:05:23 chenkh Exp $
 */
public abstract class SmartGridEngine implements Engine {

    /** 数据源*/
    protected SmartGridDataSource dataSource;

    /** logger */
    protected final static Logger logger   = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 测试需要，统计平均运行时间*/
    public static long            runtimes = 0L;

    /**
     * Getter method for property <tt>dataSource</tt>.
     * 
     * @return property value of dataSource
     */
    public SmartGridDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Setter method for property <tt>dataSource</tt>.
     * 
     * @param dataSource value to be assigned to property dataSource
     */
    public void setDataSource(SmartGridDataSource dataSource) {
        this.dataSource = dataSource;
    }

}
