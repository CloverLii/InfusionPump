package main.simple;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;


public class InfusionPump extends JFrame{

	private JButton upBtn;
	private JButton downBtn;
	private JButton yesStartBtn;
	private JButton noStopBtn;
	private JButton onOffBtn;
	private JLabel displayLabel;
			
	private final int VOLUME_MAX = 800; //maximum of volume, ml
	private final int VOLUME_MIN = 100; 
	private final int DURATION_MAX = 80; //maximum of duration, mins
	private final int DURATION_MIN = 10;
	private final int VOLUME_STEP = 100; //increment or decrement when change volume
	private final int DURATION_STEP = 10; //increment or decrement when change duration
	
	private int pumpID; //default value is 1
	private int batteryPercent = 0; // value is 100 when power on
	private int volume = 0; 
	private int duration = 0;	
	private int oldVolume = 0; //used when cancel settings
	private int oldDuration = 0; //used when cancel settings
	private String displayContent = " ";
	
	private final String hintStr = "<html>"
			+ " Configuration Instruction"
			+ "<br>"
			+ "<br> -> Power On"
			+ "<br> -> Set Volume"
			+ "<br> -> Set Duration"
			+ "<br> -> Confirm Settings"
			+ "<br> -> Start Infusion?"
			+ "<br> -> Start Infusion...";
	
	enum Status{
		Off, // power off
		Initial, // initial status after powered on or stop infusion
		SetVolume,
		SetDuration,
		SettingsConfirmed,
		QStartInfusion, //double check before start infusion
		Infusing, // infusion is ongoing
		Paused,	 // infusion is paused
		SettingsCancelled // configuration is cancelled
	}
	private Status status = Status.Off;
	
	/*
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
		displayInfo();
	}
	
/*
 * Create all elements of the infusion pump	
 */
	public void initialize () {
		
		JFrame jf = new JFrame("Infusion Pump");
		jf.setBounds(100, 100, 530, 450 );
		
		Container container = jf.getContentPane();	
		
		JLabel hintLabel  = new JLabel();
		hintLabel.setBounds(30, 20, 180, 180);
		Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
		hintLabel.setBorder(border);
		hintLabel.setText(hintStr);
		container.add(hintLabel);
		
		displayLabel  = new JLabel("Display Settings");
		displayLabel.setBounds(230, 20, 260, 180);
		displayLabel.setBorder(border);
		displayLabel.setText(displayContent);
		container.add(displayLabel);
			
		upBtn = new JButton("+");
		upBtn.setBounds(30, 220, 80, 80);
		upBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeUp();
			}
		});
		upBtn.setEnabled(false);
		container.add(upBtn);
		
		downBtn = new JButton("-");
		downBtn.setBounds(30, 320, 80, 80);
		downBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeDown();
			}
		});
		downBtn.setEnabled(false);
		container.add(downBtn);
		
		yesStartBtn = new JButton("Yes/Start");
		yesStartBtn.setBounds(125, 220, 170, 80);		
		yesStartBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeYesStart();
			}
		});
		container.add(yesStartBtn);
			
		noStopBtn = new JButton("No/Stop");
		noStopBtn.setBounds(125, 320, 170, 80);
		noStopBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeNoStop();
			}
		});	
		noStopBtn.setEnabled(false);
		container.add(noStopBtn);	
		
		onOffBtn = new JButton("On/Off");
		onOffBtn.setBounds(310, 220, 180, 180);
		onOffBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				executeOnOff();
			}
		});				
		container.add(onOffBtn);		
		
		jf.setLayout(null);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
