package test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import vo.*;
import dao.impl.UsrFrontInfoDAO;

public class TestUsrFrontInfoDAO extends TestCase {
	
	public void testFindByUsr() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		UsrFrontInfoDAO dao = (UsrFrontInfoDAO) ctx.getBean("UsrFrontInfoDAO");
		
		User user= new User();
		user.setUsrId(1);				
		Usrfrontinfo info=dao.findByUsr(user);
		
		String token="LXUGQ2YXP NCKVSU7G5QIMKI LON2DT P02WAXI4PPCLT70SLWSCF6X078Y68LENMRR0H57KBIVO5F6PFO3ORCOG3DYLGB8Y37BYY18RD79XUTTBV" +
				"MXGTJV0F5O68E5TPY3VN4FJI6IJKR0PV5774B29M952BTFKQ9JRR 362HNO6UAE5FUDKFC714FCPOWVA31IRH2F9US" +
				"BWQRB65SD9QB7OSM0S90XA030EJRC1O0E5C4S8S1HK853GYUX32L";
		User userNew = new User();
		userNew.setUsrToken(token);
		Usrfrontinfo infoNew=dao.findByUsr(userNew);
		
		assertEquals(info.getUser().getUsrId(),user.getUsrId());
		assertEquals(info.getUser().getUsrId(),infoNew.getUser().getUsrId());
	}

	
	public void testUpdateUsrFrontInfo() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		UsrFrontInfoDAO dao = (UsrFrontInfoDAO) ctx.getBean("UsrFrontInfoDAO");
		
		User user= new User();
		user.setUsrId(1);				
		Usrfrontinfo info=dao.findByUsr(user);

	
		System.out.println("FrontId: "+info.getFrontId());
		System.out.println("ItemId: "+info.getItemmatrix().getItemId());
		System.out.println("UsrId: "+info.getUser().getUsrId());
		System.out.println("ServicePetId: "+info.getServicepet().getServicePetId());
		System.out.println("ServiceItemId: "+info.getServiceitem().getServiceItemId());
	
		
		Serviceitem serviceitem= new Serviceitem();
		serviceitem.setServiceItemId(6);
		Servicepet servicepet= new Servicepet();
		servicepet.setServicePetId(6);
		Itemmatrix item= new Itemmatrix();
		item.setItemId(6);
		
		info.setServiceitem(serviceitem);
		info.setServicepet(servicepet);
		info.setItemmatrix(item);
		
		dao.updateUsrFrontInfo(info);
		Usrfrontinfo infoNew=dao.findByUsr(user);
		
		assertEquals(infoNew.getItemmatrix().getItemId(),item.getItemId());
		assertEquals(infoNew.getServiceitem().getServiceItemId(),serviceitem.getServiceItemId());
		assertEquals(infoNew.getServicepet().getServicePetId(),servicepet.getServicePetId());
		
		
	}

}
