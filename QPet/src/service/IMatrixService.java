package service;

import vo.Itemmatrix;
import vo.Matrix;
import vo.Usrfrontinfo;
 
public interface IMatrixService {
	public Itemmatrix getMatrixItemByUserInfo(Usrfrontinfo usr);
	public void saveMatrix(Matrix instance);
	public boolean checkExist( Matrix instance );
}
