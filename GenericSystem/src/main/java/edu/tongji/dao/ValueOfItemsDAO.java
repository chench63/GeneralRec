package edu.tongji.dao;

import edu.tongji.model.ValueOfItems;

public interface ValueOfItemsDAO {
    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table valueofitems
     *
     * @abatorgenerated Tue Sep 10 14:20:13 CST 2013
     */
    Integer insert(ValueOfItems record);

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table valueofitems
     *
     * @abatorgenerated Tue Sep 10 14:20:13 CST 2013
     */
    int updateByPrimaryKey(ValueOfItems record);

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table valueofitems
     *
     * @abatorgenerated Tue Sep 10 14:20:13 CST 2013
     */
    int updateByPrimaryKeySelective(ValueOfItems record);

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table valueofitems
     *
     * @abatorgenerated Tue Sep 10 14:20:13 CST 2013
     */
    ValueOfItems selectByPrimaryKey(Integer id);

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table valueofitems
     *
     * @abatorgenerated Tue Sep 10 14:20:13 CST 2013
     */
    int deleteByPrimaryKey(Integer id);
}