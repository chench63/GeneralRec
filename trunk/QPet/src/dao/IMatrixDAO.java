package dao;

import java.util.List; 

import vo.Itemmatrix;
import vo.Matrix;
import vo.User;

public interface IMatrixDAO {
	public void saveMatrix(Matrix instance);
	public List<Matrix> findByUsr(User instance);
	public List<Matrix> getFilterUserSet(User lUser, User hUser);
	public Itemmatrix getItemmatrixByUser(User usr);
	public boolean checkExist( Matrix instance );
}
