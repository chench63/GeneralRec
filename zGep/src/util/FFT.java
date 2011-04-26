package util;

/**
 * 快速傅立叶变换
 */
public class FFT
{
	private int N = -1; 				// The length of the "w" sequence
	private double[] coses, sines;
	private int[] swapIndex; 			// index of the swap of the items

	public FFT(int N)
	{
		this.N = N;

		coses = new double[N >> 1];
		sines = new double[N >> 1];
		initialize();

		swapIndex = new int[N];
		initializeSwapIndex();
	}

	/**
	 * 傅立叶正变换
	 * @param xr
	 * @param xi
	 */
	public void fft(double[] xr, double[] xi)
	{
//		assert xr.length == N;

		// 根据swapIndex交换输入数据的顺序，为蝶形算法做准备
		for (int i = 0; i < N; i++)
		{
			if (i < swapIndex[i])
			{
				double t;
				t = xr[swapIndex[i]];
				xr[swapIndex[i]] = xr[i];
				xr[i] = t;
				t = xi[swapIndex[i]];
				xi[swapIndex[i]] = xi[i];
				xi[i] = t;
			}
		}

		// 开始蝶形算法
		for (int wing_width = 1, delta_k = N >> 1; wing_width < N; wing_width <<= 1, delta_k >>= 1)
		{
			int p, q;
			p = 0;
			q = wing_width;
			for (int i = (N >> 1) / wing_width - 1; i >= 0; i--)
			{
				int k;
				k = 0;

				for (int j = 0; j < wing_width; j++)
				{
					//papilionaceous compute
					double tempr, tempi;
					tempr = coses[k] * xr[q] - sines[k] * xi[q];
					tempi = coses[k] * xi[q] + sines[k] * xr[q];

					xr[q] = xr[p] - tempr;
					xi[q] = xi[p] - tempi;
					xr[p] = xr[p] + tempr;
					xi[p] = xi[p] + tempi;

					p++;
					q++;
					k += delta_k;
				}
				p += wing_width;
				q += wing_width;
			}
		}
	}

	/**
	 * 傅立叶反变换
	 * @param xr
	 * @param xi
	 */
	public void ifft(double[] xr, double[] xi)
	{
//		assert xr.length == N;

		// 调整输入数据
		for (int i = 0; i < N; i++)
		{
			xr[i] /= (double) N;
			xi[i] /= (double) N;
		}

		// 根据swapIndex交换输入数据的顺序，为蝶形算法做准备
		for (int i = 0; i < N; i++)
		{
			if (i < swapIndex[i])
			{
				double t;
				t = xr[swapIndex[i]];
				xr[swapIndex[i]] = xr[i];
				xr[i] = t;
				t = xi[swapIndex[i]];
				xi[swapIndex[i]] = xi[i];
				xi[i] = t;
			}
		}

		// 开始蝶形算法
		for (int wing_width = 1, delta_k = N >> 1; wing_width < N; wing_width <<= 1, delta_k >>= 1)
		{
			int p, q;
			p = 0;
			q = wing_width;
			for (int i = (N >> 1) / wing_width - 1; i >= 0; i--)
			{
				int k;
				k = 0;

				for (int j = 0; j < wing_width; j++)
				{
					//papilionaceous compute
					double tempr, tempi;
					tempr = coses[k] * xr[q] - (-sines[k]) * xi[q];
					tempi = coses[k] * xi[q] + (-sines[k]) * xr[q];

					xr[q] = xr[p] - tempr;
					xi[q] = xi[p] - tempi;
					xr[p] = xr[p] + tempr;
					xi[p] = xi[p] + tempi;

					p++;
					q++;
					k += delta_k;
				}
				p += wing_width;
				q += wing_width;
			}
		}
	}


	/**
	 * 准备coses和sines
	 * @param N
	 * @param coses
	 * @param sines
	 */
	private void initialize()
	{
		double delta = -2.0 * Math.PI / N;
		double theta = 0;
		int N_2 = N >> 1;

		for (int i = 0; i < N_2; i++)
		{
			coses[i] = (double) Math.cos(theta);
			sines[i] = (double) Math.sin(theta);
			theta += delta;
		}
	}

	/**
	 * 计算交换索引
	 */
	private void initializeSwapIndex()
	{
		int M = (int) (Math.log(N) / Math.log(2) + 0.5);

		int q, m;
		q = 0;
		swapIndex[0] = q;
		int N_2 = N >> 1;
		for (int i = 1; i < N; i++)
		{
			m = N_2;
			while (m >= 2 && q >= m)
			{
				q -= m;
				m >>= 1;
			}
			q += m;
			swapIndex[i] = q;
		}
	}

	public static void main(String[] args)
	{
		FFT2.main(args);
		
//		if (true) return;
		
		int N = 1024;
//		int N = 16;
		
		double[] xr = new double[N];
		double[] xi = new double[N];
		for (int i=0; i<N; i++)
		{
			xr[i] = i; 
		}
	
		long time = System.currentTimeMillis();
		
		FFT fft = new FFT(N);
		
		for (int i=0; i<2*1024; i++)
		{
			fft.fft(xr, xi);
		}

		time = System.currentTimeMillis() - time;
		System.out.println("Time: " + time);

//		for (int i=0; i<N; i++)
//		{
//			System.out.println("" + xr[i] + "           " + xi[i]); 
//		}
	}
}



