package test;

import static org.junit.Assert.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import vo.Itemmatrix;
import vo.User;
import dao.impl.UsrFrontInfoDAO;
import vo.Usrfrontinfo;

import service.impl.MatrixService;
import org.junit.Test;

public class TestUsrFrontInfoService {
	
	@Test
	public void testGetInstanceByUsr() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		UsrFrontInfoDAO dao = (UsrFrontInfoDAO) ctx.getBean("UsrFrontInfoDAO");
		
		User user= new User();
		user.setUsrId(1);
				
		Usrfrontinfo info=dao.findByUsr(user);
		MatrixService ms= (MatrixService) ctx.getBean("MatrixService");
		Itemmatrix item= ms.getMatrixItemByUserInfo(info);
		
		System.out.println(item.getItemId());
		System.out.println(item.getContext());
	}

}
