package dao;

import vo.User;
import vo.Usrfrontinfo;

public interface IUsrFrontInfoDAO { 
	public Usrfrontinfo findByUsr(User usr);
	public void updateUsrFrontInfo(Usrfrontinfo instance);
}
