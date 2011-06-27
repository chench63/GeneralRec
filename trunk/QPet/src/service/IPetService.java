package service;

import vo.Petlib;
import vo.Servicepet;
import vo.Usrfrontinfo;
 
public interface IPetService {
	public Petlib getPetItem(Usrfrontinfo usr);
	public Servicepet getServicepet(Usrfrontinfo usr);
	public void updateServicePet(Servicepet servicepet);
}
