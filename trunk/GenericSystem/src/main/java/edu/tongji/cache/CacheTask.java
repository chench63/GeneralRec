/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import edu.tongji.configure.ConfigurationConstant;

/**
 * 
 * @author chench
 * @version $Id: DataStreamTask.java, v 0.1 2013-10-8 下午1:21:11 chench Exp $
 */
public class CacheTask {

    /** 主任务参数i, 分配任务式使用*/
    public static int I = ConfigurationConstant.I;

    /** 协任务参数j, 分配任务式使用*/
    public static int J = ConfigurationConstant.J;

    /** 当前任务i */
    public int        i;

    /** 当前任务j */
    public int        jStart;

    /** 当前任务j */
    public int        jEnd;

    /**
     * 任务区间 [jStart, jEnd)
     * 
     * @param i
     * @param jStart
     * @param jEnd
     */
    public CacheTask(int i, int jStart, int jEnd) {
        this.i = i;
        this.jStart = jStart;
        this.jEnd = jEnd;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (new StringBuilder("DataStreamTask: ")).append("i: ").append(this.i)
            .append(" jStart: ").append(this.jStart).append(" jEnd: ").append(this.jEnd).toString();
    }

}