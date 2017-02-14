package qzhwebserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.naming.spi.DirectoryManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import layout.TableLayout;

/**
 * class:main Gui class of my light web server
 * @author qzh all rights reserved
 * @finish date 12-24-02:40
 * @version:1.0
 * 
 * */

public class Gui extends JFrame {
	
	private static int totalVisit=0;  //��¼�����������ܵķ�����
	private JLabel localAddr;
	private JLabel localPort;
	private JLabel workDirect;
	private JLabel statusField;
	private JTextField addrEdit;
	private JTextField portEdit;
	private JTextField  workEdit;
	private JEditorPane areashow = null;
	private JLabel visitShow;
	private Font textStyle = new Font("΢���ź�",Font.PLAIN, 12);
	private Font textStyle1 = new Font("����", Font.BOLD, 14);
	private JButton changePort;
	private JButton changeDirectory;
	private JButton runBtn;
	private JButton stopBtn;
	private String localIp1="127.0.0.1";
	private String localIp2 ="";
	private boolean isRunning = false;
	private static Server mServer=null;
	private Thread currentThread = null;
	public void appendText(String msg)
	{
		String tmp = areashow.getText().toString();
		if(tmp.length()>300000)  //����̫��ֱ��ɾ��
		{
			areashow.setText("");
			areashow.setText(msg);
		}
		else{
			areashow.setText(tmp+msg);
		}
	}
	public void addVisit()
	{
		String string = visitShow.getText().toString().trim();
		totalVisit = Integer.valueOf(string);
		totalVisit++;
		visitShow.setText(new String("")+totalVisit);
	}
	
