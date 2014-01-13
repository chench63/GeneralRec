/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.parser;

/**
 * 数据采集，过滤类。
 * <b>
 *  防止样本规模过大，导致内存溢出。
 * </b>
 * @author chench
 * @version $Id: Filter.java, v 0.1 2013-9-6 下午7:24:11 chench Exp $
 */
public interface Filter {

    public boolean isFiler(ParserTemplate template);
}
