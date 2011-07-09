package dao.impl;

import java.util.List;

import vo.Matrix;
import vo.User;
import dao.IUserDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;


public class UserDAO extends HibernateDaoSupport 
	implements IUserDAO {

	private static final Log log = LogFactory.getLog(UserDAO.class);
	
	public User findById(int id){
		log.debug("Get Usrfrontinfo id:");
		try {
			User instance = (User) getHibernateTemplate().get(
					"vo.User", id);
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			return null;
		}
	}
	
	public int getMaxId() {
		log.debug("getMaxId");
		try {
			String hql = "select max(usr.usrId) " +
					"from User usr ";
			Query query = this.getSession().createQuery(hql);
			List instance = query.list();
			
			
			this.getSession().flush();
			
			
			log.debug("Get successful");
			return (Integer)instance.get(0);
		} catch (RuntimeException re) {
			System.out.println(re.toString());
			log.error("Get failed", re);
			return -1;
		}
	}

	public void saveUser(User usr) {
		log.debug("Save User");
		try {
			getHibernateTemplate().save(usr);
		} catch (RuntimeException re) {
			System.out.println(re.toString());
			log.error("Get failed", re); 
		}
	}
	
	public boolean checkExist(User usr){
		try {
			String hql = " from User user  " +
					"where user.usrToken = ?  ";
			
			Query query = this.getSession().createQuery(hql);
			query.setParameter(0, usr.getUsrToken());
			log.debug("Get successful");
			
			return query.list().size() != 0 ;
		} catch (RuntimeException re) {
			System.out.println(re.toString());
			log.error("Get failed", re); 
			return false;
		}
	}
	
}
