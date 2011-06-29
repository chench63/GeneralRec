package dao;

import vo.Itemlib;
import vo.User;
import vo.Serviceitem;

public interface IServiceItemDAO {
	public Itemlib findByUsr(User usr); 
	public void saveServiceItem(Serviceitem instance);
}
