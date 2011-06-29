package dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import vo.Itemlib;

import dao.IItemlibDAO;

public class ItemlibDAO  extends HibernateDaoSupport implements IItemlibDAO{
	private static final Log log = LogFactory.getLog(ServicePetDAO.class);
	 
	public void saveItemlib(Itemlib itemlib) {
		log.debug("saving Servicepet instance");
		try {
			getHibernateTemplate().save(itemlib);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			re.printStackTrace();
			throw re;
		}
		
	}
	
}
