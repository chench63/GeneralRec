package cfa.impl;

import java.util.ArrayList;
import java.util.List;

import vo.Matrix;

import cfa.IComparison;
import cfa.Entity;

public class CosixEva implements IComparison {
	private double standard = 0.9;
	private List res = new ArrayList();
	
	public List evaluating(List row, List res) {
		
		for (int i=0;i<row.size();i++){
			if ( standard-(Double)res.get(i) < 0 )
				entityProc( (Entity)row.get(i));
		}
		return this.res;
	}

	public void entityProc(Entity instance){
		res.add(instance.getRow().get(0).getUser().getUsrId());
	}
	
	public List getList(){
		return res;
	}
}
