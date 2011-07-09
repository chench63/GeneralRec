package vo;

/**
 * Usrfrontinfo entity. @author MyEclipse Persistence Tools
 */

public class Usrfrontinfo implements java.io.Serializable {

	// Fields

	private Integer frontId;
	private Integer serviceItemId;
	private Integer usrId;
	private Integer itemId;
	private Integer servicePetId;

	// Constructors

	/** default constructor */
	public Usrfrontinfo() {
	}

	/** full constructor */
	public Usrfrontinfo(Integer serviceItemId, Integer usrId, Integer itemId,
			Integer servicePetId) {
		this.serviceItemId = serviceItemId;
		this.usrId = usrId;
		this.itemId = itemId;
		this.servicePetId = servicePetId;
	}

	// Property accessors

	public Integer getFrontId() {
		return this.frontId;
	}

	public void setFrontId(Integer frontId) {
		this.frontId = frontId;
	}

	public Integer getServiceItemId() {
		return this.serviceItemId;
	}

	public void setServiceItemId(Integer serviceItemId) {
		this.serviceItemId = serviceItemId;
	}

	public Integer getUsrId() {
		return this.usrId;
	}

	public void setUsrId(Integer usrId) {
		this.usrId = usrId;
	}

	public Integer getItemId() {
		return this.itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getServicePetId() {
		return this.servicePetId;
	}

	public void setServicePetId(Integer servicePetId) {
		this.servicePetId = servicePetId;
	}

}