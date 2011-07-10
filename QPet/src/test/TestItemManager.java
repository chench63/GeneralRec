package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import service.IItemLibService;
import service.impl.ItemLibService;
import vo.Matrix;
import vo.User;

import cfa.Entity;
import cfa.ItemManager;
import junit.framework.TestCase;

public class TestItemManager extends TestCase {

	public void testGetList() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		ItemManager im = (ItemManager) ctx.getBean("ItemManager");
		im.init();
		
		List<Entity> eList =  im.getList();
		
		
		System.out.println("\n\n********************testGetList**************************");
		System.out.println("List Size: "+eList.size());
		
		for(int i =0; i< eList.size();i++){
			System.out.println("Entity: "+i+"  size:"+eList.get(i).getRow().size());
		}
		
	}

	public void testGetEntity() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		ItemManager im = (ItemManager) ctx.getBean("ItemManager");
		im.init();
		
		List<Matrix> mList =  new ArrayList<Matrix>();
		Matrix matrix_30 = new Matrix();
		matrix_30.setScore(10);
		matrix_30.setMatrixId(30);
		mList.add(matrix_30);
		
		User usr =  new User(1);
		Entity entity= im.getEntity(usr);
		
		List<Matrix> rList = entity.getRow();
		
//		System.out.println("rList Size: "+ rList.size());
		
		
		System.out.println("\n\n**********************testGetEntity******************");
		for(Iterator<Matrix> iter=rList.iterator();iter.hasNext();){
			Matrix temp = iter.next();
			if (temp.getScore() != 0)
				System.out.println("ItemId: "+temp.getItemmatrix().getItemId()+"  Score: "+temp.getScore()+"  *");
			else
				System.out.println("ItemId: "+temp.getItemmatrix().getItemId()+"  Score: "+temp.getScore());
		}
	}

}
