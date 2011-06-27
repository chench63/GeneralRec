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

import vo.Matrix;
import vo.User;
import vo.Usrfrontinfo;

import dao.IMatrixDAO;

public class MatrixDAO extends HibernateDaoSupport implements IMatrixDAO {
	private static final Log log = LogFactory.getLog(ServicePetDAO.class);
	 
	public List findByUsr(User usr) {
		log.debug("Get Usrfrontinfo id:"+usr.getUsrId());
		try {
			String hql = "from Matrix matrix where matrix.usrId = ?";
			Query query = this.getSession().createQuery(hql);
			query.setParameter(0, usr.getUsrId());
			List instance = query.list();
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			return null;
		}
	}

	public Matrix findById(java.lang.Integer id) {
		log.debug("Get Usrfrontinfo id:");
		try {
			Matrix instance = (Matrix) getHibernateTemplate().get(
					"vo.Matrix", id);
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			return null;
		}
	}
	
	
	public void saveMatrix(Matrix instance) {
		log.debug("saving Servicepet instance");
		try {
			getHibernateTemplate().save(instance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			re.printStackTrace();
			throw re;
		}
	}

}