/*
 * Initialize values	
 */
	private void initializeValue() {
		
		batteryPercent = 100;
		duration = 10;
		volume = 100;
		duration = 10;
		oldVolume = volume;
		oldDuration = duration;
		
		status = Status.Initial;
		upBtn.setEnabled(true);
		downBtn.setEnabled(true);
		noStopBtn.setEnabled(true);
		displayInfo();
	}
	
	/*
	 * Display the settings and status
	 */
	private void displayInfo() {
		if(status == Status.Off) {
			displayContent = "<html>" 
							+ " <br>Powered Off"
							+ " <br>"
							+ " <br>Display confusion status and settings"
							+ "</html>";			
		}else {
			displayContent = "<html>" 
					+ " <br>PumpId: " + pumpID
					+ " <br>BatteryPercent: " + batteryPercent
					+ " <br>"
					+ " <br>Volume(ml): " + volume
					+ " <br>Duration(mins): " + duration
					+ " <br>Rate(ml/mins): " + calculateRate()
					+ " <br>"
					+ " <br>Status: " + status
					+ "</html>";
		}
		displayLabel.setText(displayContent);
	}
	
	/*
	 * Calculate infusion rate
	 */
	private String calculateRate() {
		return String.format("%.2f", (double)volume/duration);
	}
	
	/*
	 * Increase the value of volume of duration
	 */
	private void executeUp() {
		if(status == Status.SetVolume) {
			if(volume < VOLUME_MAX) {
				volume += VOLUME_STEP;				
			}else {
				volume = VOLUME_MAX; 
			}
			System.out.println("Increase volume to: " + volume);
		}else if(status == Status.SetDuration) {
			if(duration < DURATION_MAX) {
				duration += DURATION_STEP;
			}else {
				duration = DURATION_MAX; 
			}			
			System.out.println("Increase duration to: " + duration);
		}
		displayInfo();
	}
	
	/*
	 * Decrease the value of volume or duration
	 */
	private void executeDown() {
		if(status == Status.SetVolume) {
			if(volume > VOLUME_MIN) {
				volume -= VOLUME_STEP;				
			}else {
				volume = VOLUME_MIN; 
			}
			System.out.println("Decrease volume to: " + volume);
		}else if(status == Status.SetDuration) {
			if(duration > DURATION_MIN) {
				duration -= DURATION_STEP;
			}else {
				duration = DURATION_MIN; 
			}
			System.out.println("Decrease duration to: " + duration);
		}
		displayInfo();
	}
	
	/*
	 * Switch modes: SetVolume, SetDuration, ConfirmSettings, QInfusionStart, StartInfusion	
	 */
	private void executeYesStart() {
		switch(status) {
			case Initial:
			case Infusing:
			case SettingsCancelled:
				status = Status.SetVolume;
				System.out.println("**** Set volume ****");
				break;
			case SetVolume:
				status = Status.SetDuration;
				System.out.println("**** Set duration ****");
				break;
			case SetDuration:
				status = Status.SettingsConfirmed;
				oldVolume = volume;
				oldDuration = duration;
				System.out.println("**** Confirm settings ****");
				break;
			case SettingsConfirmed:
				status = Status.QStartInfusion;
				System.out.println("**** Start infusion ? ****");
				break;
			case QStartInfusion:
			case Paused:
				status = Status.Infusing;
				System.out.println("**** Start infusion ... ****");
				break;
			default:
				break;
		}
		displayInfo();	
	}	

	/*
	 * Cancel settings, pause infusion or stop infusion
	 */
	private void executeNoStop() {
		switch (status){
			case SetVolume:
			case SetDuration:
				status = Status.SettingsCancelled;
				volume = oldVolume;
				duration = oldDuration;
				System.out.println("**** Cancel settings ****");
				break;
			case Paused:
			case SettingsCancelled:
				initializeValue();
				System.out.println("**** Status: paused/settingsCancelled -> Initial ****");
				break;
			default:
				status = Status.Paused;
				System.out.println("**** Status: infusing -> paused ****");
				break;
		}
		displayInfo();
	}
	
	/*
	 * Power on or power off
	 */
	private void executeOnOff() {
		if (status == Status.Off) {
			status = Status.Initial;
			initializeValue();
			System.out.println("**** Powered on ****");
		}else {
			status = Status.Off;
			upBtn.setEnabled(false);
			downBtn.setEnabled(false);
			noStopBtn.setEnabled(false);
			System.out.println("**** Powered off ****");
		}
		displayInfo();
	}
	
}
