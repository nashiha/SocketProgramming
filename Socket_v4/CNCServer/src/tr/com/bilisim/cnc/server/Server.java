/* @description: Multi-threaded server. Receives objects from Client
 * @authors: Akif Batur, Nashiha Ahmed
 * @version: 3 (date: 04.08.16)
 */
package tr.com.bilisim.cnc.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

//import tr.com.bilisim.cnc.client.Abc;

public class Server implements Runnable 
{
	public Socket nsocket;
	public static Thread th = null;
	public static ObjectInputStream inStream = null;
	public static Socket socket = null;
	private static JTextArea jTextAreaConsole;
	public static JTextField jTextFieldPort;
	public static JButton jButtonStart;
	public static JButton jButtonStop;
	public static JButton jButtonClear;

	Server(Socket nsocket) 
	{
		this.nsocket = nsocket;
	}
	//Create Frame

	public static JPanel jPanelTop = new JPanel();
	public static JPanel jPanelBottom = new JPanel();
	public static void createGUI()
	{

		//Create Top and Bottom Panels
		JPanel jPanelWest = new JPanel();
		JPanel jPanelCenter = new JPanel();
		JPanel jPanelButtons = new JPanel();

		//Create Labels, Text fields, and Buttons
		//Labels
		JLabel jLabelTitle = new JLabel("CNC Server Ayarlari");
		JLabel jLabelIp = new JLabel("IP: ");
		JLabel jLabelIpValue = null;
		try {
			jLabelIpValue = new JLabel(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) 
		{
			e1.printStackTrace();
		}
		JLabel jLabelPort = new JLabel("Port:      ");
		jLabelTitle.setHorizontalAlignment(JLabel.CENTER);
		jLabelTitle.setVerticalAlignment(JLabel.CENTER);
		jLabelTitle.setFont( new Font( "Dialog", Font.BOLD, 13));
		//Text Field
		jTextFieldPort = new JTextField();
		jTextFieldPort.setText("8090");
		jTextFieldPort.setMaximumSize( new Dimension( 250,20));
		//Buttons
		jButtonStart = new JButton("BASLAT");
		jButtonStop = new JButton("DURDUR");
		jButtonClear = new JButton("TEMIZLE");
		jButtonStop.setEnabled(false);
		//Text Area with Scroll
		jTextAreaConsole = new JTextArea( "");
		jTextAreaConsole.setRows(10);
		jTextAreaConsole.setColumns(50);
		jTextAreaConsole.setText(jTextAreaConsole.getText()+"******************************"
				+ "*******************************************************************************\n");
		jTextAreaConsole.setText(jTextAreaConsole.getText()+"\t\t                    BILISIM A.S.\n");
		jTextAreaConsole.setText(jTextAreaConsole.getText()+"******************************"
				+ "*******************************************************************************\n");
		jTextAreaConsole.setEditable(false);
		JScrollPane scroll = new JScrollPane ( jTextAreaConsole );
		scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		DefaultCaret caret = (DefaultCaret)jTextAreaConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		scroll.setAutoscrolls(true);
		//Settings for Panels
		Color periwinkleBlue = new Color( 204, 204, 255);
		Color periwinkleBlueD = new Color( 175, 175, 255);
		//Top Panel
		jPanelTop.setLayout( (LayoutManager) new BorderLayout());
		jPanelTop.setBackground( periwinkleBlueD);
		jPanelTop.add(jLabelTitle, BorderLayout.NORTH);
		jPanelTop.add(jPanelWest, BorderLayout.WEST);
		jPanelTop.add(jPanelCenter, BorderLayout.CENTER);
		jPanelTop.add(jPanelButtons, BorderLayout.SOUTH);
		//West Panel
		jPanelWest.setBackground( periwinkleBlue);
		jPanelWest.add( jLabelIp);
		jPanelWest.add( jLabelPort);
		jPanelWest.setLayout((LayoutManager) new BoxLayout( jPanelWest, BoxLayout.PAGE_AXIS));
		//Port Panel
		jPanelCenter.setBackground( periwinkleBlue);
		jPanelCenter.add( jLabelIpValue);
		jPanelCenter.add( jTextFieldPort);
		jPanelCenter.setLayout((LayoutManager) new BoxLayout( jPanelCenter, BoxLayout.PAGE_AXIS));
		//Port Panel
		jPanelButtons.setBackground( periwinkleBlue);
		jPanelButtons.add( jButtonStart);
		jPanelButtons.add( jButtonStop);
		jPanelButtons.add( jButtonClear);
		//Bottom Panel
		jPanelBottom.setBackground( periwinkleBlue);
		jPanelBottom.add( scroll );

	}
	public static void main(String[] args) throws IOException
	{
		JFrame jFrameMain = new JFrame("Server Ayarlari");
		createGUI();
		//Settings for Frame
		jFrameMain.setLayout((LayoutManager) new BoxLayout( jFrameMain.getContentPane(), BoxLayout.PAGE_AXIS));
		jFrameMain.getContentPane().add(jPanelTop);
		jFrameMain.getContentPane().add(jPanelBottom);
		jFrameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrameMain.setResizable(false);
		jFrameMain.pack();
		jFrameMain.setLocationRelativeTo(null);
		jFrameMain.setVisible(true);

		//Action part of GUI 

		jButtonStop.addActionListener(new ActionListener() 
		{ 
			public void actionPerformed(ActionEvent e) 
			{ 
				breakFlag = true;
				try {
					listener.close();
					jButtonStop.setEnabled(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
		} );
		final Runnable r = new Runnable() {
			public void run() {
				try {
					jButtonStop.setEnabled(true);
					runServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		final ExecutorService executor = Executors.newCachedThreadPool();
		//executor.submit(r);
		jButtonStart.addActionListener(new ActionListener() 
		{ 
			public void actionPerformed(ActionEvent e) 
			{ 
				try {

					hede = true;
					breakFlag = false;
					executor.submit(r);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} 
		} );
		
		jButtonClear.addActionListener(new ActionListener() 
		{ 
			public void actionPerformed(ActionEvent e) 
			{ 
				jTextAreaConsole.setText("");
				jTextAreaConsole.setText(jTextAreaConsole.getText()+"******************************"
						+ "*******************************************************************************\n");
				jTextAreaConsole.setText(jTextAreaConsole.getText()+"\t\t                    BILISIM A.S.\n");
				jTextAreaConsole.setText(jTextAreaConsole.getText()+"******************************"
						+ "*******************************************************************************\n");
			} 
		} );
	}

	public static boolean breakFlag = false;
	public static ServerSocket listener = null;
	public static boolean hede = true;
	public static void runServer() throws IOException 
	{
		try 
		{
			jTextFieldPort.setText(jTextFieldPort.getText().trim());
			listener = new ServerSocket(new Integer(jTextFieldPort.getText().trim()));
			jButtonStart.setEnabled(false);
			jTextFieldPort.setEnabled(false);

			while (true) 
			{
				try 
				{
					if(breakFlag)
					{
						jButtonStart.setEnabled(true);
						jTextFieldPort.setEnabled(true);
						break;
					}
					if(hede)
						jTextAreaConsole.setText(jTextAreaConsole.getText()+"Server dinlemeye basladi... Port: "+jTextFieldPort.getText().trim()+"\n");
					hede = false;
					socket = listener.accept();
					th = new Thread(new Server(socket));
					th.start();
				} 
				catch(Exception e) 
				{
					jTextAreaConsole.setText(jTextAreaConsole.getText()+"Server Durduruldu!\n");
				}
			}
		}
		catch(Exception e)
		{
			jTextAreaConsole.setText(jTextAreaConsole.getText()+"Error! Error! Panik! x( "+e.getMessage()+"\n");
			return;
		}
		finally 
		{
			//socket.close();
			listener.close();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void run() 
	{
		try 
		{
			if(!breakFlag)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                out.println("Enter a line with only a period to quit\n");
                jTextAreaConsole.setText(jTextAreaConsole.getText()+in.readLine()+"\n");
//				System.out.println("ddddfsdfsdf");
//				InputStream is = socket.getInputStream();
//				inStream = new ObjectInputStream(is);
//				jTextAreaConsole.setText(jTextAreaConsole.getText()+inStream.readObject()+"\n");
			}
		} 
		catch (IOException e) 
		{
			System.out.println(e+" ERROR");
			e.printStackTrace();
		} 
		finally 
		{
			th.stop();
			try 
			{
				nsocket.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
