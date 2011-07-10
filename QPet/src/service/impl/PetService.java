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
	
//	public int getInitPetId(){
//		Random random = new Random(System. currentTimeMillis());
//		int id= petLibDAO.getMaxId();
//		return random.nextInt(id)+1;
//	}

	public int getInitPetId(){
		Random random = new Random(System. currentTimeMillis());
		int id = random.nextInt(130000);
		
		if (id < 1000)
			return 0;
		else if (id < 18800)
			return 1;
		else if (id < 25600)
			return 2;
		else if (id < 38900)
			return 3;
		else if (id < 40870)
			return 4;
		else if (id < 50690)
			return 5;
		else if (id < 60430)
			return 6;
		else if (id < 70780)
			return 7;
		else if (id < 80000)
			return 8;
		else if (id < 90780)
			return 9;
		else if (id < 109800)
			return 10;
		else if (id < 110650)
			return 11;
		else
			return 12;
	}
	
	public int findByUser(User usr) {
		try{
			Servicepet sp= (Servicepet)servicePetDAO.findByUser(usr).get(0);
			return sp.getServicePetId();
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		
	}
	
	
	
	
}
