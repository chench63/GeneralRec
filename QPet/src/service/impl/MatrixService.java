package service.impl;


import dao.IItemMatrixDAO;
import dao.IUsrFrontInfoDAO;
import dao.IMatrixDAO;


import service.IMatrixService;
import vo.Itemmatrix;
import vo.Matrix;
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
		int itemId = usr.getItemId();
		System.out.println(itemId);
		Itemmatrix item= itemMatrixDAO.findById(itemId);
		//Itemmatrix item= itemMatrixDAO.findById(usr.getUser().getUsrId());
		
		
		Itemmatrix nextItem= new Itemmatrix();
		nextItem.setItemId (
				(item.getItemId()+1)%31
				);
		
		usr.setItemId(nextItem.getItemId());
		usrFrontInfoDAO.updateUsrFrontInfo(usr);
		
		if (item.getItemId() == 0)
			return null;
		else
			return item;
	}
	
	public void saveMatrix(Matrix instance){
		matrixDAO.saveMatrix(instance);
	}
	
	
}
