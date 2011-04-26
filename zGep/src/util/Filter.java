package util;

/**
 * 滤波器
 */
public class Filter
{
	private double frequency;				// 低通滤波器的上限频率 (全部通过为1.0)
	
	public Filter(double frequency)
	{
		this.frequency = frequency;	
	}
	
	/**
	 * 对数据进行滤波(利用傅立叶变换) 
	 */
	public void filter(double[] data)
	{
		if (frequency>=1.0) return;
		
		int size = 1;
		while (size<data.length)
		{
			size <<= 1;
		}
		FFT fft = new FFT(size);
		
		// 准备数据并进行傅立叶正变换
		double[] xr = new double[size];
		double[] xi = new double[size];
		for (int i=0; i<data.length; i++)
		{
			xr[i] = data[i];
		}
		fft.fft(xr, xi);
		
		// 将高频部分滤掉
		int m = (int) (size * frequency);
		for (int i=m; i<size-m; i++)
		{
			xr[i] = 0.0;
			xi[i] = 0.0;
		}
		
		// 傅立叶反变换，并取出数据
		fft.ifft(xr, xi);
		for (int i=0; i<data.length; i++)
		{
			data[i] = xr[i];
		}
	}
}
