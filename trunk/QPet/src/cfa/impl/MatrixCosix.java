package cfa.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.Math;

import vo.Matrix;

import cfa.ICalculator;
import cfa.Entity;


public class MatrixCosix implements ICalculator {
	private double[] qTable={0,1,4,9,16,25,
			36,49,64,81,100};
	
	public List<Double> calculating(List row) {
		Entity base = (Entity) row.get(1);
		List<Double> instance = new ArrayList<Double>();
		
		for (Iterator<Entity> iter= row.iterator();iter.hasNext();){
			Entity eachEnt= (Entity)iter.next();
			double res;
			try{
				res=vectorMul(base, eachEnt) / vectorMode(base,eachEnt);
				
				System.out.println("vectorMul: "+vectorMul(base, eachEnt)+
						"   vectorMode: "+vectorMode(base,eachEnt));
			}
			catch(Exception e){
				e.printStackTrace();
				res = 0.0;
			}
			instance.add((Double) res);
		}
		
		return instance;
	}

	public double vectorMul(Entity base, Entity each){
		List<Matrix> bList = base.getRow();
		List<Matrix> eList = each.getRow();
		
		double res= 0.0;
		for (int i=0;i< bList.size();i++){
			res += bList.get(i).getScore() * eList.get(i).getScore();
		}
		return res;
	}
	
	public double vectorMode(Entity base, Entity each){
		List<Matrix> bList = base.getRow();
		List<Matrix> eList = each.getRow();
		
//		System.out.println(eList.size());
		
		double bTmp = 0.0;
		double eTmp = 0.0;
		for (int i=0;i< bList.size();i++){
			bTmp += qTable[bList.get(i).getScore()];
			eTmp += qTable[eList.get(i).getScore()];
		}
		
		return Math.sqrt(eTmp*eTmp);
	}
	
}
