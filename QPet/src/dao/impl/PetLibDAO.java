package dao.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import vo.Matrix;
import vo.Petlib;

import dao.IPetLibDAO;

public class PetLibDAO extends HibernateDaoSupport implements IPetLibDAO {
	private static final Log log = LogFactory.getLog(ServicePetDAO.class);
	
	
	public Petlib findById(Integer id) {
		log.debug("Get Usrfrontinfo id:");
		try {
			Petlib instance = (Petlib) getHibernateTemplate().get(
					"vo.Petlib", id);
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			return null;
		}
	}


	public int getMaxId() {
		log.debug("getRandomPet");
		try {
			String hql = "select max(pet.petId) " +
					"from Petlib pet ";
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
