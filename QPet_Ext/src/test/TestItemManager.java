package test;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import service.IItemLibService;
import service.impl.ItemLibService;

import cfa.Entity;
import cfa.ItemManager;
import junit.framework.TestCase;

public class TestItemManager extends TestCase {

//	public void testGetList() {
//		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
//		ItemManager im = (ItemManager) ctx.getBean("ItemManager");
//		im.init();
//		
//		List<Entity> eList =  im.getList();
//		
//		
//		System.out.println("********************testGetList**************************");
//		System.out.println("List Size: "+eList.size());
//		
//		for(int i =0; i< eList.size();i++){
//			System.out.println("Entity: "+i+"  size:"+eList.get(i).getRow().size());
//		}
//		
//	}

	public void testSaveList() {
		//fail("Not yet implemented");
	}

}
