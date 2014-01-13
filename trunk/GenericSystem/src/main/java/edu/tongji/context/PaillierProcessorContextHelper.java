/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.math.BigInteger;
import java.util.List;

import edu.tongji.function.FunctionHelper;
import edu.tongji.util.PaillierUtil;

/**
 * 
 * @author chench
 * @version $Id: PaillierProcessorContextHelper.java, v 0.1 2013-10-18 下午1:45:38 chench Exp $
 */
public final class PaillierProcessorContextHelper {

    /**
     * 生成同态加密锁需要的数据串
     * 
     * @param ratingsValusOfItemI
     * @param ratingsValusOfItemJ
     * @param numeratorOfSim
     * @param denominatroOfSimAboutI
     * @param denominatroOfSimAboutJ
     */
    public static void forgePaillierDataAsPearson(List<Number> ratingsValusOfItemI,
                                                  List<Number> ratingsValusOfItemJ,
                                                  List<Number> numeratorOfSim,
                                                  List<Number> denominatroOfSimAboutI,
                                                  List<Number> denominatroOfSimAboutJ) {
        //求平均值
        Number avgOfItemI = FunctionHelper.averageValue(ratingsValusOfItemI);
        Number avgOfItemJ = FunctionHelper.averageValue(ratingsValusOfItemJ);

        //向量线性变换
        //Vector: v 
        //Constant Vector: c
        // v - c
        FunctionHelper.tranformAsSubtact(ratingsValusOfItemI, avgOfItemI);
        FunctionHelper.tranformAsSubtact(ratingsValusOfItemJ, avgOfItemJ);

        int size = ratingsValusOfItemI.size();
        for (int i = 0; i < size; i++) {
            //分子加密 , 保证运算进度，*1000
            Number numerator = ratingsValusOfItemI.get(i).doubleValue() * 1000
                               * ratingsValusOfItemJ.get(i).doubleValue() * 1000;
            BigInteger numChiper = PaillierUtil
                .encryptions(BigInteger.valueOf(numerator.intValue()));
            numeratorOfSim.add(numChiper);

            //I部分分母加密
            Number denomiOfI = Math.pow(ratingsValusOfItemI.get(i).doubleValue() * 1000, 2.0);
            BigInteger denomiChiperOfI = PaillierUtil.encryptions(BigInteger.valueOf(denomiOfI
                .intValue()));
            denominatroOfSimAboutI.add(denomiChiperOfI);

            //J部分分母加密
            Number denomiOfJ = Math.pow(ratingsValusOfItemJ.get(i).doubleValue() * 1000, 2.0);
            BigInteger denomiChiperOfJ = PaillierUtil.encryptions(BigInteger.valueOf(denomiOfJ
                .intValue()));
            denominatroOfSimAboutJ.add(denomiChiperOfJ);
        }

    }

    /**
     * 生成常规pearson相似度所需要的数据串
     * 
     * @param ratingsValusOfItemI
     * @param ratingsValusOfItemJ
     * @param numeratorOfSim
     * @param denominatroOfSimAboutI
     * @param denominatroOfSimAboutJ
     */
    public static void forgeDataAsPearson(List<Number> ratingsValusOfItemI,
                                          List<Number> ratingsValusOfItemJ,
                                          List<Number> numeratorOfSim,
                                          List<Number> denominatroOfSimAboutI,
                                          List<Number> denominatroOfSimAboutJ) {
        //求平均值
        Number avgOfItemI = FunctionHelper.averageValue(ratingsValusOfItemI);
        Number avgOfItemJ = FunctionHelper.averageValue(ratingsValusOfItemJ);

        //向量线性变换
        //Vector: v 
        //Constant Vector: c
        // v - c
        FunctionHelper.tranformAsSubtact(ratingsValusOfItemI, avgOfItemI);
        FunctionHelper.tranformAsSubtact(ratingsValusOfItemJ, avgOfItemJ);

        int size = ratingsValusOfItemI.size();
        for (int i = 0; i < size; i++) {
            //分子
            Number numerator = ratingsValusOfItemI.get(i).doubleValue()
                               * ratingsValusOfItemJ.get(i).doubleValue();
            numeratorOfSim.add(numerator);

            //I部分分母
            Number denomiOfI = Math.pow(ratingsValusOfItemI.get(i).doubleValue(), 2.0);
            denominatroOfSimAboutI.add(denomiOfI);

            //J部分分母
            Number denomiOfJ = Math.pow(ratingsValusOfItemJ.get(i).doubleValue(), 2.0);
            denominatroOfSimAboutJ.add(denomiOfJ);
        }

    }

}
