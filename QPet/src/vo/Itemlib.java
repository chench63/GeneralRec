package vo;

import java.util.HashSet;
import java.util.Set;

/**
 * Itemlib entity. @author MyEclipse Persistence Tools
 */

public class Itemlib implements java.io.Serializable {

	// Fields

	private Integer itemId;
	private String imgUrl;
	private String context;
	private String webUrl;
	private String ext;
	private Set serviceitems = new HashSet(0);

	// Constructors

	/** default constructor */
	public Itemlib() {
	}

	/** full constructor */
	public Itemlib(String imgUrl, String context, String webUrl, String ext,
			Set serviceitems) {
		this.imgUrl = imgUrl;
		this.context = context;
		this.webUrl = webUrl;
		this.ext = ext;
		this.serviceitems = serviceitems;
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

	public String getExt() {
		return this.ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Set getServiceitems() {
		return this.serviceitems;
	}

	public void setServiceitems(Set serviceitems) {
		this.serviceitems = serviceitems;
	}

}