package edu.tongji.dao;

import edu.tongji.model.Movie;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class MovieDAOImpl extends SqlMapClientDaoSupport implements MovieDAO {

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table movie
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public MovieDAOImpl() {
        super();
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table movie
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public String insert(Movie record) {
        Object newKey = getSqlMapClientTemplate().insert("movie.abatorgenerated_insert", record);
        return (String) newKey;
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table movie
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public int updateByPrimaryKey(Movie record) {
        int rows = getSqlMapClientTemplate().update("movie.abatorgenerated_updateByPrimaryKey", record);
        return rows;
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table movie
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public int updateByPrimaryKeySelective(Movie record) {
        int rows = getSqlMapClientTemplate().update("movie.abatorgenerated_updateByPrimaryKeySelective", record);
        return rows;
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table movie
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public Movie selectByPrimaryKey(String id) {
        Movie key = new Movie();
        key.setId(id);
        Movie record = (Movie) getSqlMapClientTemplate().queryForObject("movie.abatorgenerated_selectByPrimaryKey", key);
        return record;
    }

    /**
     * This method was generated by Abator for iBATIS.
     * This method corresponds to the database table movie
     *
     * @abatorgenerated Tue Sep 10 11:05:22 CST 2013
     */
    public int deleteByPrimaryKey(String id) {
        Movie key = new Movie();
        key.setId(id);
        int rows = getSqlMapClientTemplate().delete("movie.abatorgenerated_deleteByPrimaryKey", key);
        return rows;
    }
}