package dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

}
