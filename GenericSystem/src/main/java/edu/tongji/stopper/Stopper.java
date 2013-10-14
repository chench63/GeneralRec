/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.stopper;

/**
 * 
 * @author chenkh
 * @version $Id: Stopper.java, v 0.1 2013-9-18 上午11:19:58 chenkh Exp $
 */
public interface Stopper {

    /**
     * 判断是否停止
     * 
     * @return
     */
    public boolean isStop();

    /**
     * 生产种子，特定条件返回Null，则表示停止
     * 
     * @return
     */
    public Object genSeed();

    /**
     * 重置Stopper
     */
    public void reset();

}
