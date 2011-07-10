package dao;

import java.util.List;

import vo.Servicepet;
import vo.User;

public interface IServicePetDAO { 
	public void saveServicePet(Servicepet servicepet);
	public void saveOrUpdateServicePet(Servicepet servicepet);
	public Servicepet findById(java.lang.Integer id);
	public List findByUser(User instance);
}
