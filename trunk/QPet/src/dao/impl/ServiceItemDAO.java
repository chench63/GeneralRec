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



import dao.IServiceItemDAO;
import vo.Itemlib;
import vo.Serviceitem;
import vo.User;
import vo.Usrfrontinfo;

public class ServiceItemDAO extends HibernateDaoSupport implements
		IServiceItemDAO {
	private static final Log log = LogFactory.getLog(ServicePetDAO.class);
	
	public Itemlib findByUsr(User usr){
		log.debug("Get Usrfrontinfo id:"+usr.getUsrId());
		try {
			Itemlib instance = (Itemlib) getHibernateTemplate().get(
					"vo.Usrfrontinfo", usr.getUsrId());
			log.debug("Get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			return null;
		}
	}


	public void saveServiceItem(Serviceitem instance) {
		log.debug("Save ServiceItem");
		try {
			getHibernateTemplate().save(instance);
			log.debug("Save successful");
		} catch (RuntimeException re) {
			log.error("Save failed", re);
		}
		
	}
	
}
