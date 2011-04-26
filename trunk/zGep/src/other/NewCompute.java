package other;

import java.io.*;
import java.util.LinkedList;
import java.util.StringTokenizer;


public class NewCompute
{  
	
	private double a,b,c,d,e,f,g,h,i,j,k,l,m;
	//final int dimension = 13; 
	//final int future
	//double[][] para = new double[300][13];
	
	final int dimension = 12;   //嵌入维数
	double[][] para = new double[300][12];  //嵌入维数  10+1
	NewCompute()
	{
		//int count = 0;
		/*a = 101;
		b = 82;
		c = 66;
		d = 35;
		e = 31;
		f = 7;
		g = 20;
		h = 92;
		i = 154;
		j = 125;*/
		  /* j =  1453.99;
		   i = 1457.3;
		   h = 1446.29;
		   g = 1492.96;
		   f = 1500.64;
		   e = 1513.83;
		   d = 1512.6;
		   c = 1514.78;
		   b = 1496.43;
		   a = 1511.69;*/
		/*
		   para[][count++] = a;
		   para[][count++] = b;
		   para[][count++] = c;
		   para[count++] = d;
		   para[count++] = e;
		   para[count++] = f;
		   para[count++] = g;
		   para[count++] = h;
		   para[count++] = i;
		   para[count++] = j;
		//   para[count++] = k;
		//   para[count++] = l;
		  // para[count++] = m;*/
		   
	}
	
	
	private void readSourceData()
	{
	//	String source = "D:\\GEP\\GEP_data\\stock\\处理后03年上证指数(03_1_2到03_12_31).txt";
	// String source = "D:\\GEP\\GEP_data\\stock\\处理后03年上证指数(03_1_2到03_12_31)嵌入维数为10.txt";
		String source = "E:\\Myeclipse\\Original zGEP\\data\\stock\\Mistock_08_200.txt";
		//String source ="E:\\Myeclipse\\Original zGEP\\data\\stock\\stock_03_all_13.txt";
		int row = 0,col = 0;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(source));
			String line = reader.readLine();
			while(true)
			{
				if(line == null) break;
				col = 0;
				for (StringTokenizer t=new StringTokenizer(line); t.hasMoreTokens()&&col < dimension;)
				{
					String token = t.nextToken();
					para[row][col++] = Double.parseDouble(token);
				}
				line = reader.readLine();
				row++;
			}
			reader.close();	
		}
		catch(IOException e){
			System.out.println("error\n");
		}
		
	}
	
public void computeTheFunction()
{		
		   double res = 0;
		   
		   /*load data from file to para*/
		   this.readSourceData();
		   /*compute the function with the para*/
		   for(int row = 0;row < 191/*221*//*241-13*/;row++){
			    int count = 0;
			    res = 0;
				a = this.para[row][count++];
				b = this.para[row][count++];
				c = this.para[row][count++];
				d = this.para[row][count++];
				e = this.para[row][count++];
				f = this.para[row][count++];
				g = this.para[row][count++];
				h = this.para[row][count++];
				i = this.para[row][count++];
				j = this.para[row][count++];
				k = this.para[row][count++];
				l = this.para[row][count++];
			//	m = this.para[row][count++];
		   
		  
		   /*基因表达式编程中预测太阳黑子的函数*/
		   /*res += (j+((i/(f+e))+((j-j)*a)));
		   res += (j/((((d+d)+c)-j)+i));
		   res += (j/(e+g));
		   res += (j/(e+c));
		   res += (f/(c-g));
		   res += (d/(b-f));
		   res += (b/(h-a));
		   res += ((((c-i)*j)+(a+a))/(h+(i+c)));*/
				   
		/*res = l/(e/(c-e)-(b+e))+c/(a/(c-f)-l)+
		f/(d+a/(a-f)+g+m)+(g+e*g/a)/((l+c)*(h-i))+
		e/(b/(c+e-f-h)+h)+m ;*///论文中的真实函数
		res = (((((-(g))*((-(((i)-(c))+(f)))+
				((f)*(f))))+(((f)+(-(c)))/((l)+
						(((b)+(b))/(b)))))+(c))+((h)+(g)))+(k)
				;
		   
		   System.out.println(res);
		   }
		 /*  for(count = 0 ;count < this.para.length - 1;count++)
			   this.para[count] = this.para[count+1];
		   this.para[count] = res;*/
	
	}
   
	public static void main(String []args)
	{
		NewCompute test=new NewCompute();
		test.computeTheFunction();
	}
	
}




