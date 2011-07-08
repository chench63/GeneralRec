package vo;

import java.util.HashSet;
import java.util.Set;

/**
 * User entity. @author MyEclipse Persistence Tools
 */

public class User implements java.io.Serializable {

	// Fields

	private Integer usrId;
	private String usrToken;
	private String uext1;
	private String uext2;
	private Set matrixes = new HashSet(0);
	private Set serviceitems = new HashSet(0);
	private Set usrfrontinfos = new HashSet(0);
	private Set servicepets = new HashSet(0);

	// Constructors

	/** default constructor */
	public User() {
	}
	
	public User(int id) {
		usrId =id;
	}

	/** full constructor */
	public User(String usrToken, String uext1, String uext2, Set matrixes,
			Set serviceitems, Set usrfrontinfos, Set servicepets) {
		this.usrToken = usrToken;
		this.uext1 = uext1;
		this.uext2 = uext2;
		this.matrixes = matrixes;
		this.serviceitems = serviceitems;
		this.usrfrontinfos = usrfrontinfos;
		this.servicepets = servicepets;
	}

	// Property accessors

	public Integer getUsrId() {
		return this.usrId;
	}

	public void setUsrId(Integer usrId) {
		this.usrId = usrId;
	}

	public String getUsrToken() {
		return this.usrToken;
	}

	public void setUsrToken(String usrToken) {
		this.usrToken = usrToken;
	}

	public String getUext1() {
		return this.uext1;
	}

	public void setUext1(String uext1) {
		this.uext1 = uext1;
	}

	public String getUext2() {
		return this.uext2;
	}

	public void setUext2(String uext2) {
		this.uext2 = uext2;
	}

	public Set getMatrixes() {
		return this.matrixes;
	}

	public void setMatrixes(Set matrixes) {
		this.matrixes = matrixes;
	}

	public Set getServiceitems() {
		return this.serviceitems;
	}

	public void setServiceitems(Set serviceitems) {
		this.serviceitems = serviceitems;
	}

	public Set getUsrfrontinfos() {
		return this.usrfrontinfos;
	}

	public void setUsrfrontinfos(Set usrfrontinfos) {
		this.usrfrontinfos = usrfrontinfos;
	}

	public Set getServicepets() {
		return this.servicepets;
	}

	public void setServicepets(Set servicepets) {
		this.servicepets = servicepets;
	}

}