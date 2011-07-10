package dao;

import vo.Itemlib;
import vo.User;
import vo.Serviceitem;

public interface IServiceItemDAO {
	public Itemlib findByUsr(User usr); 
	public void saveServiceItem(Serviceitem instance);
	
	/*
	 * Merge the User into a field for 
	 * collaborative filtering
	 * */
	public void mergeForCol(User usr,String token);
	
	/*
	 * Merge the rest User into the unique field for 
	 * collaborative filtering
	 * */
	public void mergeRestForCol(String uToken, String oriToken);
}
