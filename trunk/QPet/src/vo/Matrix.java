package vo;

/**
 * Matrix entity. @author MyEclipse Persistence Tools
 */

public class Matrix implements java.io.Serializable {

	// Fields

	private Integer matrixId;
	private User user;
	private Itemmatrix itemmatrix;
	private Integer score;

	// Constructors

	/** default constructor */
	public Matrix() {
	}

	/** full constructor */
	public Matrix(User user, Itemmatrix itemmatrix, Integer score) {
		this.user = user;
		this.itemmatrix = itemmatrix;
		this.score = score;
	}

	// Property accessors

	public Integer getMatrixId() {
		return this.matrixId;
	}

	public void setMatrixId(Integer matrixId) {
		this.matrixId = matrixId;
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

	public Integer getScore() {
		return this.score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

}