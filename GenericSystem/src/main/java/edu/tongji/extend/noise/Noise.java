/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.noise;

/**
 * 噪声
 * 
 * @author chench
 * @version $Id: Noise.java, v 0.1 2014-2-24 下午4:17:13 chench Exp $
 */
public interface Noise {

    /**
     * 生成随机数
     * 
     * @return
     */
    public double random();

    /**
     * 噪声名称
     * 
     * @return
     */
    public String getName();
}
