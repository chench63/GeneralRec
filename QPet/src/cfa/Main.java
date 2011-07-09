package cfa;

import java.util.List;

import cfa.ItemManager;
import cfa.ICalculator;
import cfa.IComparison;

import cfa.impl.*;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ICalculator iCalculator = new MatrixCosix();
		IComparison iComparison = new CosixEva();
		ItemManager itemManager = new ItemManager();
		
		List<Entity> uList = itemManager.getList();
		if (uList != null){
			
			for(int i=0;i< uList.size();i++){
				System.out.println("The Size:"+uList.get(i).getRow().size());
			}
			
			
			List<Double> res= iCalculator.calculating(uList);
			iComparison.evaluating(uList, res);
			itemManager.saveList(uList);
		}
		
	}

}
