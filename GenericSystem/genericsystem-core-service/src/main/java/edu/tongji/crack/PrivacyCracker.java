/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.crack;

/**
 * 隐私破解器
 * 
 * @author chench
 * @version $Id: CrackPrivacy.java, v 0.1 2014-2-20 上午9:47:29 chench Exp $
 */
public interface PrivacyCracker {

    /**
     * 破解数组对象
     * 
     * @param object
     */
    public void crack(CrackObject object, int blockSize);
}
