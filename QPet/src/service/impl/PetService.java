package service.impl;


import java.util.Random;

import service.IPetService;
import vo.Petlib;
import vo.Servicepet;
import vo.User;

import dao.IServicePetDAO;
import dao.IUsrFrontInfoDAO;
import dao.IPetLibDAO;
import vo.Usrfrontinfo;
import vo.Petlib;
 
public class PetService implements IPetService{
	private IServicePetDAO servicePetDAO;
	private IUsrFrontInfoDAO usrFrontInfoDAO;
	private IPetLibDAO petLibDAO;
	
	public IPetLibDAO getPetLibDAO() {
		return petLibDAO;
	}

	public void setPetLibDAO(IPetLibDAO petLibDAO) {
		this.petLibDAO = petLibDAO;
	}

	public IUsrFrontInfoDAO getUsrFrontInfoDAO() {
		return usrFrontInfoDAO;
	}

	public void setUsrFrontInfoDAO(IUsrFrontInfoDAO usrFrontInfoDAO) {
		this.usrFrontInfoDAO = usrFrontInfoDAO;
	}
	

	public IServicePetDAO getServicePetDAO() {
		return servicePetDAO;
	}

	public void setServicePetDAO(IServicePetDAO servicePetDAO) {
		this.servicePetDAO = servicePetDAO;
	}

	
	
	
	/*
	 * Service Method
	 * */
	public Petlib getPetItem(Usrfrontinfo usr){
		int petId=this.getServicepet(usr).getPetlib().getPetId();
		return petLibDAO.findById(petId);
	}
	
	public Servicepet getServicepet(Usrfrontinfo usr){
		int petSerId = usr.getServicePetId();
		return servicePetDAO.findById(petSerId);
	}
	
	public void saveOrUpdateServicePet(Servicepet servicepet){
		servicePetDAO.saveOrUpdateServicePet(servicepet);
	}
	
	public int getInitPetId(){
		Random random = new Random(System. currentTimeMillis());
		int id= petLibDAO.getMaxId();
		return random.nextInt(id)+1;
	}

	public int findByUser(User usr) {
		try{
			Servicepet sp= (Servicepet)servicePetDAO.findByUser(usr).get(0);
			return sp.getUser().getUsrId();
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		
	}
	
	
	
	
}
