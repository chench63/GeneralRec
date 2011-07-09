package service;

import vo.User;

public interface IUserService {
	public void save(User usr);
	public boolean checkExist(User usr);
}
