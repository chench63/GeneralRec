package bo;

import service.impl.*;
import service.*;
import vo.*;
 
public class petManager { 
	public IUsrFrontInfoService usrFrontInfoService;
	public IPetService petService;
	public IMatrixService matrixService;
	public IItemLibService itemLibService;
	public IUserService userService;
		
	
	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

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
	 * Register new User Information
	 * 
	 * User class including attr:
	 * 	1) usrtoken
	 * */
	
	private Usrfrontinfo Register(User instance){
		//Save Table User Info
		userService.save(instance);
		int usrId = instance.getUsrId();
		User usr = new User(usrId);
		
		//Save Table ServicePet Info
		Servicepet sp = new Servicepet();
		Petlib pl = new Petlib();
		int petId = petService.getInitPetId();
		pl.setPetId(petId);
		int exp = 0;
		int level = 0;
		sp.setExp(exp);
		sp.setLevel(level);
		sp.setPetlib(pl);
		sp.setUser(usr);
		petService.saveOrUpdateServicePet(sp);
		
		//Save Table UsrFrontInfo Info
		
		
		System.out.println("Message From petManager  usrId: "+usrId);
		
		
		int servicePetId = petService.findByUser( new User(usrId) );
		int itemId = 0 ;
		Usrfrontinfo newUser=  new Usrfrontinfo();
		
		newUser.setServiceItemId(itemId);
		newUser.setServicePetId(servicePetId);
		newUser.setUsrId(usrId);
		newUser.setItemId(itemId);
		
		usrFrontInfoService.saveOrUpdate(newUser);
		return newUser;
	}
	
	
	/*
	 * Get a control Object for every User.
	 * 	You can treat as a handle.
	 * */
	public Usrfrontinfo GetInstance(String token){
		
		User usr= new User();
		usr.setUsrToken(token);
		if ( userService.checkExist(usr))
			return usrFrontInfoService.GetInstanceByUsr(usr);
		else
			return this.Register(usr);
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
		petService.saveOrUpdateServicePet(servicepet);
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
		User usr= new User();
		usr.setUsrId(info.getUsrId());
		instance.setUser(usr);
		instance.setScore(score);
		
		Itemmatrix item= new Itemmatrix();
		item.setItemId(info.getItemId());
		instance.setItemmatrix(item);
		
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
	public boolean AppendInterestingItem(Itemlib item, Usrfrontinfo info){
		User usr= new User();
		
		usr.setUsrId(info.getUsrId());
		if (itemLibService.checkExist(item))
			return false;
		else
			itemLibService.saveItem(item, usr);
		return true;
	}
	
}