	//�������������쳣��ʱ��ص��˺���
	public void CloseWhenException()
	{
		if(mServer!=null)
			mServer.ClearSocket();
		isRunning = false;
		statusField.setText("haulting");
		if(currentThread.isAlive())
		{
			try {
				currentThread.join(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Error Happen when kill child thread");
				e.printStackTrace();
			}
		}
	}
	public Gui() {
		// TODO Auto-generated constructor stub
	//	setIconImages(new ImageIcon(this.getClass().getResource("/Icon/startIconB.png")));
		try {
			Image mainIcon = ImageIO.read(this.getClass().getResource("/Icon/appIcon.png"));
			setIconImage(mainIcon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		areashow = new JEditorPane();
		setTitle("My Web Server");
		setSize(500,400);
		InitializeMainWindow();
		InitializeFunction();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);  //��ʼ�������ÿɼ��Ĳ��費�ܵߵ����м�
		setLocationRelativeTo(null);
	}
	/**
	 * function: set the recall function that on each click or change event
	 * @author qzh
	 * @date:12-24/2016
	 * @version:1.0
	 * */
	public void InitializeFunction()  
	{
		changeDirectory.addActionListener(e->{
			
			FileDialog OpenDia = new FileDialog(this, "Open File"); // ��Ҫ
			OpenDia.setVisible(true);
			String path = OpenDia.getDirectory() + OpenDia.getFile(); // ��Ҫ
			workEdit.setText(path);
		});
		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();	
			localIp2=addr.getHostAddress().toString();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(localIp2!=null)
		{
		//	System.out.println(localIp2);
			addrEdit.setText(localIp1+" "+localIp2);
		}
		else{
			addrEdit.setText(localIp1);
		}
		stopBtn.addActionListener(event->{
			if(isRunning == false)
				return;
			else{
				isRunning = false;
				statusField.setText("halting");
				mServer.ClearSocket();
				mServer.CloseServer();
			}
		});
		runBtn.addActionListener(event->{
			if(isRunning)
			{
				return;
			}
			String tmpPort = portEdit.getText().toString().trim();
			int port = Integer.valueOf(tmpPort);
			String workPath = workEdit.getText();
			System.out.println(tmpPort);
			System.out.println(port);
			System.out.println(workPath);
			try {
				mServer = new Server(port);
				mServer.SetRoot(workPath);
				mServer.setUi(Gui.this);
			//	Runnable thread = ()->{
			//		System.out.println("Xxx");
			//		mServer.start();
			//	};
				currentThread = new Thread(()->{
					mServer.start();
				});
				currentThread.start();
				isRunning = true;
				statusField.setText("Running");
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		);
	}
	/*
	 * 
		JSplitPane mainSpilt = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
		mainSpilt.setLeftComponent(sroll1);
		mainSpilt.setRightComponent();
		mainSpilt.setDividerLocation(200);
		mainSpilt.setDividerSize(5);
		rightpane.setVisible(true);
	 * 
	 * 
	 * 
	 * */
	public void InitializeMainWindow()
	{
		//setLayout(null);  //���Բ��ֵķ�ʽ
		double[][] size = {
				{TableLayout.FILL},
				{0.05,0.6,0.05,0.3}
		};
		
		JLabel top1 = new JLabel("����");
		top1.setFont(textStyle);
		JLabel top2 = new JLabel("����״̬");
		top2.setFont(textStyle);
		
		JScrollPane scrollRight = new JScrollPane(areashow);
		
		JPanel middlePane = new JPanel();
		JPanel bottomPane = new JPanel();
		middlePane.setLayout(null);
		bottomPane.setLayout(null); // CDCDB4
		middlePane.setBackground(new Color(0xcd, 0xcd, 0xb4)); //Color.LIGHT_GRAY
		bottomPane.setBackground(new Color(0xcd, 0xcd, 0xb4)); //Color.LIGHT_GRAY
		
		
		top1.setBounds(0, 0, 100, 50);
		
		
		localAddr = new JLabel("����ip");
		localAddr.setFont(textStyle1);
		addrEdit = new JTextField();
		localAddr.setBounds(2,3,60,20);
		addrEdit.setBounds(70,3,150,20);
		addrEdit.setEditable(false);
		
		localPort = new JLabel("���ض˿�");
		localPort.setFont(textStyle1);	
		localPort.setBounds(2, 50, 60, 20);
		portEdit = new JTextField();
		portEdit.setBounds(70, 50, 100, 20);
		portEdit.setText(new String("")+806);
		
		workDirect = new JLabel("����Ŀ¼");
		workDirect.setFont(textStyle1);
		workDirect.setBounds(2, 100, 60, 20);
		
		workEdit = new JTextField();
		workEdit.setBounds(70, 100, 150, 20);
		workEdit.setText("e:/Document/MywebServer");
		
		changeDirectory = new JButton("Change");
		changeDirectory.setFont(textStyle);
		changeDirectory.setBounds(70,121,20,20);
		changeDirectory.setBorderPainted(false);
		
		runBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/startIconB.png")));
		runBtn.setBorderPainted(false);	
		runBtn.setBounds(40, 160, 30, 25);
		
		stopBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/stopIcon.png")));
		// new ImageIcon(this.getClass().getResource("/Icon/stopIcon.png"))
		stopBtn.setBorderPainted(false);
		stopBtn.setBounds(130, 160, 30, 25);
		
		middlePane.add(localAddr);
		middlePane.add(addrEdit);
		middlePane.add(localPort);
		middlePane.add(portEdit);
		middlePane.add(workDirect);
		middlePane.add(workEdit);
	//	middlePane.add(changeDirectory);
		middlePane.add(runBtn);
		middlePane.add(stopBtn);
		
		JLabel status = new JLabel("����״̬");
		localAddr = new JLabel("����ip");
		localAddr.setFont(textStyle1);
		status.setBounds(2,3,60,20);
		status.setFont(textStyle1);
		statusField = new JLabel("halting");
	//	statusField.setEditable(false);
		statusField.setBounds(70,3,100,20);
		JLabel visitLabel = new JLabel("������");
		visitLabel.setFont(textStyle1);
		visitShow = new JLabel(new String("")+totalVisit);
		visitLabel.setBounds(2,40,50,20);
		visitShow.setBounds(70, 40, 50, 20);
		bottomPane.add(status);
		bottomPane.add(statusField);
		bottomPane.add(visitLabel);
		bottomPane.add(visitShow);
		
//		this.add(localAddr);
		JPanel leftpane = new JPanel(new TableLayout(size));//�Ȼ������
		leftpane.add(top1,"0,0");
		leftpane.add(top2,"0,2");
		leftpane.add(middlePane,"0,1");
		leftpane.add(bottomPane,"0,3");
		
		
		JSplitPane mainSpilt = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
		mainSpilt.setLeftComponent(leftpane);
		mainSpilt.setRightComponent(scrollRight);
		mainSpilt.setDividerLocation(200);
		mainSpilt.setDividerSize(5);
//		this.getContentPane().add(mainSpilt);
	//	this.getContentPane().add(new TextArea(),BorderLayout.CENTER);
		this.add(mainSpilt);
	//	this.pack();
	//	this.getContentPane().add(new JLabel("����������"));
		
	}
	
	public static void main(String[] args) {
		new Gui();
	}
}
