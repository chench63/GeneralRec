/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.math.BigInteger;
import java.util.List;

import edu.tongji.function.FunctionHelper;
import edu.tongji.thread.NetflixCmpSimPaillierRecorder;
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
        //1. 求I,J的评分和
        //求和数据经过解密，获得均值
        //用户与服务器交互，用户发送分子加密数据给服务器
        int size = ratingsValusOfItemI.size();
        Number avgOfItemI = BigInteger.ZERO;
        Number avgOfItemJ = BigInteger.ZERO;
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                avgOfItemI = ratingsValusOfItemI.get(0);
                avgOfItemJ = ratingsValusOfItemJ.get(0);
            }

            //求和
            avgOfItemI = PaillierUtil.add((BigInteger) avgOfItemI,
                (BigInteger) ratingsValusOfItemI.get(i));
            avgOfItemJ = PaillierUtil.add((BigInteger) avgOfItemJ,
                (BigInteger) ratingsValusOfItemJ.get(i));
        }

        //2. 求分子与分母各个部分
        for (int i = 0; i < size; i++) {
            //模拟值
            BigInteger chipher = NetflixCmpSimPaillierRecorder.CHIPHER_CACHE[i
                                                                             % NetflixCmpSimPaillierRecorder.CHIPHER_CACHE.length];
            //分子部分
            //服务器计算均值后，发给用户，用户计算分子部分值，
            //完成加密，发送给服务器, 此值模拟
            numeratorOfSim.add(chipher);

            //I分母部分
            //服务器计算均值后，发给用户，用户计算分子部分值，
            //完成加密，发送给服务器, 此值模拟
            denominatroOfSimAboutI.add(chipher);

            //J分母部分
            //服务器计算均值后，发给用户，用户计算分子部分值，
            //完成加密，发送给服务器, 此值模拟
            denominatroOfSimAboutJ.add(chipher);
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
            Number numerator = ratingsValusOfItemI.get(i).floatValue()
                               * ratingsValusOfItemJ.get(i).floatValue();
            numeratorOfSim.add(numerator);

            //I部分分母
            Number denomiOfI = Math.pow(ratingsValusOfItemI.get(i).floatValue(), 2.0);
            denominatroOfSimAboutI.add(denomiOfI);

            //J部分分母
            Number denomiOfJ = Math.pow(ratingsValusOfItemJ.get(i).floatValue(), 2.0);
            denominatroOfSimAboutJ.add(denomiOfJ);
        }

    }

}
