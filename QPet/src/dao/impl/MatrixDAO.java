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
import vo.User;
import vo.Usrfrontinfo;

import dao.IMatrixDAO;

public class MatrixDAO extends HibernateDaoSupport implements IMatrixDAO {
	private static final Log log = LogFactory.getLog(ServicePetDAO.class);
	 
	public List<Matrix> findByUsr(User usr) {
		log.debug("findByUsr"+usr.getUsrId());
		
		try {
			String hql = "from Matrix matrix where matrix.user.usrId = ?";

			Query query = this.getSession().createQuery(hql);
			query.setParameter(0, usr.getUsrId());
			
//			System.out.println("1.GetUsrId:  "+usr.getUsrId());

			List<Matrix> instance = query.list();
			
			System.out.println("2.GetUsrId:  "+usr.getUsrId());	
			
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			re.printStackTrace();
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
	
	public List<Matrix> getFilterUserSet( User lUser, User hUser ) {
		log.debug("getFilterItem");
		try {
			String hql = 
						"select matrix " +
						"from  Matrix matrix  " +
						"group by matrix.user.usrId  " +
						"Having count(matrix) >= 1  " +
						"and matrix.user.usrId > ?  and matrix.user.usrId <= ?  " ;
			Query query = this.getSession().createQuery(hql);
			query.setParameter(0, lUser.getUsrId());
			query.setParameter(1, hUser.getUsrId());
			List<Matrix> instance = query.list();
			
//			System.out.println(
//					"The Message is from MatrixDAO.getFilterItem:   "+
//					instance.size()
//					);
			
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			re.printStackTrace();
			return null;
		}
	}
	
	public Itemmatrix getItemmatrixByUser(User usr){
		try {
			String hql = "select item  " +
					"from Itemmatrix item  "+
					"where item.itemId not IN (  "+
					"select matrix.itemmatrix.itemId from Matrix matrix "+
					"where matrix.user.usrId = ? " +
					"	)";
			
			Query query = this.getSession().createQuery(hql);
			query.setParameter(0, usr.getUsrId());
			List<Itemmatrix> instance = query.list();
			
			log.debug("Get successful");
			if ( instance.size() > 0 )
				return instance.get(0);
			else
				return null;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			re.printStackTrace();
			return null;
		}
	}
}
