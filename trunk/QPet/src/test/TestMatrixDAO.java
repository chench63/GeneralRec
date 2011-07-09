package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import vo.Itemmatrix;
import vo.Matrix;
import vo.User;

import dao.impl.MatrixDAO;

public class TestMatrixDAO {

	@Test
	public void testFindByUsr() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		MatrixDAO dao = (MatrixDAO) ctx.getBean("MatrixDAO");
		
		int usrId = 1;
		for(;usrId<5;usrId++){
			
			User usr = new User(usrId);
			List<Matrix> mList= dao.findByUsr(usr);
		
//			System.out.println("************************testFindByUsr***************************");
//			System.out.println("usrId: "+ usrId);
			for(int i=1;i<= mList.size();i++){
//				System.out.println("Matrix "+i+"  itemId: "+mList.get(i-1).getMatrixId());
				assertEquals(mList.get(i-1).getUser().getUsrId(),usrId);
			}
		}
		

	}

	@Test
	public void testFindById() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		MatrixDAO dao = (MatrixDAO) ctx.getBean("MatrixDAO");
		
		int matrixId = 1;
		Matrix instance = dao.findById(matrixId);
		
		assertEquals(instance.getMatrixId(),matrixId);
		System.out.println("\n\n\n************************testFindById***************************");
	}

	@Test
	public void testGetFilterUserSet() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		MatrixDAO dao = (MatrixDAO) ctx.getBean("MatrixDAO");
		System.out.println("\n\n\n************************testGetFilterItem***************************");
		
		int usrId = 1;
		User usr = new User(usrId);
		List<Matrix> mList = dao.getFilterUserSet(new User(1),
				new User(5000)
		);
		
		System.out.println("mList Size: "+mList.size());
		
		for(int i = 0; i < mList.size();i++){
			System.out.println("User "+(i+1)+"  Id:"+mList.get(i).getUser().getUsrId());
		}
	}

	
	@Test
	public void testGetItemmatrixByUser() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		MatrixDAO dao = (MatrixDAO) ctx.getBean("MatrixDAO");
		System.out.println("\n\n\n************************testGetFilterItem***************************");
		
		User usr =  new User(1);
		Itemmatrix item= dao.getItemmatrixByUser(usr);
		
		System.out.println(item.getContext());
	}
	
	
	
	
	
	
	
	
}