class FFT2
{
	private int N = -1; 				// The length of the "w" sequence
	private float[] coses, sines;
	private int[] swapIndex; 			// index of the swap of the items

	public FFT2(int N)
	{
		this.N = N;

		coses = new float[N >> 1];
		sines = new float[N >> 1];
		initialize();

		swapIndex = new int[N];
		initializeSwapIndex();
	}

	/**
	 * 傅立叶正变换
	 * @param xr
	 * @param xi
	 */
	public void fft(float[] xr, float[] xi)
	{
//		assert xr.length == N;

		// 根据swapIndex交换输入数据的顺序，为蝶形算法做准备
		for (int i = 0; i < N; i++)
		{
			if (i < swapIndex[i])
			{
				float t;
				t = xr[swapIndex[i]];
				xr[swapIndex[i]] = xr[i];
				xr[i] = t;
				t = xi[swapIndex[i]];
				xi[swapIndex[i]] = xi[i];
				xi[i] = t;
			}
		}

		// 开始蝶形算法
		for (int wing_width = 1, delta_k = N >> 1; wing_width < N; wing_width <<= 1, delta_k >>= 1)
		{
			int p, q;
			p = 0;
			q = wing_width;
			for (int i = (N >> 1) / wing_width - 1; i >= 0; i--)
			{
				int k;
				k = 0;

				for (int j = 0; j < wing_width; j++)
				{
					//papilionaceous compute
					float tempr, tempi;
					tempr = coses[k] * xr[q] - sines[k] * xi[q];
					tempi = coses[k] * xi[q] + sines[k] * xr[q];

					xr[q] = xr[p] - tempr;
					xi[q] = xi[p] - tempi;
					xr[p] = xr[p] + tempr;
					xi[p] = xi[p] + tempi;

					p++;
					q++;
					k += delta_k;
				}
				p += wing_width;
				q += wing_width;
			}
		}
	}

	/**
	 * 傅立叶反变换
	 * @param xr
	 * @param xi
	 */
	public void ifft(float[] xr, float[] xi)
	{
//		assert xr.length == N;

		// 调整输入数据
		for (int i = 0; i < N; i++)
		{
			xr[i] /= (float) N;
			xi[i] /= (float) N;
		}

		// 根据swapIndex交换输入数据的顺序，为蝶形算法做准备
		for (int i = 0; i < N; i++)
		{
			if (i < swapIndex[i])
			{
				float t;
				t = xr[swapIndex[i]];
				xr[swapIndex[i]] = xr[i];
				xr[i] = t;
				t = xi[swapIndex[i]];
				xi[swapIndex[i]] = xi[i];
				xi[i] = t;
			}
		}

		// 开始蝶形算法
		for (int wing_width = 1, delta_k = N >> 1; wing_width < N; wing_width <<= 1, delta_k >>= 1)
		{
			int p, q;
			p = 0;
			q = wing_width;
			for (int i = (N >> 1) / wing_width - 1; i >= 0; i--)
			{
				int k;
				k = 0;

				for (int j = 0; j < wing_width; j++)
				{
					//papilionaceous compute
					float tempr, tempi;
					tempr = coses[k] * xr[q] - (-sines[k]) * xi[q];
					tempi = coses[k] * xi[q] + (-sines[k]) * xr[q];

					xr[q] = xr[p] - tempr;
					xi[q] = xi[p] - tempi;
					xr[p] = xr[p] + tempr;
					xi[p] = xi[p] + tempi;

					p++;
					q++;
					k += delta_k;
				}
				p += wing_width;
				q += wing_width;
			}
		}
	}


	/**
	 * 准备coses和sines
	 * @param N
	 * @param coses
	 * @param sines
	 */
	private void initialize()
	{
		float delta = (float) (-2.0 * Math.PI / N);
		float theta = 0;
		int N_2 = N >> 1;

		for (int i = 0; i < N_2; i++)
		{
			coses[i] = (float) Math.cos(theta);
			sines[i] = (float) Math.sin(theta);
			theta += delta;
		}
	}

	/**
	 * 计算交换索引
	 */
	private void initializeSwapIndex()
	{
		int M = (int) (Math.log(N) / Math.log(2) + 0.5);

		int q, m;
		q = 0;
		swapIndex[0] = q;
		int N_2 = N >> 1;
		for (int i = 1; i < N; i++)
		{
			m = N_2;
			while (m >= 2 && q >= m)
			{
				q -= m;
				m >>= 1;
			}
			q += m;
			swapIndex[i] = q;
		}
	}

	public static void main(String[] args)
	{
		int N = 1024;
//		int N = 16;
		
		float[] xr = new float[N];
		float[] xi = new float[N];
		for (int i=0; i<N; i++)
		{
			xr[i] = i; 
		}
	
		long time = System.currentTimeMillis();
		
		FFT2 fft = new FFT2(N);
		
		for (int i=0; i<2*1024; i++)
		{
			fft.fft(xr, xi);
		}

		time = System.currentTimeMillis() - time;
		System.out.println("Time: " + time);

//		for (int i=0; i<N; i++)
//		{
//			System.out.println("" + xr[i] + "           " + xi[i]); 
//		}
	}
}
