package vo;

import java.util.HashSet;
import java.util.Set;

/**
 * Petlib entity. @author MyEclipse Persistence Tools
 */

public class Petlib implements java.io.Serializable {

	// Fields

	private Integer petId;
	private String srcUrl;
	private String petExt;
	private String petExt1;
	private Set servicepets = new HashSet(0);

	// Constructors

	/** default constructor */
	public Petlib() {
	}

	/** full constructor */
	public Petlib(String srcUrl, String petExt, String petExt1, Set servicepets) {
		this.srcUrl = srcUrl;
		this.petExt = petExt;
		this.petExt1 = petExt1;
		this.servicepets = servicepets;
	}

	// Property accessors

	public Integer getPetId() {
		return this.petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public String getSrcUrl() {
		return this.srcUrl;
	}

	public void setSrcUrl(String srcUrl) {
		this.srcUrl = srcUrl;
	}

	public String getPetExt() {
		return this.petExt;
	}

	public void setPetExt(String petExt) {
		this.petExt = petExt;
	}

	public String getPetExt1() {
		return this.petExt1;
	}

	public void setPetExt1(String petExt1) {
		this.petExt1 = petExt1;
	}

	public Set getServicepets() {
		return this.servicepets;
	}

	public void setServicepets(Set servicepets) {
		this.servicepets = servicepets;
	}

}