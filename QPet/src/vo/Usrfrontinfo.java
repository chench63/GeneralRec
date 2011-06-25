package vo;

/**
 * Usrfrontinfo entity. @author MyEclipse Persistence Tools
 */

public class Usrfrontinfo implements java.io.Serializable {

	// Fields

	private Integer frontId;
	private Servicepet servicepet;
	private Serviceitem serviceitem;
	private User user;
	private Itemmatrix itemmatrix;

	// Constructors

	/** default constructor */
	public Usrfrontinfo() {
	}

	/** full constructor */
	public Usrfrontinfo(Servicepet servicepet, Serviceitem serviceitem,
			User user, Itemmatrix itemmatrix) {
		this.servicepet = servicepet;
		this.serviceitem = serviceitem;
		this.user = user;
		this.itemmatrix = itemmatrix;
	}

	// Property accessors

	public Integer getFrontId() {
		return this.frontId;
	}

	public void setFrontId(Integer frontId) {
		this.frontId = frontId;
	}

	public Servicepet getServicepet() {
		return this.servicepet;
	}

	public void setServicepet(Servicepet servicepet) {
		this.servicepet = servicepet;
	}

	public Serviceitem getServiceitem() {
		return this.serviceitem;
	}

	public void setServiceitem(Serviceitem serviceitem) {
		this.serviceitem = serviceitem;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Itemmatrix getItemmatrix() {
		return this.itemmatrix;
	}

	public void setItemmatrix(Itemmatrix itemmatrix) {
		this.itemmatrix = itemmatrix;
	}

}