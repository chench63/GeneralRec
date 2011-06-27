package bo;

import service.impl.*;
import service.*;
import vo.*;
 
public class petManager { 
	public IUsrFrontInfoService usrFrontInfoService;
	public IPetService petService;
	public IMatrixService matrixService;
	public IItemLibService itemLibService;
		
	public IUsrFrontInfoService getUsrFrontInfoService() {
		return usrFrontInfoService;
	}

	public void setUsrFrontInfoService(IUsrFrontInfoService usrFrontInfoService) {
		this.usrFrontInfoService = usrFrontInfoService;
	}

	public IPetService getPetService() {
		return petService;
	}

	public void setPetService(IPetService petService) {
		this.petService = petService;
	}

	public IMatrixService getMatrixService() {
		return matrixService;
	}

	public void setMatrixService(IMatrixService matrixService) {
		this.matrixService = matrixService;
	}

	public IItemLibService getItemLibService() {
		return itemLibService;
	}

	public void setItemLibService(IItemLibService itemLibService) {
		this.itemLibService = itemLibService;
	}

	
	
	
	
	
	//___________________Main Method__________________________________________________	
	
	/*
	 * Get a control Object for every User.
	 * 	You can treat as a handle.
	 * */
	public Usrfrontinfo GetInstance(String token){
		User usr= new User();
		usr.setUsrToken(token);
		return usrFrontInfoService.GetInstanceByUsr(usr);
	}
	/*
	 * Get common information of a Pet
	 * */
	public Petlib GetDefaultPet(Usrfrontinfo info){
		return petService.getPetItem(info);
	} 
	
	/*
	 * Get detail information of a pet relating to the user
	 * */
	public Servicepet GetDefaltServicePetItem(Usrfrontinfo info){
		return petService.getServicepet(info);
	}
	
	
	/*
	 * Getter and Closer is a couple operation.
	 * 
	 * */
	public void CloseServicePetItem(Servicepet servicepet){
		petService.updateServicePet(servicepet);
	}
	
	/*
	 * Get different Item every time.
	 * Output:
	 * Null    No More Items
	 **/
	public Itemmatrix GetRatingItem(Usrfrontinfo info){
		return matrixService.getMatrixItemByUserInfo(info);
	}
	
	
	/*
	 * Getter and Closer is a couple operation
	 **/
	public void CloseRatingItem(Usrfrontinfo info, Integer score){
		Matrix instance= new Matrix();
		instance.setUser(info.getUser());
		instance.setScore(score);
		instance.setItemmatrix(info.getItemmatrix());
		
		matrixService.saveMatrix(instance);
	}
	
	/*
	 * Get different Item every time
	 * */
	public Itemlib GetInterestingItem(Usrfrontinfo info){
		return itemLibService.getItemByUsr(info);
	}
	
	/*
	 * Store a Item that user inputs.
	 * */
	public void AppendInterestingItem(Itemlib item, Usrfrontinfo info){
		itemLibService.saveItem(item, info.getUser());
	}
	
}
