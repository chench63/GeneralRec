/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.List;

/**
 * 图像格式器。讲实验数据格式化，并在图像中展示
 * 
 * @author chench
 * @version $Id: FigureFormatter.java, v 0.1 2014-2-18 下午1:56:17 chench Exp $
 */
public interface FigureFormatter {

    /** 总小时数*/
    public final static int HOUR_RANGE    = 24;

    /** 总时刻数*/
    public final static int QUARTER_RANGE = 4;

    /**
     * 判断是否需要行号
     * 
     * @return
     */
    public boolean needRowNum();

    /**
     * 格式化数据，符合图像格式
     * 
     * @param context
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List<String> format(List context, int rowSize);

    /**
     * 格式化数据，符合图像格式
     * 
     * @param context
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String[][] formatToArrs(List context, int rowSize);

}
