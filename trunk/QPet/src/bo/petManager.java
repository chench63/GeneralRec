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
		try{
			//Save Table User Info
			userService.save(instance);
			int usrId = instance.getUsrId();
			User usr = new User(usrId);
		
			//Save Table ServicePet Info
			Servicepet sp = new Servicepet();
			Petlib pl = new Petlib();
			int petId = petService.getInitPetId();	
			int exp = 0;
			int level = 0;
			
			pl.setPetId(petId);
			sp.setExp(exp);
			sp.setLevel(level);
			sp.setPetlib(pl);
			sp.setUser(usr);
			
			petService.saveOrUpdateServicePet(sp);
		
			//Save Table UsrFrontInfo Info
//			System.out.println("Message From petManager  usrId: "+usrId);
			int servicePetId = petService.findByUser( new User(usrId) );
			
			int itemId = 0 ;
			Usrfrontinfo newUser=  new Usrfrontinfo();
			newUser.setServiceItemId(itemId);
			newUser.setServicePetId(servicePetId);
			newUser.setUsrId(usrId);
			newUser.setItemId(itemId);
			usrFrontInfoService.saveOrUpdate(newUser);

			return newUser;
		}catch(Exception e){
			return null;
		}
		
	}
	
	/*
	 * Check whether it's a new Customer or not
	 * */
	public boolean isFresh(String token){
		try{
			User usr= new User();
			usr.setUsrToken(token);
			return !userService.checkExist( usr);
		}catch(Exception e){
			return false;
		}
	}
	
	
	/*
	 * Get a control Object for every User.
	 * 	You can treat as a handle.
	 * */
	public Usrfrontinfo GetInstance(String token){
		try{
			User usr= new User();
			usr.setUsrToken(token);
			if ( userService.checkExist(usr))
				return usrFrontInfoService.GetInstanceByUsr(usr);
			else
				return this.Register(usr);
		}catch(Exception e){
			return null;
		}
		
	}
	/*
	 * Get common information of a Pet
	 * */
	public Petlib GetDefaultPet(Usrfrontinfo info){
		try{
			return petService.getPetItem(info);
		}catch(Exception e){
			return null;
		}
		
	} 
	
	/*
	 * Get detail information of a pet relating to the user
	 * */
	public Servicepet GetDefaltServicePetItem(Usrfrontinfo info){
		try{
			return petService.getServicepet(info);
		}catch(Exception e){
			return null;
		}
		
	}
	
	
	/*
	 * Getter and Closer is a couple operation.
	 *   save the information of Servicepet 
	 * */
	public boolean CloseServicePetItem(Servicepet servicepet){
		try{
			petService.saveOrUpdateServicePet(servicepet);
			return true;
		}catch(Exception e){
			return false;
		}
		
	}
	
	/*
	 * Get different Item every time.
	 * Output:
	 * Null    No More Items
	 **/
	public Itemmatrix GetRatingItem(Usrfrontinfo info){
		try{
			Itemmatrix item = matrixService.getMatrixItemByUserInfo(info);
			return item;
		}catch(Exception e){
			return null;
		}
		
	}
	
	
	/*
	 * Getter and Closer is a couple operation
	 **/
	public boolean CloseRatingItem(Usrfrontinfo info, Integer score){
		try{
			Matrix instance= new Matrix();
			User usr= new User();
			usr.setUsrId(info.getUsrId());
			instance.setUser(usr);
			instance.setScore(score);
		
			Itemmatrix item= new Itemmatrix();
			item.setItemId(info.getItemId());
			instance.setItemmatrix(item);
			
			if( !matrixService.checkExist(instance)  )
				matrixService.saveMatrix(instance);
			return true;
		}catch(Exception e){
			return false;
		}
		
	}
	
	/*
	 * Get different Item every time
	 * */
	public Itemlib GetInterestingItem(Usrfrontinfo info){
		try{
			return itemLibService.getItemByUsr(info);
		}catch(Exception e){
			return null;
		}
		
	}
	
	/*
	 * Store a Item that user inputs.
	 * */
	public boolean AppendInterestingItem(Itemlib item, Usrfrontinfo info){
		try{
			User usr= new User();
			usr.setUsrId(info.getUsrId());
			if (itemLibService.checkExist(item))
				return false;
			else
				itemLibService.saveItem(item, usr);
		return true;
		}catch(Exception e){
			return false;
		}
		
	}
	
}
