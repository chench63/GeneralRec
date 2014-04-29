/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.statistics;

import java.util.List;

/**
 * 统计器
 * 
 * @author chench
 * @version $Id: Statistician.java, v 0.1 2014-2-22 下午12:25:18 chench Exp $
 */
public interface Statistician {

    /**
     * 执行统计工作
     * 
     * @param l1
     * @param l2
     * @return
     */
    public Number calculate(List<Statisticable> l1, List<Statisticable> l2);
}
