package ManagerExt;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import vo.Itemlib;
import vo.Itemmatrix;
import vo.Petlib;
import vo.Servicepet;
import vo.Usrfrontinfo;

import bo.petManager;

public class King {
	public Usrfrontinfo GetInstance(String token){
		try{
			ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
			petManager pet = (petManager) ctx.getBean("petManager");
			return pet.GetInstance(token);
		}catch(Exception e){
			return null;
		}
	}
	
	public Petlib GetDefaultPet(Usrfrontinfo info){
		try{
			ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
			petManager pet = (petManager) ctx.getBean("petManager");
			return pet.GetDefaultPet(info);
		}catch(Exception e){
			return null;
		}
		
	} 
	
	public Servicepet GetDefaltServicePetItem(Usrfrontinfo info){
		try{
			ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
			petManager pet = (petManager) ctx.getBean("petManager");
			return pet.GetDefaltServicePetItem(info);
		}catch(Exception e){
			return null;
		}
	}
	
	public boolean CloseServicePetItem(Servicepet servicepet){
		try{
			ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
			petManager pet = (petManager) ctx.getBean("petManager");
			pet.CloseServicePetItem(servicepet);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public Itemmatrix GetRatingItem(Usrfrontinfo info){
		try{
			ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
			petManager pet = (petManager) ctx.getBean("petManager");
			return pet.GetRatingItem(info);
		}catch(Exception e){
			return null;
		}
	}
	
	public boolean CloseRatingItem(Usrfrontinfo info, Integer score){
		try{
			ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
			petManager pet = (petManager) ctx.getBean("petManager");
			pet.CloseRatingItem(info, score);
			return true;
		}catch(Exception e){
			return false;
		}
		
	}
	
	public Itemlib GetInterestingItem(Usrfrontinfo info){
		try{
			ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
			petManager pet = (petManager) ctx.getBean("petManager");
			return pet.GetInterestingItem(info);
		}catch(Exception e){
			return null;
		}
		
	}
	
	public boolean AppendInterestingItem(Itemlib item, Usrfrontinfo info){
		try{
			ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
			petManager pet = (petManager) ctx.getBean("petManager");
			return pet.AppendInterestingItem(item, info);
		}catch(Exception e){
			return false;
		}
	}
	
}
