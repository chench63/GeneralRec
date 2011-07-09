package dao;

import vo.Itemmatrix;
 
public interface IItemMatrixDAO {
	public Itemmatrix findById(java.lang.Integer id);
	public void saveItemMatrix(Itemmatrix instance);
	public int getMaxId();
}
