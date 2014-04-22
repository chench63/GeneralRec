/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.model.Rating;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author chench
 * @version $Id: ProcessorContextHelper.java, v 0.1 2013-9-24 下午10:10:06 chench Exp $
 */
public final class ProcessorContextHelper {

    /**
     * 禁用构造函数
     */
    private ProcessorContextHelper() {

    }

    /**
     * 根据评分Rating向量，转化为相应可以计算的Integer向量；
     * <p>
     *      非对称性的评分（存在有个用户每对改item评分），则补零加以处理，提高数据的稀疏度。
     * </p>
     * 
     * @param ratingsOfItemI
     * @param ratingsOfItemJ
     * @param ratingsValusOfItemI
     * @param ratingsValusOfItemJ
     */
    public static void forgeRatingValues(List<Rating> ratingsOfItemI, List<Rating> ratingsOfItemJ,
                                         List<Number> ratingsValusOfItemI,
                                         List<Number> ratingsValusOfItemJ) {
        doForgeRatingValues(ratingsOfItemI, ratingsOfItemJ, ratingsValusOfItemI,
            ratingsValusOfItemJ, true);
    }

    /**
     * 根据评分Rating向量，转化为相应可以计算的Integer向量；
     * <p>
     *      非对称性的评分(存在有个用户每对改item评分)，不计算入内。
     * </p>
     * 
     * @param ratingsOfItemI
     * @param ratingsOfItemJ
     * @param ratingsValusOfItemI
     * @param ratingsValusOfItemJ
     */
    public static void forgeSymmetryRatingValues(List<Rating> ratingsOfItemI,
                                                 List<Rating> ratingsOfItemJ,
                                                 List<Number> ratingsValusOfItemI,
                                                 List<Number> ratingsValusOfItemJ) {
        doForgeRatingValues(ratingsOfItemI, ratingsOfItemJ, ratingsValusOfItemI,
            ratingsValusOfItemJ, false);
    }

    /**
     * 面向Randomized Perturbation算法，
     *  根据评分Rating向量，转化为相应可以计算的Integer向量；
     * 
     * @param ratingsOfItemI
     * @param ratingsOfItemJ
     * @param ratingsValusOfItemI
     * @param ratingsValusOfItemJ
     */
    public static void forgeRandomizedPerturbationRatingValues(List<Rating> ratingsOfItemI,
                                                               List<Rating> ratingsOfItemJ,
                                                               List<Number> ratingsValusOfItemI,
                                                               List<Number> ratingsValusOfItemJ,
                                                               boolean isCountSingles) {
        //优化代码效率，使搜索复杂度为O(1)，但是提高了内存使用率
        Map<String, Integer> usrIdOfItemJ = new HashMap<String, Integer>();
        for (int i = 0; i < ratingsOfItemJ.size(); i++) {
            usrIdOfItemJ.put(ratingsOfItemJ.get(i).getUsrId(), i);
        }

        //扫描ratingsOfItemI所有元素，用户给item打过分则填入真实值，否则补零修正
        RatingVO rating = null;
        Integer indexOfRatingsJ = -1;
        for (int i = 0, j = ratingsOfItemI.size(); i < j; i++) {
            rating = (RatingVO) ratingsOfItemI.get(i);
            //重写了Raing的equal方法，usrId相同即返回true
            //            indexOfRatingsJ = ratingsOfItemJ.indexOf(rating);
            indexOfRatingsJ = usrIdOfItemJ.get(rating.getUsrId());

            if (indexOfRatingsJ != null && indexOfRatingsJ > -1) {
                ratingsValusOfItemI.add((Number) rating.get("DISGUISED_VALUE"));
                ratingsValusOfItemJ.add(((Number) ((RatingVO) ratingsOfItemJ.get(indexOfRatingsJ))
                    .get("DISGUISED_VALUE")));
                ratingsOfItemJ.remove(indexOfRatingsJ);
            } else if (isCountSingles) {
                ratingsValusOfItemI.add((Number) rating.get("DISGUISED_VALUE"));
                ratingsValusOfItemJ.add(0);
            }

        }

        //计算Singles的评分
        //存在看过ItemJ没看过ItemI的用户
        //进行补零
        while (isCountSingles && !ratingsOfItemJ.isEmpty()) {
            ratingsValusOfItemI.add(0);
            ratingsValusOfItemJ.add((Number) ((RatingVO) ratingsOfItemJ.get(0))
                .get("DISGUISED_VALUE"));
            ratingsOfItemJ.remove(0);
        }

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
    private static void doForgeRatingValues(List<Rating> ratingsOfItemI,
                                            List<Rating> ratingsOfItemJ,
                                            List<Number> ratingsValusOfItemI,
                                            List<Number> ratingsValusOfItemJ, boolean isCountSingles) {
        //优化代码效率，使搜索复杂度为O(1)，但是提高了内存使用率
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
                ratingsValusOfItemI.add((Integer) rating.getRating());
                ratingsValusOfItemJ.add(ratingsOfItemJ.get(indexOfRatingsJ).getRating());
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
