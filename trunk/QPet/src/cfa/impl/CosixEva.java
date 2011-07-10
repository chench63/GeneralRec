package cfa.impl;

import java.util.ArrayList;
import java.util.List;

import vo.Matrix;

import cfa.IComparison;
import cfa.Entity;

public class CosixEva implements IComparison {
	private double standard = 0.9;
	private double stStep = 0.02;
	
	private List res = new ArrayList();
	private int population = 10;
	
	
	
	
	/*
	 * Evaluate the Matrix, then merge them who have the stable similarity
	 * 							 into a List.
	 * 
	 * Principle:
	 * 	 the Stand Line must  more than $standard,
	 * 	 the population of the them can't less than $population
	 * 
	 * 
	 **/
	public List evaluating(List row, List res) {
		double stLine = standard;
		
		do{
			this.res.clear();
			for (int i=0;i<row.size();i++){
				if ( standard-(Double)res.get(i) < 0 )
					entityProc( (Entity)row.get(i));
			}
			stLine -= stStep;
		}while( this.res.size() < population && stLine >= 0.7);
		
		return this.res;
	}

	public void entityProc(Entity instance){
		res.add(instance.getRow().get(0).getUser().getUsrId());
	}
	
	public List getList(){
		return res;
	}
}
