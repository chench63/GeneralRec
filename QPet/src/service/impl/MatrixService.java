package service.impl;


import dao.IItemMatrixDAO;
import dao.IUsrFrontInfoDAO;
import dao.IMatrixDAO;


import service.IMatrixService;
import vo.Itemmatrix;
import vo.Matrix;
import vo.User;
import vo.Usrfrontinfo;
 
public class MatrixService implements IMatrixService{
	private IItemMatrixDAO itemMatrixDAO;
	private IUsrFrontInfoDAO usrFrontInfoDAO;
	private IMatrixDAO matrixDAO;
	
	public IMatrixDAO getMatrixDAO() {
		return matrixDAO;
	}

	public void setMatrixDAO(IMatrixDAO matrixDAO) {
		this.matrixDAO = matrixDAO;
	}

	public IItemMatrixDAO getItemMatrixDAO() {
		return itemMatrixDAO;
	}

	public void setItemMatrixDAO(IItemMatrixDAO itemMatrixDAO) {
		this.itemMatrixDAO = itemMatrixDAO;
	}

	public IUsrFrontInfoDAO getUsrFrontInfoDAO() {
		return usrFrontInfoDAO;
	}

	public void setUsrFrontInfoDAO(IUsrFrontInfoDAO usrFrontInfoDAO) {
		this.usrFrontInfoDAO = usrFrontInfoDAO;
	}

	
	
	/*
	 * Service Method
	 * */
	public Itemmatrix getMatrixItemByUserInfo(Usrfrontinfo usr){	
		Itemmatrix item=  matrixDAO.getItemmatrixByUser( new User(usr.getUsrId()) );
		
		if (item == null)
			return null;
		
		usr.setItemId(item.getItemId());
		return item;
	}
	
	public void saveMatrix(Matrix instance){
		matrixDAO.saveMatrix(instance);
	}
	
	
}
