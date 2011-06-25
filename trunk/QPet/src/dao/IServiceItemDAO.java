package dao;

import vo.Itemlib;
import vo.User;

public interface IServiceItemDAO {
	public Itemlib findByUsr(User usr);
}
