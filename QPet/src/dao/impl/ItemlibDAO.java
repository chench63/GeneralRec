package dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
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
	
	
	public boolean checkExist(Itemlib itemlib){
		try {
			String hql = " from Itemlib lib  " +
					"where lib.imgUrl = ? or lib.context = ? or" +
					"  lib.webUrl = ?  ";
			
			Query query = this.getSession().createQuery(hql);
			query.setParameter(0, itemlib.getImgUrl());
			query.setParameter(1,itemlib.getContext());
			query.setParameter(2,itemlib.getWebUrl());
			
			return query.list().size() != 0 ;
		} catch (RuntimeException re) {
			System.out.println(re.toString());
			log.error("Get failed", re); 
			return false;
		}
	}
	
}
