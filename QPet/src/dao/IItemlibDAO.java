package dao;
 
import vo.Itemlib;

public interface IItemlibDAO {
	public void saveItemlib(Itemlib itemlib);
	public boolean checkExist(Itemlib itemlib);
}
