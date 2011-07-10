package cfa;

import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cfa.ItemManager;
import cfa.ICalculator;
import cfa.IComparison;

import cfa.impl.*;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
		
		ICalculator iCalculator = new MatrixCosix();
		IComparison iComparison = new CosixEva();
		ItemManager itemManager = (ItemManager) ctx.getBean("ItemManager");
		itemManager.init();
		
		List<Entity> uList = itemManager.getList();
		if (uList != null){
			
			for(int i=0;i< uList.size();i++){
				System.out.println("The Size:"+uList.get(i).getRow().size());
			}
			
			
			List<Double> res= iCalculator.calculating(uList);
			System.out.println("*******************Comparision ResultSet*********");
			for (Iterator<Double> iter=res.iterator();iter.hasNext();){
				System.out.println(iter.next());
			}
			
			List reSet=iComparison.evaluating(uList, res);
			
			
			System.out.println("Filter List: "+reSet.size());
			
			for(Iterator<Integer> iter = reSet.iterator();iter.hasNext();)
				System.out.println(iter.next());
//			itemManager.saveList(uList);
		}
		
	}

}
