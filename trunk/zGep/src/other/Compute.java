package other;

import java.io.*;
import java.util.*;
public class Compute
{  
	private int WinSide=12;
	final int loop=216;
	private double a,b,c,d,e,f,g,h,i,j,k,l;
	
	private double[] data = {
			1462.61,
			1462.97,
			1463.64,
			1464.59,
			1466.01,
			1468.11,
			1472.11,
			1478.45,
			1479.66,
			1484.46,
			1490.39,
			1500.22
	};
	
/*	private double[] data = {
			1452.62,
			1453.32,
			1454.36,
			1456.02,
			1457.71,
			1460.53,
			1466.77,
			1477.11,
			1481.95,
			1486.75
	//		1485.62,
//			1478.68
			};
	*/
	void computeTheFunction(){
		for(int iloop=1;iloop<=loop;iloop++)
		{
		/*
			a=data[9];
			b=data[8];
			c=data[7];
			d=data[6];
			e=data[5];
			f=data[4];
			g=data[3];
			h=data[2];
			i=data[1];
			j=data[0];
		*/
			
//			/*
			a=data[0];
			b=data[1];
			c=data[2];
			d=data[3];
			e=data[4];
			f=data[5];
			g=data[6];
			h=data[7];
			i=data[8];
			j=data[9];
			k=data[10];
			l=data[11];
				
//			 */
			
			double res=((((((k)+(((i)-(g))/(a)))/((i)-(((d)/(f))+(h))))+
					(((-((e)-(k)))/((i)-((a)*(a))))*((l)+(d))))+
					(((((k)-(a))-(a))/(d))+(((d)/(a))-((k)/(h)))))+
					((k)*(-((b)*(i)))))+((j)/((i)/(j)))
			;
			System.out.println(res);
//	  /*			
			for(int iWinSide=1;iWinSide<WinSide;iWinSide++){
				data[iWinSide-1]=data[iWinSide];
			}
			data[WinSide-1]=res;
 //     */	
			
	/*		
			for(int iWinSide=WinSide-1;iWinSide>0;iWinSide--){
				data[iWinSide]=data[iWinSide-1];
			}
			data[0]=res;	
	*/		
		}
	}
	
	
	public static void main(String[] args) {
		Compute c = new Compute();
			c.computeTheFunction();
	}
   
}
