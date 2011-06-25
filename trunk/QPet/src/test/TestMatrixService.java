package test;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import vo.Itemmatrix;
import vo.User;
import dao.impl.UsrFrontInfoDAO;
import vo.Usrfrontinfo;

import service.IMatrixService;
import service.impl.MatrixService;


public class TestMatrixService extends TestCase {
	
	@Test
	public void testGetMatrixItemByUserInfo() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		UsrFrontInfoDAO dao = (UsrFrontInfoDAO) ctx.getBean("UsrFrontInfoDAO");
		
		
		User user= new User();
		user.setUsrId(1);				
		Usrfrontinfo info= dao.findByUsr(user);
//		int iBase = 0;//info.getItemmatrix().getItemId();
		
		IMatrixService ms = (MatrixService) ctx.getBean("MatrixService");
		Itemmatrix im= ms.getMatrixItemByUserInfo(info);
		
		System.out.println("******************testGetMatrixItemByUserInfo********************");
		System.out.println("ItemId: "+im.getItemId());
		System.out.println("ImgUrl: "+im.getImgUrl());
		System.out.println("WebUrl: "+im.getWebUrl());
		System.out.println("Context: "+im.getContext());
		
		
//		for(int i= 1;i<10;i++){
//			assertEquals(  (Integer)(i+info.getItemmatrix().getItemId()),
//					im.getItemId());
//			info= dao.findByUsr(user);
//			im= ms.getMatrixItemByUserInfo(info);
//			System.out.println((i+iBase)+"   "+im.getItemId());
//		}
		
	}

	@Test
	public void testSaveMatrix() {
		
	}

}
