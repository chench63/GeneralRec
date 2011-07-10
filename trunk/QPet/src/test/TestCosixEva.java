package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.junit.Test;

import vo.Matrix;
import vo.User;

import cfa.Entity;
import cfa.impl.CosixEva;



public class TestCosixEva {

	@Test
	public void testEvaluating() {
		CosixEva ce = new CosixEva();
		
		List<Entity> eList = new ArrayList();
		List<Double> dList = new ArrayList();
		for(int i = 0;i<100;i++){
			Entity instance = new Entity();
			List<Matrix> mList = new ArrayList();
			Matrix tmp = new Matrix();
			tmp.setMatrixId(i);
			tmp.setScore(i);
			tmp.setUser(new User(i));
			mList.add(tmp);
			instance.setRow(mList);
			eList.add(instance);
			dList.add(i/100.0);
		}
		
		List<Integer> res = ce.evaluating(eList, dList);
		
		int target = 90;
		int count = 1;
		for(Iterator<Integer> iter= res.iterator();iter.hasNext();){
			assertEquals(iter.next(),
					target+ count++
					);
		}
		
		
	}

	@Test
	public void testEntityProc() {
		CosixEva ce = new CosixEva();
		
		for(int i=0;i<10;i++){
			Entity instance = new Entity();
			List<Matrix> mList = new ArrayList();
			Matrix tmp = new Matrix();
			tmp.setMatrixId(i);
			tmp.setScore(i);
			tmp.setUser(new User(i));
			mList.add(tmp);
			instance.setRow(mList);
			ce.entityProc(instance);
		}
		
		for(int i=0;i< ce.getList().size();i++){
			assertEquals(ce.getList().get(i),i);
		}
		
	}

}
