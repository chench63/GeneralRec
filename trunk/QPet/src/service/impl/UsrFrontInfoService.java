package service.impl;

import service.IUsrFrontInfoService;
import vo.User;
import vo.Usrfrontinfo;

import vo.Servicepet;
import vo.Serviceitem;
import vo.Itemmatrix;

import dao.IUsrFrontInfoDAO;
 

public class UsrFrontInfoService implements IUsrFrontInfoService{
	private IUsrFrontInfoDAO usrFrontInfoDAO;
	
	public IUsrFrontInfoDAO getUsrFrontInfoDAO() {
		return usrFrontInfoDAO;
	}

	public void setUsrFrontInfoDAO(IUsrFrontInfoDAO usrFrontInfoDAO) {
		this.usrFrontInfoDAO = usrFrontInfoDAO;
	}

	
	public Usrfrontinfo GetInstanceByUsr(User instance){
		return usrFrontInfoDAO.findByUsr(instance);
	}

	public void saveOrUpdate(Usrfrontinfo instance) {
		usrFrontInfoDAO.updateUsrFrontInfo(instance);
		
	}	

	
}
