package service;

import vo.Petlib;
import vo.Servicepet;
import vo.User;
import vo.Usrfrontinfo;
 
public interface IPetService {
	public Petlib getPetItem(Usrfrontinfo usr);
	public Servicepet getServicepet(Usrfrontinfo usr);
	public void saveOrUpdateServicePet(Servicepet servicepet);
	public int getInitPetId();
	public int findByUser(User usr);
}
