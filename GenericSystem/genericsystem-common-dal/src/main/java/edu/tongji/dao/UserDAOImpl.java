package edu.tongji.dao;

import edu.tongji.model.User;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class UserDAOImpl extends SqlMapClientDaoSupport implements UserDAO {

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table user
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public UserDAOImpl() {
        super();
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table user
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public String insert(User record) {
        Object newKey = getSqlMapClientTemplate().insert("user.abatorgenerated_insert", record);
        return (String) newKey;
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table user
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public int updateByPrimaryKey(User record) {
        int rows = getSqlMapClientTemplate().update("user.abatorgenerated_updateByPrimaryKey", record);
        return rows;
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table user
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public int updateByPrimaryKeySelective(User record) {
        int rows = getSqlMapClientTemplate().update("user.abatorgenerated_updateByPrimaryKeySelective", record);
        return rows;
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table user
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public User selectByPrimaryKey(String id) {
        User key = new User();
        key.setId(id);
        User record = (User) getSqlMapClientTemplate().queryForObject("user.abatorgenerated_selectByPrimaryKey", key);
        return record;
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table user
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public int deleteByPrimaryKey(String id) {
        User key = new User();
        key.setId(id);
        int rows = getSqlMapClientTemplate().delete("user.abatorgenerated_deleteByPrimaryKey", key);
        return rows;
    }
}