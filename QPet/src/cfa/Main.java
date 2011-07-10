package cfa;

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
		
		List<Entity> uList = itemManager.getList();
		if (uList != null){
			
			for(int i=0;i< uList.size();i++){
				System.out.println("The Size:"+uList.get(i).getRow().size());
			}
			
			
			List<Double> res= iCalculator.calculating(uList);
			
			
			
//			iComparison.evaluating(uList, res);
//			itemManager.saveList(uList);
		}
		
	}

}
