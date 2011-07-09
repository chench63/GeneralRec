package service.impl;

import service.IUserService;
import vo.User;
import dao.IUserDAO;
import dao.impl.UserDAO;


public class UserService implements IUserService {
	public IUserDAO userDAO;
	
	public IUserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(IUserDAO userDAO) {
		this.userDAO = userDAO;
	}




	public void saveOrUpdateUser(User usr) {
		userDAO.saveUser(usr);
	}

}
