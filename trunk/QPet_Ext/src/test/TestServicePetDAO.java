package test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import vo.User;
import vo.Servicepet;
import dao.impl.ServicePetDAO;
 
public class TestServicePetDAO {

	@Test
	public void testSaveServicePet() {
		//fail("Not yet implemented");
	}

	@Test
	public void testUpdateServicePet() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		ServicePetDAO dao = (ServicePetDAO) ctx.getBean("ServicePetDAO");
		User user= new User();
		user.setUsrId(2);
		
		Servicepet pet= dao.findById(2);
		int exp= 100;
		int level= 10;
		pet.setLevel(level);
		pet.setExp(exp);
		
		dao.saveOrUpdateServicePet(pet);
		pet= dao.findById(2);
		
		assertEquals(exp,pet.getExp());
		assertEquals(level,pet.getLevel());
	}

	@Test
	public void testFindById() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		ServicePetDAO dao = (ServicePetDAO) ctx.getBean("ServicePetDAO");
		User user= new User();
		user.setUsrId(2);
		
		Servicepet pet= dao.findById(2);
		assertEquals(pet.getServicePetId(),2);
	}

	@Test
	public void testFindByUser() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		ServicePetDAO dao = (ServicePetDAO) ctx.getBean("ServicePetDAO");
		User user= new User();
		user.setUsrId(2);
		List list=dao.findByUser(user);
		
		Servicepet pet;
		for(Iterator iter = list.iterator(); iter.hasNext();){
			pet= (Servicepet) iter.next();
			assertEquals(pet.getUser().getUsrId(),user.getUsrId());
			
			
			System.out.println("UserId: "+pet.getUser().getUsrId());
			System.out.println("Exp: "+pet.getExp());
			System.out.println("Level: "+pet.getLevel());
		}
		
		
	}

}
