package dao.impl;

import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;



import vo.Itemmatrix;
import vo.Servicepet;
import vo.User;
import dao.IServicePetDAO;


public class ServicePetDAO extends HibernateDaoSupport implements
		IServicePetDAO {
	private static final Log log = LogFactory.getLog(ServicePetDAO.class);
	
	
	public void saveServicePet(Servicepet servicepet){
		log.debug("saving Servicepet instance");
		try {
			getHibernateTemplate().save(servicepet);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}
	
	/*Servicepet just update  "level,exper "
	 * 			servicePetId is the Index
	 * */
	public void saveOrUpdateServicePet(Servicepet servicepet){
		log.debug("Updating Servicepet instance id:"+servicepet.getServicePetId());
		try {
			getHibernateTemplate().saveOrUpdate(servicepet);
			log.debug("Update successful");
		} catch (RuntimeException re) {
			log.error("Update failed", re);
			throw re;
		}
	}
 
	public Servicepet findById(Integer id) {
		log.debug("Get Usrfrontinfo id:");
		try {
			Servicepet instance = (Servicepet) getHibernateTemplate().get(
					"vo.Servicepet", id);
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			return null;
		}
	}

	public List findByUser(User usr) {
		log.debug("Get Usrfrontinfo id:");
		try {
			String hql = "from Servicepet as servicepet where servicepet.user.usrId = ? ";
			Query query = this.getSession().createQuery(hql);
			query.setParameter(0, usr.getUsrId());
			List instance = query.list();
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			System.out.println(re.toString());
			log.error("Get failed", re);
			return null;
		}
	}	
	
	
	
}
