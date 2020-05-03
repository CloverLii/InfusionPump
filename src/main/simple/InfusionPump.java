package main.simple;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.border.Border;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;


public class InfusionPump extends JFrame{

	public JButton Up;
	public JButton Down;
	public JButton YesStart;
	public JButton NoStop;
	public JButton OnOff;
	public JLabel Display;
			
	private int TOTAL_VOLUME = 500;
	private int RATE_MAX = 25;
	private int VALUE_STEP = 1;
	
	private int pumpID;
	private int batteryPercent = 0;
	private int rate;	// volume per minute, ml/min
	private int duration;	// duration = TOTAL_VOLUME/rate
	private int curRate;
	private String displayContent = "";
	private boolean isPowerOn = false;
	
	enum Status{
		Initial,	// initial status after powered on
		SettingStart,	// start setting
		Setting,	// configuration is ongoing, + and - is enabled
		Paused,		// infusion is paused
		Infusing,	// infusion is ongoing
		SettingCancelled	// configuration is cancelled
	}
	private Status status;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InfusionPump window = new InfusionPump(1);
					window.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public InfusionPump(int id) {
		this.pumpID = id;
		initialize();
		initializeValue();
	}
	
/**
 * Create main view of the infusion pump	
 */
	public void initialize () {
		
		JFrame jf = new JFrame("Infusion Pump");
		jf.setBounds(100, 100, 550, 470 );
		
		Container container = jf.getContentPane();	
		
		Display  = new JLabel("Display Settings");
		Display.getBounds();
		Display.setName("Display");
		Display.setBounds(80, 10, 400, 200);
		Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
		Display.setBorder(border);
		Display.setText(displayContent);
		container.add(Display);
			
		Up = new JButton("+");
		Up.setName("Up");
		Up.setBounds(10, 220, 100, 100);
		Up.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeUp();
			}
		});
		container.add(Up);
		
		Down = new JButton("-");
		Down.setName("Down");
		Down.setBounds(10, 330, 100, 100);
		Down.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeDown();
			}
		});
		container.add(Down);
		
		YesStart = new JButton("Yes/Start");
		YesStart.setName("YesStart");
		YesStart.setBounds(120, 220, 200, 100);		
		// press: confirm the settings or start infusion
		YesStart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeYesStart();
			}
		});
		
		// long press: start setting
		Timer timer2 = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executeSetting();
			}
		});
		
		YesStart.addMouseListener(new MouseAdapter() {			 
		      @Override
		      public void mousePressed(MouseEvent e) {
		        timer2.start();
		      }		 
		      @Override
		      public void mouseReleased(MouseEvent e) {
		        timer2.stop();
		      }
		    });		
		container.add(YesStart);
			
		NoStop = new JButton("No/Stop");
		NoStop.setName("NoStop");
		NoStop.setBounds(120, 330, 200, 100);
		NoStop.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeNoStop();
			}
		});		
		container.add(NoStop);	
		
		OnOff = new JButton("On/Off");
		OnOff.setName("OnOff");
		OnOff.setBounds(330, 220, 200, 200);		
		Timer timer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executeOnOff();
			}
		});		
		OnOff.addMouseListener(new MouseAdapter() {			 
		      @Override
		      public void mousePressed(MouseEvent e) {
		        timer.start();
		      }		 
		      @Override
		      public void mouseReleased(MouseEvent e) {
		        timer.stop();
		      }
		    });		
		container.add(OnOff);		
		
		jf.setLayout(null);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
/**
 * Initialize the values when turns on	
 */
	private void initializeValue() {
		
		batteryPercent = 100;
		rate = 5;
		curRate = rate;
		duration = TOTAL_VOLUME/rate;
		status = Status.Initial;
		displayInfo();
	}
	
	private void displayInfo() {
		if(isPowerOn) {
			displayContent = "<html>" 
					+ "<br>1. Long press [On/Off] to power on "
					+ "<br>2. Long press [Yes/Start] to configure the infusion pump"
					+ "<br>3. Duration(mins) = total volume/rate"
					+ "<br>"
					+ "<br>PumpId: " + pumpID
					+ "<br>BatteryPercent: " + batteryPercent
					+ "<br>Total Volume: " + TOTAL_VOLUME
					+ "<br>Rate(ml/min): " + rate
					+ "<br>Duration(mins): " + duration
					+ "<br>Status: " + status
					+ "</html>";
		}else {
			displayContent = "<html>"
					+ " Powered off..."
					+ "<br>"
					+ "<br>1. Long press [On/Off] to power on "
					+ "<br>"
					+ "<br>2. Long press [Yes/Start] to configure the infusion pump";
		}	
		Display.setText(displayContent);
	}
	
	/**
	 * Increase the value of rate, calculate the duration
	 */
	private void executeUp() {
		if(status == Status.SettingStart) {
			status = Status.Setting;
			curRate = rate;
		}
		if(isPowerOn && status == Status.Setting) {
			if(rate < RATE_MAX) {
				rate += VALUE_STEP;
				duration = (int) TOTAL_VOLUME/rate;
			}else {
				rate = RATE_MAX; 
				duration = (int) TOTAL_VOLUME/rate;
			}
			System.out.println("Increase rate to: " + rate);
		}	
		displayInfo();
	}
	
	/**
	 * Decrease the value of rate, calculate the duration
	 */
	private void executeDown() {
		if(status == Status.SettingStart) {
			status = Status.Setting;
			curRate = rate;
		}
		if(isPowerOn && status == Status.Setting) {
			if(rate > 1) {
				rate -= VALUE_STEP;
				duration = (int) TOTAL_VOLUME/rate;
			}else {
				rate = 1;
				duration = (int) TOTAL_VOLUME/rate;
			}
			System.out.println("Decrease rate to: " + rate);
		}
		displayInfo();
	}
	
	/**
	 * Start infusion	
	 */
	private void executeYesStart() {	
		if(isPowerOn) {
			if(status == Status.Initial) {
				System.out.println("Please configure the infusion pump first ");
			}else if(status == Status.Paused  || status == Status.SettingCancelled || status == Status.Setting) {
				status = Status.Infusing;
				System.out.println("**** Start infusion ****");
			}
			displayInfo();
		}		
	}
	
	/**
	 * Start configuration
	 */
	public void executeSetting() {
		if(isPowerOn && status != Status.SettingStart) {
				status = Status.SettingStart;
				System.out.println("**** Start configuration ****");
			}
		displayInfo();
	}

	/**
	 * Cancel settings or pause infusion
	 */
	private void executeNoStop() {
		if(isPowerOn) {
			if(status == Status.Infusing) {
				status = Status.Paused;
				System.out.println("**** Status: infusting -> paused ****");
			}else if(status == Status.Setting || status == Status.SettingStart) {
				setRate(curRate);
				setDuration(curRate);
				status = Status.SettingCancelled;
				System.out.println("**** Cancel configuration ****");
			}
			displayInfo();
		}
	}
	
	/**
	 * Power on or power off
	 */
	private void executeOnOff() {
		if (isPowerOn == false) {
			isPowerOn = true;
			System.out.println("**** Power on ****");
		}else {
			isPowerOn = false;
			System.out.println("**** Powered off ****");
		}
		displayInfo();
	}
	
	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getRate() {
		return rate;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int rate) {
		if(rate != 0) {
			this.duration = (int)TOTAL_VOLUME/rate;
		}else {
			duration = 0;
		}

	}
}
