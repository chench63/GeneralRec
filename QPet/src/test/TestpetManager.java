package test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import vo.Itemlib;
import vo.Itemmatrix;
import vo.Petlib;
import vo.Servicepet;
import vo.Usrfrontinfo;

import bo.petManager;
 
public class TestpetManager {

	@Test
	public void testGetInstance() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		petManager pet = (petManager) ctx.getBean("petManager");
		
		String token="LXUGQ2YXP NCKVSU7G5QIMKI LON2DT P02WAXI4PPCLT70SLWSCF6X078Y68LENMRR0H57KBIVO5F6PFO3ORCOG3DYLGB8Y37BYY18RD79XUTTBV" +
		"MXGTJV0F5O68E5TPY3VN4FJI6IJKR0PV5774B29M952BTFKQ9JRR 362HNO6UAE5FUDKFC714FCPOWVA31IRH2F9US" +
		"BWQRB65SD9QB7OSM0S90XA030EJRC1O0E5C4S8S1HK853GYUX32L";
		
		Usrfrontinfo info = pet.GetInstance(token);
		System.out.println("UsrId: "+info.getUser().getUsrId());
		//assertEquals(info.getUser().getUsrToken(),token);
		
	}

	@Test
	public void testGetDefaultPet() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		petManager pet = (petManager) ctx.getBean("petManager");
		
		String token="LXUGQ2YXP NCKVSU7G5QIMKI LON2DT P02WAXI4PPCLT70SLWSCF6X078Y68LENMRR0H57KBIVO5F6PFO3ORCOG3DYLGB8Y37BYY18RD79XUTTBV" +
		"MXGTJV0F5O68E5TPY3VN4FJI6IJKR0PV5774B29M952BTFKQ9JRR 362HNO6UAE5FUDKFC714FCPOWVA31IRH2F9US" +
		"BWQRB65SD9QB7OSM0S90XA030EJRC1O0E5C4S8S1HK853GYUX32L";
		
		Usrfrontinfo info = pet.GetInstance(token);
		Petlib lib= pet.GetDefaultPet(info);
		System.out.println("\n\n\n************************GetDefaultPet**********");
		System.out.println("PetId: "+lib.getPetId());
		System.out.println("SrcUrl: "+lib.getSrcUrl());
	}

	@Test
	public void testGetDefaltServicePetItem() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		petManager pet = (petManager) ctx.getBean("petManager");
		
		String token="LXUGQ2YXP NCKVSU7G5QIMKI LON2DT P02WAXI4PPCLT70SLWSCF6X078Y68LENMRR0H57KBIVO5F6PFO3ORCOG3DYLGB8Y37BYY18RD79XUTTBV" +
		"MXGTJV0F5O68E5TPY3VN4FJI6IJKR0PV5774B29M952BTFKQ9JRR 362HNO6UAE5FUDKFC714FCPOWVA31IRH2F9US" +
		"BWQRB65SD9QB7OSM0S90XA030EJRC1O0E5C4S8S1HK853GYUX32L";
		
		Usrfrontinfo info = pet.GetInstance(token);
		Servicepet serPet= pet.GetDefaltServicePetItem(info);
		System.out.println("\n\n\n************************GetDefaultServicePetItem**********");
		System.out.println("ServicePetId: "+serPet.getServicePetId());
		System.out.println("UsrId: "+serPet.getUser().getUsrId());
		System.out.println("PetId: "+serPet.getPetlib().getPetId());
		System.out.println("Level: "+serPet.getLevel());
	}

	@Test
	public void testCloseServicePetItem() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetRatingItem() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		petManager pet = (petManager) ctx.getBean("petManager");
		
		String token="LXUGQ2YXP NCKVSU7G5QIMKI LON2DT P02WAXI4PPCLT70SLWSCF6X078Y68LENMRR0H57KBIVO5F6PFO3ORCOG3DYLGB8Y37BYY18RD79XUTTBV" +
		"MXGTJV0F5O68E5TPY3VN4FJI6IJKR0PV5774B29M952BTFKQ9JRR 362HNO6UAE5FUDKFC714FCPOWVA31IRH2F9US" +
		"BWQRB65SD9QB7OSM0S90XA030EJRC1O0E5C4S8S1HK853GYUX32L";
		
		Usrfrontinfo info = pet.GetInstance(token);
		Itemmatrix item=pet.GetRatingItem(info);
		System.out.println("\n\n\n************************GetRatingItem**********");
		System.out.println("ItemId: "+item.getItemId());
		System.out.println("ImgUrl: "+item.getImgUrl());
		System.out.println("WebUrl: "+item.getWebUrl());
		System.out.println("Context: "+item.getContext());
		
	}

	@Test
	public void testCloseRatingItem() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		petManager pet = (petManager) ctx.getBean("petManager");
		
		String token="LXUGQ2YXP NCKVSU7G5QIMKI LON2DT P02WAXI4PPCLT70SLWSCF6X078Y68LENMRR0H57KBIVO5F6PFO3ORCOG3DYLGB8Y37BYY18RD79XUTTBV" +
		"MXGTJV0F5O68E5TPY3VN4FJI6IJKR0PV5774B29M952BTFKQ9JRR 362HNO6UAE5FUDKFC714FCPOWVA31IRH2F9US" +
		"BWQRB65SD9QB7OSM0S90XA030EJRC1O0E5C4S8S1HK853GYUX32L";
		
		Usrfrontinfo info = pet.GetInstance(token);
		int score = 10;
		pet.CloseRatingItem(info, score);
		System.out.println("\n\n\n************************CloseRatingItem**********");
	}

	@Test
	public void testGetInterestingItem() {
		//fail("Not yet implemented");
	}

	@Test
	public void testAppendInterestingItem() {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		petManager pet = (petManager) ctx.getBean("petManager");
		
		String token="LXUGQ2YXP NCKVSU7G5QIMKI LON2DT P02WAXI4PPCLT70SLWSCF6X078Y68LENMRR0H57KBIVO5F6PFO3ORCOG3DYLGB8Y37BYY18RD79XUTTBV" +
		"MXGTJV0F5O68E5TPY3VN4FJI6IJKR0PV5774B29M952BTFKQ9JRR 362HNO6UAE5FUDKFC714FCPOWVA31IRH2F9US" +
		"BWQRB65SD9QB7OSM0S90XA030EJRC1O0E5C4S8S1HK853GYUX32L";
		
		Usrfrontinfo info = pet.GetInstance(token);
		Itemlib item = new Itemlib();
		item.setContext("Test");
		item.setExt("Test");
		item.setImgUrl("Test");
		item.setWebUrl("Test");
		pet.AppendInterestingItem(item, info);
		System.out.println("\n\n\n************************AppendInterestingItem**********");
	}

}
