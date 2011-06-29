package service;

import vo.Itemlib;
import vo.Serviceitem;
import vo.User;
import vo.Usrfrontinfo;
 
public interface IItemLibService {
		public Itemlib getItemByUsr(Usrfrontinfo info);
		public void saveItem(Itemlib item,User usr);
}
