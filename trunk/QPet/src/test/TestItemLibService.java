package test;
 
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.IServicePetDAO;
import dao.impl.ServicePetDAO;

import service.IItemLibService;
import service.impl.ItemLibService;
import vo.Itemlib;
import vo.User;

import junit.framework.TestCase;

public class TestItemLibService extends TestCase {

	public void testGetItemByUsr() {
		//fail("Not yet implemented");
	}

	public void testSaveItem() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		IItemLibService itemSer = (ItemLibService) ctx.getBean("ItemLibService");
		
		Itemlib item= new Itemlib();
		item.setContext("Test");
		item.setExt("Test");
		item.setImgUrl("Test");
		item.setWebUrl("Test");
		User usr = new User();
		usr.setUsrId(1);
		
		itemSer.saveItem(item, usr);
		System.out.println("**********SaveItem******");
	}

}
