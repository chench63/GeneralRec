package dao;

import java.util.List;

import vo.User;

public interface IUserDAO {
	public int getMaxId();
	public void saveUser(User usr);
	public User findById(int id);
	public boolean checkExist(User usr);
}
