package ga;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * 手动停止器
 * 人工点击发出停止信号
 */
public class ManualStopper implements Stopper, Runnable
{
	private GA ga;
	
	private boolean stopSignal = false;
	private boolean pauseSignal = false;

	private JFrame frame;
	private JButton pauseButton;
	private JButton stopButton;
	
	private long time;

	public ManualStopper(GA ga)
	{
		this.ga = ga;
		new Thread(this).start();
	}

	public boolean canStop()
	{
		synchronized (this)
		{
			try
			{
				while (pauseSignal) wait(); 			
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}

		return stopSignal;
	}
	
	public void run()
	{
		initializeComponent();
	}
	
	private void initializeComponent()
	{
		frame = new JFrame();
		frame.setTitle("遗传算法");
		frame.getContentPane().setLayout(new GridLayout());
		pauseButton = new JButton("暂停");
		frame.getContentPane().add(pauseButton);
		stopButton = new JButton("结束");
		frame.getContentPane().add(stopButton);
		
		pauseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				pauseButtonOnClick(e);
			}
		});
		
		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				stopButtonOnClick(e);
			}
		});
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		frame.show();
	}
	
	private void pauseButtonOnClick(ActionEvent e)
	{
		synchronized (this)
		{
			if (pauseSignal)
			{
				// 结束暂停
//				time = System.currentTimeMillis() - time;
//				ga.startTime -= time;
				pauseSignal = false;
				pauseButton.setText("暂停");
			}
			else
			{	
				// 开始暂停
//				time = System.currentTimeMillis();
				pauseSignal = true;
				pauseButton.setText("继续");
			}
			notify(); 			
		}
	}

	private void stopButtonOnClick(ActionEvent e)
	{
		synchronized (this)
		{
			pauseSignal = false;
			stopSignal = true;
			notify(); 			
		}

		frame.dispose();
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
