package dao.impl;

import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import vo.Itemmatrix;
import vo.Matrix;

import dao.IItemMatrixDAO;

public class ItemMatrixDAO extends HibernateDaoSupport implements
		IItemMatrixDAO {

	private static final Log log = LogFactory.getLog(Itemmatrix.class);
	 
	public Itemmatrix findById(Integer id) {
		log.debug("Get Usrfrontinfo id:");
		try {
			Itemmatrix instance = (Itemmatrix) getHibernateTemplate().get(
					"vo.Itemmatrix", id);
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			return null;
		}
	}

	public void saveItemMatrix(Itemmatrix instance) {
		log.debug("saving Servicepet instance");
		try {
			getHibernateTemplate().save(instance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}

	}

	public int getMaxId() {
		log.debug("getMaxId");
		try {
			String hql = "select max(matrix.itemId) " +
					"from Itemmatrix matrix ";
			Query query = this.getSession().createQuery(hql);
			List instance = query.list();
			log.debug("Get successful");
			return (Integer)instance.get(0);
		} catch (RuntimeException re) {
			System.out.println(re.toString());
			log.error("Get failed", re);
			return -1;
		}
		
	}

	
	
}
