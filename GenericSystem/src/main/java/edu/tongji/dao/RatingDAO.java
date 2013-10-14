package edu.tongji.dao;

import java.util.List;
import java.util.Map;

import edu.tongji.model.Rating;

public interface RatingDAO {
    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table rating
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    String insert(Rating record);

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table rating
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    int updateByPrimaryKey(Rating record);

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table rating
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    int updateByPrimaryKeySelective(Rating record);

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table rating
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    @SuppressWarnings("rawtypes")
    List<Rating> select(Map map);

    /**
     * DataSource.excute中使用
     * 
     * @param type
     * @param param
     * @return
     */
    List<Rating> select(String type, List<String> param);

    /**
     * 计算itemI和itemJ一共的评价数
     * 
     * @param itemI
     * @param itemJ
     * @return
     */
    int countTotalItems(int itemI, int itemJ);

    /**
     * 计算itemI和itemJ丢失的评价数
     * 
     * @param itemI
     * @param itemJ
     * @return
     */
    int countMissingItems(int itemI, int itemJ);

}