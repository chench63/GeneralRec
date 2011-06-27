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

import vo.User;
import vo.Usrfrontinfo;

import dao.IUsrFrontInfoDAO;

public class UsrFrontInfoDAO extends HibernateDaoSupport implements
		IUsrFrontInfoDAO {
	private static final Log log = LogFactory.getLog(ServicePetDAO.class);
	
	
	public Usrfrontinfo findByUsr(User usr) {
		log.debug("Get Usrfrontinfo id:"+usr.getUsrId());
		List<Usrfrontinfo> res;
		try {
			if (usr.getUsrId() == null ||
					usr.getUsrId()<0){
				String qStr = "from Usrfrontinfo info " +
						"where info.user.usrToken =? ";
				res= getHibernateTemplate().find(qStr,usr.getUsrToken()); 
				
			}
			else{
				String qStr = "from Usrfrontinfo info " +
					"where info.user.usrId =? ";
				//getHibernateTemplate().findByCriteria(criteria)
				res= getHibernateTemplate().find(qStr,usr.getUsrId());
			}
			           
			log.debug("Get successful");
			if (res != null)
				return res.get(0);
			else
				return null;
		} catch (RuntimeException re) {
			log.error("Get failed", re);
			re.printStackTrace();
			return null;
		}
		
	}

	
	/*
	 * Update all except usrId
	 * 
	 * */
	public void updateUsrFrontInfo(Usrfrontinfo instance){
		log.debug("Updating Servicepet instance id:");
		try {
			getHibernateTemplate().update(instance);
			log.debug("Update successful");
		} catch (RuntimeException re) {
			log.error("Update failed", re);
			throw re;
		}
	}
	
	
}
