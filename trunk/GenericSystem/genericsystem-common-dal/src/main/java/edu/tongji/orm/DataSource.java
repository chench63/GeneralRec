/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.orm;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据源
 * 
 * @author Hanke Chen
 * @version $Id: DataSource.java, v 0.1 2013-9-6 下午3:54:59 chench Exp $
 */
public interface DataSource {

    /**
     * 是否启用懒加载
     * 
     * @return
     */
    public boolean isLazy();

    /**
     * 重新加载数据
     */
    public void reload();

    /**
     * 执行数据集【增】【删】【改】【查】操作
     * 
     * @param expression
     * @return
     */
    public List<? extends Serializable> excute(String expression);

    /**
     * 执行数据集【增】【删】【改】【查】操作
     * 
     * @param expression
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Map excuteEx(String expression);

}
