package vo;

import java.util.HashSet;
import java.util.Set;

/**
 * Serviceitem entity. @author MyEclipse Persistence Tools
 */

public class Serviceitem implements java.io.Serializable {

	// Fields

	private Integer serviceItemId;
	private User user;
	private Itemlib itemlib;
	private String ext;
	private Set usrfrontinfos = new HashSet(0);

	// Constructors

	/** default constructor */
	public Serviceitem() {
	}

	/** full constructor */
	public Serviceitem(User user, Itemlib itemlib, String ext, Set usrfrontinfos) {
		this.user = user;
		this.itemlib = itemlib;
		this.ext = ext;
		this.usrfrontinfos = usrfrontinfos;
	}

	// Property accessors

	public Integer getServiceItemId() {
		return this.serviceItemId;
	}

	public void setServiceItemId(Integer serviceItemId) {
		this.serviceItemId = serviceItemId;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Itemlib getItemlib() {
		return this.itemlib;
	}

	public void setItemlib(Itemlib itemlib) {
		this.itemlib = itemlib;
	}

	public String getExt() {
		return this.ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Set getUsrfrontinfos() {
		return this.usrfrontinfos;
	}

	public void setUsrfrontinfos(Set usrfrontinfos) {
		this.usrfrontinfos = usrfrontinfos;
	}

}