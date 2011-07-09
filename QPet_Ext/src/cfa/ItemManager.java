package cfa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vo.Matrix;
import vo.User;
import dao.IMatrixDAO;
import dao.IServiceItemDAO;
import dao.IUserDAO;
import dao.impl.MatrixDAO;
import dao.impl.ServiceItemDAO;
import dao.impl.UserDAO;
import cfa.Entity;

public class ItemManager {
	private int populationNum = 100;  /*The population Num in a field*/
	private int CUId = 10;				  /*The current UserId Default: 0 */
	private int UIdMax = 100;				  /*The max UserId*/
	private int fieldNum = 10;		  /*The Number of people's flavor*/
	private int CfNum =1;
	private int step = 1;
	
	public IMatrixDAO iMatrixDAO;
	public IServiceItemDAO iServiceItemDAO;
	public IUserDAO iUserDAO;
	
	public ItemManager(){
//		iMatrixDAO = new MatrixDAO();
//		iServiceItemDAO = new ServiceItemDAO();
//		iUserDAO = new UserDAO();
//		UIdMax = iUserDAO.getMaxId();
	}
	
	public IMatrixDAO getiMatrixDAO() {
		return iMatrixDAO;
	}

	public void setiMatrixDAO(IMatrixDAO iMatrixDAO) {
		this.iMatrixDAO = iMatrixDAO;
	}

	public IServiceItemDAO getiServiceItemDAO() {
		return iServiceItemDAO;
	}

	public void setiServiceItemDAO(IServiceItemDAO iServiceItemDAO) {
		this.iServiceItemDAO = iServiceItemDAO;
	}

	public IUserDAO getiUserDAO() {
		return iUserDAO;
	}

	public void setiUserDAO(IUserDAO iUserDAO) {
		this.iUserDAO = iUserDAO;
	}
	
/*----------------------------------------------------------------
 * 
 * 			Main Method
 * 
 *---------------------------------------------------------------- */

	public List<Entity> getList(){
		try{
			List<Entity> res = new ArrayList();
			do{
				
				List<Matrix> tmp = iMatrixDAO.getFilterUserSet(
						new User(CUId) ,
						new User(CUId+step)
						);
				
				System.out.println("The message of ItemManager.getList .. List Size: "+ tmp.size() );
				
				for(Iterator<Matrix> iter = tmp.iterator();iter.hasNext();){
						User usr = new User( iter.next().getUser().getUsrId() );
						res.add( 
								getEntity(  usr  ) 
								);
						System.out.println("*usrId: "+ usr.getUsrId() );
				}
				
				
			}while( stop() ) ;
			
			return res;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	private Entity getEntity(User usr){
		Entity res = new Entity();
		res.setRow( 
				iMatrixDAO.findByUsr(usr)
				);
		System.out.println("********Loop");
		return res;
	}
	
	
	public void saveList(List<Entity> res){
		int usrId =  res.get(0).getRow().get(0).getUser().getUsrId();
		User uFather = iUserDAO.findById(usrId);
		String uToke = uFather.getUsrToken();
		
		List<User> uList = new ArrayList();
		
		for(Iterator<Entity> iter= res.iterator();iter.hasNext();){
			Entity tmp = (Entity) iter.next();
			int uId = tmp.getRow().get(0).getUser().getUsrId();
			User uSon =  iUserDAO.findById(uId);
			uSon.setUext1(uToke);
			uList.add(uSon);
		}
		
		for(Iterator<User> iter= uList.iterator();iter.hasNext();){
			iServiceItemDAO.mergeForCol(iter.next(), uToke);
		}
		
		for(Iterator<User> iter= uList.iterator();iter.hasNext();){
			iUserDAO.saveUser(iter.next());
		}
	}

	
	private boolean stop(){
		CUId += step;
		
		System.out.println("Stop...");
		
		
		if ( CUId >= UIdMax )
			return false;
		else
			return true;
			
	}
	
	public void init(){
		UIdMax = iUserDAO.getMaxId();
	}
}
