package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import vo.Matrix;
import vo.User;

import cfa.Entity;
import cfa.impl.MatrixCosix;

public class TestMatrixCosix {

	@Test
	public void testCalculating() {
		MatrixCosix mc = new MatrixCosix();
		List<Entity> eList = new ArrayList<Entity>();
		
		
		List<Matrix> mList = new ArrayList<Matrix>();
		Entity tmp = new Entity();
		for(int j=0;j <10;j++){
			Matrix mTmp = new Matrix();
			mTmp.setScore(j);
			mList.add(mTmp);
		}
		tmp.setRow(mList);
		
		for(int i=0;i <10;i++){
			eList.add(tmp);
		}
		
		List<Double> res = mc.calculating(eList);
		
		System.out.println("*********************testCalculating******************************");
		for(Iterator<Double> iter= res.iterator();iter.hasNext();){
			System.out.println(iter.next());
			assertEquals(iter.next(),1.0);
		}
	}

//	@Test
//	public void testVectorMul() {
//		MatrixCosix mc = new MatrixCosix();
//		
//		Entity bInstance = new Entity();
//		Entity eInstance = new Entity();
//		
//		List<Matrix> bList = new ArrayList<Matrix>();
//		List<Matrix> eList = new ArrayList<Matrix>();
//	
//		double res = 0;
//		for(int i=0;i <10;i++){
//			Matrix tmp = new Matrix();
//			tmp.setScore(i);
//			
//			bList.add(tmp);
//			eList.add(tmp);
//			res += i*i; 
//		}
//		
//		bInstance.setRow(bList);
//		eInstance.setRow(eList);
//		
//		System.out.println("\n\n*********************testVectorMul******************************");
//		System.out.println(res);
//		assertEquals(mc.vectorMul(bInstance, eInstance), res);
//		
//	}
//
//	@Test
//	public void testVectorMode() {
//		MatrixCosix mc = new MatrixCosix();
//		
//		Entity bInstance = new Entity();
//		Entity eInstance = new Entity();
//		
//		List<Matrix> bList = new ArrayList();
//		List<Matrix> eList = new ArrayList();
//	
//		double res = 0.0;
//		for(int i=0;i <10;i++){
//			Matrix tmp = new Matrix();
//			tmp.setScore(i);
//			
//			bList.add(tmp);
//			eList.add(tmp);
//			res += i*i;
//		}
//		
//		bInstance.setRow(bList);
//		eInstance.setRow(eList);
//		
//		System.out.println("\n\n*********************testVectorMode******************************");
//		System.out.println(res);
//		assertEquals(mc.vectorMode(eInstance, eInstance),res);
//	}

}
