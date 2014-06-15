import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Launcher
{
	public static String token = "";
	public static String accountCookie = "";
	
	public static Party party = null;
	public static DefaultWindow defaultFrame = null;
	
	public static int userId = 0;
	public static String userName = "";
	
	public static Cache cache = new Cache();
	public static Gson gson = new GsonBuilder().create();
	
	private static String appdata = System.getProperty("os.name").toUpperCase().contains("WIN") ? System.getenv("APPDATA") : System.getProperty("user.home") + "/Library/Application Support";
	public static Properties properties;
	
	private static final int pingTime = 120; // Ping www.roblox.com every 2 minutes or so (to make you always appear online)
	
	public static boolean trayEnabled = false;
	private static final boolean allowMultipleRuns = false; // Just for testing, where I like to terminate my programs improperly
	
	public static final Color[] colors = {
		new Color(252, 118, 118),
		new Color(0xc2eeff),
		new Color(111, 231, 132),
		new Color(222, 102, 255),
		new Color(255, 205, 117),
		new Color(253, 253, 144),
		new Color(255, 170, 255),
		new Color(213, 209, 149)
	};
	
	public static void main(String args[])
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			error("Unable to set look and feel. Please re-install Java.");
			System.exit(1);
		}
		
		try
		{
			new File(appdata + "/RobloxChat/").mkdirs();
			File doubleRun = new File(appdata + "/RobloxChat/isrunning");
			
			if (doubleRun.exists() && !allowMultipleRuns)
				error("Program already running. See system tray to display main window.\nDelete " + appdata + "/RobloxChat/isrunning and restart the program is this is a mistake.");
			else
				doubleRun.createNewFile();
			
			File file2 = new File(appdata + "/RobloxChat/.properties");
			
			if (!file2.exists())
				file2.createNewFile();
			
			properties = new Properties();
			FileInputStream propertiesStream = new FileInputStream(appdata + "/RobloxChat/.properties");
			properties.load(propertiesStream);
			propertiesStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			error("Unable to load properties");
			System.exit(1);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				try
				{
					System.out.println("Saving properties...");
					FileOutputStream out = new FileOutputStream(appdata + "/RobloxChat/.properties");
					properties.store(out, "");
					out.close();
					System.out.println("Saved properties!");
					System.out.println("Allowing other sessions");
					
					if (new File(appdata + "/RobloxChat/isrunning").delete())
						System.out.println("Could not delete safeguard file. Please delete " + appdata + "/RobloxChat/isrunning before restarting.");
					else
						System.out.println("Other sessions allowed");
				}
				catch (Exception e)
				{
					System.out.println("Unable to save properties");
					error("Unable to save properties");
				}
			}
		});
		
		login();
		
		if (SystemTray.isSupported())
		{
			trayEnabled = true;
			PopupMenu trayPopup = new PopupMenu();
			
			MenuItem showWindow = new MenuItem("Show Window");
			showWindow.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Launcher.defaultFrame.setVisible(true);
				}
			});
			trayPopup.add(showWindow);
			
			MenuItem exit = new MenuItem("Exit");
			exit.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			});
			trayPopup.add(exit);
			
			TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(Launcher.class.getResource("/imgs/favicon16.png")));
			trayIcon.setPopupMenu(trayPopup);
			
			try
			{
				SystemTray.getSystemTray().add(trayIcon);
			}
			catch (Exception e)
			{
				trayEnabled = false;
				error(e.getMessage());
			}
		}
		
		defaultFrame = new DefaultWindow();
		party = new Party();
		
		int updates = 0;
		
		while (true)
		{
			try
			{
				party.doUpdate();
				ChatManager.doUpate();
				
				if (updates++ == pingTime)
				{
					updates = 0;
					pingSiteSync();
				}
				
				Thread.sleep(1000); // Sleep 500 ms between scans for new messages
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public static void login()
	{
		final JFrame frame = new JFrame("Please Login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JTextField name = new JTextField("Username");
		name.setPreferredSize(new Dimension(100, 20));
		frame.add(name);
		
		final JTextField id = new JTextField("User ID");
		id.setPreferredSize(new Dimension(100, 20));
		frame.add(id);
		
		final JPasswordField ps = new JPasswordField("Password");
		name.setPreferredSize(new Dimension(100, 20));
		frame.add(ps);
		
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				EasyHttp.login(name.getText(), String.copyValueOf(ps.getPassword()));
				Launcher.userId = Integer.parseInt(id.getText());
				Launcher.userName = name.getText();
			}
		});
		frame.add(login);
		frame.setLayout(new FlowLayout());
		frame.setSize(new Dimension(308, 96));
		frame.setResizable(false);
		frame.setVisible(true);
		
		while (accountCookie.isEmpty())
		{
			try
			{
				Thread.sleep(10);
			}
			catch (Exception e)
			{
				error(e.getMessage());
			}
		}
		
		frame.dispose();
	}
	
	public static synchronized void chatSound() // Thank you to pek on stackoverflow!
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try // Error is somewhere in this chunk
				{
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource("/sounds/chatsound.wav"));
					clip.open(inputStream);
					clip.start();
				}
				catch (Exception e)
				{
					//Launcher.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static synchronized void pingSiteSync() // Still not sure how synchronized functions this works, I'm just going off the above function
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				EasyHttp.getNoToken("http://www.roblox.com/");
			}
		}).start();
	}
	
	public static Color getNameColor(String name) // Thank you Jojomen56!
	{
		if (name.equals("DaMrNelson"))
			return new Color(0x98ff9a);
		
		int value = 0;
		
		for (int index = 0; index < name.length(); index++)
		{
			int cValue = name.charAt(index);
			int reverseIndex = name.length() - index + 2;
			
			if (name.length() % 2 == 1)
				reverseIndex = reverseIndex - 1;
			
			if (reverseIndex % 4 >= 2)
				cValue = -cValue;
			
			value = value + cValue;
		}
		
		return colors[Math.abs(value % 8)];
	}
	
	public static void error(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
