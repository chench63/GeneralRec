/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.gnuplot;

import java.util.List;

import edu.tongji.extend.crack.support.HashKeyCallBack;

/**
 * 图像格式器。讲实验数据格式化，并在图像中展示
 * 
 * @author Hanke Chen
 * @version $Id: FigureFormatter.java, v 0.1 2014-2-18 下午1:56:17 chench Exp $
 */
public interface FigureFormatter {

    /**
     * 格式化数据，符合图像格式
     * 
     * @param context
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String[][] formatToArrs(List context, int rowSize, HashKeyCallBack hashKyGen);

}
