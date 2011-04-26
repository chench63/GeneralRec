package ga;

/**
 * 模型测试器
 */
public interface Tester
{
	/**
	 * 进行测试
	 * @param file 测试的输出结果文件
	 */
	public double test(Protein protein) throws Exception;
}
