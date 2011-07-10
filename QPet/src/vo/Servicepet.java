package vo;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicepet entity. @author MyEclipse Persistence Tools
 */

public class Servicepet implements java.io.Serializable {

	// Fields

	private Integer servicePetId;
	private Petlib petlib;
	private User user;
	private Integer level;
	private Integer exp;
	private String ext;
	private Set usrfrontinfos = new HashSet(0);

	// Constructors

	/** default constructor */
	public Servicepet() {
	}

	/** full constructor */
	public Servicepet(Petlib petlib, User user, Integer level, Integer exp,
			String ext, Set usrfrontinfos) {
		this.petlib = petlib;
		this.user = user;
		this.level = level;
		this.exp = exp;
		this.ext = ext;
		this.usrfrontinfos = usrfrontinfos;
	}

	// Property accessors

	public Integer getServicePetId() {
		return this.servicePetId;
	}

	public void setServicePetId(Integer servicePetId) {
		this.servicePetId = servicePetId;
	}

	public Petlib getPetlib() {
		return this.petlib;
	}

	public void setPetlib(Petlib petlib) {
		this.petlib = petlib;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getExp() {
		return this.exp;
	}

	public void setExp(Integer exp) {
		this.exp = exp;
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