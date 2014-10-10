/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.engine.recommendation.thread.NetflixCmpSimPaillierRecorder;
import edu.tongji.model.Rating;
import edu.tongji.vo.RatingVO;

/**
 * 推荐系统所属，上下文帮助类 
 * 
 * @author Hanke Chen
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
     *      非对称性的评分(存在有个用户每对改item评分)，不计算入内。
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
        //优化代码效率，使搜索复杂度为O(1)，但是提高了内存使用率
        Map<Integer, Integer> usrIdOfItemJ = new HashMap<Integer, Integer>();
        for (int i = 0; i < ratingsOfItemJ.size(); i++) {
            usrIdOfItemJ.put(ratingsOfItemJ.get(i).getUsrId(), i);
        }

        //扫描ratingsOfItemI所有元素
        Rating rating = null;
        Integer indexOfRatingsJ = -1;
        for (int i = 0, j = ratingsOfItemI.size(); i < j; i++) {
            rating = ratingsOfItemI.get(i);
            indexOfRatingsJ = usrIdOfItemJ.get(rating.getUsrId());

            if (indexOfRatingsJ != null && indexOfRatingsJ > -1) {
                ratingsValusOfItemI.add(rating.getRating());
                ratingsValusOfItemJ.add(ratingsOfItemJ.get(indexOfRatingsJ).getRating());
            }

        }
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
    public static void forgeSymmetryRatingValues(List<RatingVO> ratingsOfItemI,
                                                 List<RatingVO> ratingsOfItemJ,
                                                 List<Number> ratingsValusOfItemI,
                                                 List<Number> ratingsValusOfItemJ) {
        //优化代码效率，使搜索复杂度为O(1)，但是提高了内存使用率
        Map<Integer, Integer> usrIdOfItemJ = new HashMap<Integer, Integer>(ratingsOfItemJ.size());
        for (int i = 0; i < ratingsOfItemJ.size(); i++) {
            usrIdOfItemJ.put(ratingsOfItemJ.get(i).getUsrId(), i);
        }

        //扫描ratingsOfItemI所有元素
        RatingVO rating = null;
        Integer indexOfRatingsJ = -1;
        for (int i = 0, j = ratingsOfItemI.size(); i < j; i++) {
            rating = ratingsOfItemI.get(i);
            indexOfRatingsJ = usrIdOfItemJ.get(rating.getUsrId());

            if (indexOfRatingsJ != null && indexOfRatingsJ > -1) {
                ratingsValusOfItemI.add(rating.getRatingCmp());
                ratingsValusOfItemJ.add(ratingsOfItemJ.get(indexOfRatingsJ).getRatingCmp());
            }
        }

        //对齐内存
        ((ArrayList<Number>) ratingsValusOfItemI).ensureCapacity(ratingsValusOfItemI.size());
        ((ArrayList<Number>) ratingsValusOfItemJ).ensureCapacity(ratingsValusOfItemJ.size());
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
    public static void forgeSymmetryChipherValues(List<RatingVO> ratingsOfItemI,
                                                  List<RatingVO> ratingsOfItemJ,
                                                  List<Number> ratingsValusOfItemI,
                                                  List<Number> ratingsValusOfItemJ) {
        //优化代码效率，使搜索复杂度为O(1)，但是提高了内存使用率
        Map<Integer, Integer> usrIdOfItemJ = new HashMap<Integer, Integer>(ratingsOfItemJ.size());
        for (int i = 0; i < ratingsOfItemJ.size(); i++) {
            usrIdOfItemJ.put(ratingsOfItemJ.get(i).getUsrId(), i);
        }

        //扫描ratingsOfItemI所有元素
        RatingVO rating = null;
        Integer indexOfRatingsJ = -1;
        for (int i = 0, j = ratingsOfItemI.size(); i < j; i++) {
            rating = ratingsOfItemI.get(i);
            indexOfRatingsJ = usrIdOfItemJ.get(rating.getUsrId());

            if (indexOfRatingsJ != null && indexOfRatingsJ > -1) {
                ratingsValusOfItemI.add(NetflixCmpSimPaillierRecorder.CHIPHER_CACHE[rating
                    .getRatingCmp().intValue()]);
                ratingsValusOfItemJ.add(NetflixCmpSimPaillierRecorder.CHIPHER_CACHE[ratingsOfItemJ
                    .get(indexOfRatingsJ).getRatingCmp().intValue()]);
            }
        }

        //对齐内存
        ((ArrayList<Number>) ratingsValusOfItemI).ensureCapacity(ratingsValusOfItemI.size());
        ((ArrayList<Number>) ratingsValusOfItemJ).ensureCapacity(ratingsValusOfItemJ.size());
    }

}
