package ga;


/**
 * 解码器 
 * 
 * 将编码空间中的符号转换到问题空间中的实际的冬冬
 */
public interface Decoder
{
	/**
	 * 重置解码器，准备开始解码下一条染色体
	 */
	public void reset();

	/**
	 * 解码
	 * @param chromosome
	 * @return
	 */
	public Protein decode(Chromosome chromosome);

}
