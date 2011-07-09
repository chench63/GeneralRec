package test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.impl.UserDAO;

import service.IItemLibService;
import service.impl.ItemLibService;
import vo.User;

public class TestUserDAO {

	@Test
	public void testFindById() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		UserDAO dao = (UserDAO) ctx.getBean("UserDAO");
		
		int usrId = 1;
		User usr = dao.findById(usrId);
		String token= "LXUGQ2YXP NCKVSU7G5QIMKI LON2DT P02WAXI4PPCL" +
				"T70SLWSCF6X078Y68LENMRR0H57KBIVO5F6PFO3ORCOG3DYLGB8Y37BYY18RD79" +
				"XUTTBVMXGTJV0F5O68E5TPY3VN4FJI6" +
				"IJKR0PV5774B29M952BTFKQ9JRR 362HNO6UAE5FUDKFC714" +
				"FCP" +
				"OWVA31IRH2F9USBWQRB65SD9QB7OSM0S90XA030EJ" +
				"RC1O0E5C4S8S1HK853GYUX32L";
		
//		System.out.println(usr.getUsrToken());
		
		assertEquals(usr.getUsrToken(),token);
		
		System.out.println("************************testFindById************************");
		
	}

	@Test
	public void testGetMaxId() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		UserDAO dao = (UserDAO) ctx.getBean("UserDAO");
		
		System.out.println("\n\n************************testGetMaxId************************");
		
		for(int i=0;i<5;i++)
			System.out.println(i+":  "+dao.getMaxId());
	}

	@Test
	public void testSaveUser() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		UserDAO dao = (UserDAO) ctx.getBean("UserDAO");
		
		String token = "TEST";
		User usr =  new User();
		usr.setUsrToken(token);
		
		System.out.println("\n\n************************testSaveUser************************");
		System.out.println(dao.checkExist(usr));
	}

	@Test
	public void testCheckExist() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		UserDAO dao = (UserDAO) ctx.getBean("UserDAO");
		
		String token = "TEST";
		User usr =  new User();
		usr.setUsrToken(token);
		
		System.out.println("\n\n************************testSaveUser************************");
		System.out.println(dao.checkExist(usr));
	}
	
}
