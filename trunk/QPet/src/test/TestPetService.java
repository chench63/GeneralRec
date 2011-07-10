package test;

import junit.framework.TestCase;

import java.util.List;

import javassist.bytecode.Descriptor.Iterator;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.impl.UsrFrontInfoDAO;
import vo.Servicepet;
import vo.User;
import vo.Petlib;
import vo.Usrfrontinfo;

import service.IPetService;

import service.impl.PetService;;

 
public class TestPetService extends TestCase {
	@Test
	public void testGetPetItem() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		
		UsrFrontInfoDAO dao = (UsrFrontInfoDAO) ctx.getBean("UsrFrontInfoDAO");	
		User user= new User();
		user.setUsrId(1);				
		Usrfrontinfo info=dao.findByUsr(user);		

		IPetService ps = (PetService) ctx.getBean("PetService");
		Petlib lib= ps.getPetItem(info);
		
		System.out.println("******************testGetPetItem*****************");
		System.out.println("PetId: "+lib.getPetId());
		System.out.println("SrcUrl: "+lib.getSrcUrl());
	}
	
	
	@Test
	public void testGetServicepet() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		
		UsrFrontInfoDAO dao = (UsrFrontInfoDAO) ctx.getBean("UsrFrontInfoDAO");	
		User user= new User();
		user.setUsrId(1);				
		Usrfrontinfo info=dao.findByUsr(user);		

		IPetService ps = (PetService) ctx.getBean("PetService");
		Servicepet spet= ps.getServicepet(info);
		
		System.out.println("\n\n******************testGetServicepet*****************");
		System.out.println("ServicePetId: "+spet.getServicePetId());
		System.out.println("PetId: "+spet.getPetlib().getPetId());
		System.out.println("PetExp: "+spet.getExp());
		System.out.println("Level: "+spet.getLevel());
		System.out.println(spet.getUser().getUsrId());
		//assertEquals((Integer)1,spet.getUser().getUsrId());
	}

	
	public void testGetInitPetId() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		
		UsrFrontInfoDAO dao = (UsrFrontInfoDAO) ctx.getBean("UsrFrontInfoDAO");	
		User user= new User();
		user.setUsrId(1);				
		Usrfrontinfo info=dao.findByUsr(user);		

		IPetService ps = (PetService) ctx.getBean("PetService");
		
		System.out.println("\n\n******************testGetInitPetId*****************");
		System.out.println(ps.getInitPetId());
	}
	
	
	/*
	@Test
	public void testUpdateServicePet() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		
		UsrFrontInfoDAO dao = (UsrFrontInfoDAO) ctx.getBean("UsrFrontInfoDAO");	
		User user= new User();
		user.setUsrId(1);				
		Usrfrontinfo info=dao.findByUsr(user);

		IPetService ps = (PetService) ctx.getBean("PetService");
		
		
		System.out.println("\n\n******************testUpdateServicePet*****************");
	}
	*/
}
