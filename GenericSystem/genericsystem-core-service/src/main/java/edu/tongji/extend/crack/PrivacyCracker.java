/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.extend.crack;

import edu.tongji.extend.crack.support.HashKeyCallBack;
import edu.tongji.noise.Noise;

/**
 * 隐私破解器
 * 
 * @author chench
 * @version $Id: CrackPrivacy.java, v 0.1 2014-2-20 上午9:47:29 chench Exp $
 */
public interface PrivacyCracker {

    /**
     * 破解对象，噪声有外部生成
     * 
     * @param object
     */
    public void crack(CrackObject object, int blockSize, Noise noise, HashKeyCallBack hashKyGen);

    /**
     * 破解对象,噪声有内部生成
     * 
     * @param object    破解对象
     * @param noise     需要的噪声
     * @param hashKyGen 哈希函数
     */
    public void crackInnerNoise(CrackObject object, Noise noise, HashKeyCallBack hashKyGen);
}
