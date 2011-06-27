package service.impl;

import service.IItemLibService;
import vo.Itemlib;
import vo.Serviceitem;
import vo.User;
import vo.Usrfrontinfo;

import dao.IItemlibDAO;
import dao.IServiceItemDAO;
 
public class ItemLibService implements IItemLibService {
	private IItemlibDAO itemlibDAO;
	private IServiceItemDAO serviceItemDAO;
	
	public IItemlibDAO getItemlibDAO() {
		return itemlibDAO;
	}
	public void setItemlibDAO(IItemlibDAO itemlibDAO) {
		this.itemlibDAO = itemlibDAO;
	}
	public IServiceItemDAO getServiceItemDAO() {
		return serviceItemDAO;
	}
	public void setServiceItemDAO(IServiceItemDAO serviceItemDAO) {
		this.serviceItemDAO = serviceItemDAO;
	}
	
	
	//___________________main method
	public Itemlib getItemByUsr(Usrfrontinfo info){
		return null;
	}
	
	public void saveItem(Itemlib item,User usr){
		itemlibDAO.saveItemlib(item);
		
		Serviceitem instance= new Serviceitem();
		instance.setItemlib(item);
		instance.setUser(usr);
		
		serviceItemDAO.saveServiceItem(instance);
	}
}
