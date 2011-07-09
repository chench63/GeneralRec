package vo;

import java.util.HashSet;
import java.util.Set;

/**
 * Itemmatrix entity. @author MyEclipse Persistence Tools
 */

public class Itemmatrix implements java.io.Serializable {

	// Fields

	private Integer itemId;
	private String imgUrl;
	private String context;
	private String webUrl;
	private Set matrixes = new HashSet(0);
	private Set usrfrontinfos = new HashSet(0);

	// Constructors

	/** default constructor */
	public Itemmatrix() {
	}

	/** full constructor */
	public Itemmatrix(String imgUrl, String context, String webUrl,
			Set matrixes, Set usrfrontinfos) {
		this.imgUrl = imgUrl;
		this.context = context;
		this.webUrl = webUrl;
		this.matrixes = matrixes;
		this.usrfrontinfos = usrfrontinfos;
	}

	// Property accessors

	public Integer getItemId() {
		return this.itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getImgUrl() {
		return this.imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getWebUrl() {
		return this.webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public Set getMatrixes() {
		return this.matrixes;
	}

	public void setMatrixes(Set matrixes) {
		this.matrixes = matrixes;
	}

	public Set getUsrfrontinfos() {
		return this.usrfrontinfos;
	}

	public void setUsrfrontinfos(Set usrfrontinfos) {
		this.usrfrontinfos = usrfrontinfos;
	}

}