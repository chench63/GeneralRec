package service;

import vo.User;
import vo.Usrfrontinfo;
 
public interface IUsrFrontInfoService {
	public Usrfrontinfo GetInstanceByUsr(User instance);
}
