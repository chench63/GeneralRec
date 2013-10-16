/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.model.Rating;

/**
 * 
 * @author chenkh
 * @version $Id: ProcessorContextHelper.java, v 0.1 2013-9-24 下午10:10:06 chenkh Exp $
 */
public final class ProcessorContextHelper {

    /**
     * 禁用构造函数
     */
    private ProcessorContextHelper() {

    }

    /**
     * 根据评分Rating向量，转化为相应可以计算的Integer向量
     * 
     * @param ratingsOfItemI
     * @param ratingsOfItemJ
     * @param ratingsValusOfItemI
     * @param ratingsValusOfItemJ
     */
    public static void forgeRatingValues(List<Rating> ratingsOfItemI, List<Rating> ratingsOfItemJ,
                                         List<Integer> ratingsValusOfItemI,
                                         List<Integer> ratingsValusOfItemJ) {
        doForgeRatingValues(ratingsOfItemI, ratingsOfItemJ, ratingsValusOfItemI,
            ratingsValusOfItemJ, true);
    }

    /**
     * 根据评分Rating向量，转化为相应可以计算的Integer向量
     * 
     * @param ratingsOfItemI
     * @param ratingsOfItemJ
     * @param ratingsValusOfItemI
     * @param ratingsValusOfItemJ
     * @param isCountSingles
     */
    public static void doForgeRatingValues(List<Rating> ratingsOfItemI,
                                           List<Rating> ratingsOfItemJ,
                                           List<Integer> ratingsValusOfItemI,
                                           List<Integer> ratingsValusOfItemJ, boolean isCountSingles) {
        Map<String, Integer> usrIdOfItemJ = new HashMap<String, Integer>();
        for (int i = 0; i < ratingsOfItemJ.size(); i++) {
            usrIdOfItemJ.put(ratingsOfItemJ.get(i).getUsrId(), i);
        }

        //扫描ratingsOfItemI所有元素，用户给item打过分则填入真实值，否则补零修正
        Rating rating = null;
        Integer indexOfRatingsJ = -1;
        for (int i = 0, j = ratingsOfItemI.size(); i < j; i++) {
            rating = ratingsOfItemI.get(i);
            //重写了Raing的equal方法，usrId相同即返回true
            //            indexOfRatingsJ = ratingsOfItemJ.indexOf(rating);
            indexOfRatingsJ = usrIdOfItemJ.get(rating.getUsrId());

            if (indexOfRatingsJ != null && indexOfRatingsJ > -1) {
                ratingsValusOfItemI.add(rating.getRating());
                ratingsValusOfItemJ.add(ratingsOfItemJ.get(indexOfRatingsJ).getRating());
                ratingsOfItemJ.remove(indexOfRatingsJ);
            } else if (isCountSingles) {
                ratingsValusOfItemI.add(rating.getRating());
                ratingsValusOfItemJ.add(0);
            }

        }

        //计算Singles的评分
        //存在看过ItemJ没看过ItemI的用户
        //进行补零
        while (isCountSingles && !ratingsOfItemJ.isEmpty()) {
            ratingsValusOfItemI.add(0);
            ratingsValusOfItemJ.add(ratingsOfItemJ.get(0).getRating());
            ratingsOfItemJ.remove(0);
        }
    }

}
