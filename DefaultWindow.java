import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class DefaultWindow extends JFrame
{
	private Container friendsContainer = null;
	private DefaultWindow window = this;
	
	public DefaultWindow()
	{
		super("ROBLOX Chat Beta");
		
		setSize(new Dimension(500, 160));
		setLocation(new Point(200, 300));
		setResizable(false);
		setIconImages(Launcher.cache.iconImages);
		
		if (Launcher.trayEnabled)
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		else
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container pane = getContentPane();
		pane.setLayout(new FlowLayout());
		
		JButton partyButton = new JButton(Launcher.cache.partyIcon);
		partyButton.setPreferredSize(new Dimension(110, 110));
		partyButton.setFocusable(false);
		partyButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Launcher.party.window.setVisible(true);
			}
		});
		add(partyButton);
		
		JButton chatButton = new JButton(Launcher.cache.chatIcon);
		chatButton.setPreferredSize(new Dimension(110, 110));
		chatButton.setFocusable(false);
		chatButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String user = JOptionPane.showInputDialog(window, "Enter the user ID of the one you wish to chat with");
				
				if (user != null && !user.isEmpty())
				{
					try
					{
						String response = EasyHttp.getNoToken(String.format(Links.GetUserInfo, Integer.parseInt(user)));
						
						if (response != null && !response.isEmpty())
						{
							UserInfo info = Launcher.gson.fromJson(response, UserInfo.class);
							ChatManager.newChat(info.Id, info.Username);
						}
						else
							Launcher.error("Invalid user ID");
					}
					catch (Exception err)
					{
						Launcher.error(err.getMessage());
					}
				}
			}
		});
		add(chatButton);
		
		friendsContainer = new Container();
		friendsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
		
		JScrollPane friendsScroll = new JScrollPane(friendsContainer);
		friendsScroll.setPreferredSize(new Dimension(500, 110));
		friendsScroll.setFocusable(false);
		friendsScroll.setBackground(pane.getBackground());
		friendsScroll.setBorder(BorderFactory.createEmptyBorder());
		friendsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		friendsScroll.getHorizontalScrollBar().setUnitIncrement(16);
		add(friendsScroll);
		
		updateFriends();
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu options = new JMenu("Options");
		menuBar.add(options);
		
		options.add(new OptionMenuItem("Chat Sounds"));
		options.add(new OptionMenuItem("Party Sounds"));
		options.addSeparator();

		JMenu chatDisplays = new JMenu("Chat Display");
		options.add(chatDisplays);
		
		final JCheckBoxMenuItem bffs = new JCheckBoxMenuItem("Best Friends");
		chatDisplays.add(bffs);
		final JCheckBoxMenuItem friends = new JCheckBoxMenuItem("Friends");
		chatDisplays.add(friends);
		final JCheckBoxMenuItem recents = new JCheckBoxMenuItem("Recent");
		chatDisplays.add(recents);
		
		String chatDisplay = Launcher.properties.getProperty("ChatDisplay", "recents");
		
		if (chatDisplay.equals("bestfriends"))
			bffs.setSelected(true);
		else if (chatDisplay.equals("friends"))
			friends.setSelected(true);
		else
			recents.setSelected(true);
		
		bffs.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				bffs.setSelected(true);
				friends.setSelected(false);
				recents.setSelected(false);
				Launcher.properties.setProperty("ChatDisplay", "bestfriends");
				Launcher.defaultFrame.updateFriends();
			}
		});
		
		friends.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				bffs.setSelected(false);
				friends.setSelected(true);
				recents.setSelected(false);
				Launcher.properties.setProperty("ChatDisplay", "friends");
				Launcher.defaultFrame.updateFriends();
			}
		});
		
		recents.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				bffs.setSelected(false);
				friends.setSelected(false);
				recents.setSelected(true);
				Launcher.properties.setProperty("ChatDisplay", "recents");
				Launcher.defaultFrame.updateFriends();
			}
		});
		
		JMenu info = new JMenu("Info");
		menuBar.add(info);
		
		info.add(new LinkMenuItem("Program By: DaMrNelson", "http://www.roblox.com/User.aspx?ID=3925445"));
		info.add(new LinkMenuItem("Token Help: Oxcool1", "http://www.roblox.com/Forum/ShowPost.aspx?PostID=136203616"));
		info.add(new LinkMenuItem("Name Color Detection: Jojomen56", "http://www.roblox.com/--item?id=158495174"));
		info.add(new LinkMenuItem("Name Color Choosing: Legohalo", "http://www.roblox.com/User.aspx?ID=1101691"));
		info.add(new LinkMenuItem("JSON Decoding Api: GSON", "https://code.google.com/p/google-gson/"));
		info.add(new LinkMenuItem("HTML Character Encoding Api: Apache", "http://commons.apache.org/proper/commons-lang/"));
		info.addSeparator();
		info.add(new LinkMenuItem("Source Code", "https://github.com/DaMrNelson/RobloxChatBeta"));
		
		setJMenuBar(menuBar);
		setVisible(true);
		pack();
		
		new Thread()
		{
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(10000); // 10 seconds
						updateFriends();
					}
					catch (Exception e)
					{
						Launcher.error(e.getMessage());
					}
				}
			}
		}.start();
	}
	
	public void updateFriends()
	{
		String friendsToShow = Launcher.properties.getProperty("ChatDisplay", "recent");
		String friendsJson;
		boolean showAll = false;
		
		if (friendsToShow.equals("bestfriends"))
		{
			friendsJson = EasyHttp.getWithToken(Links.GetBestFriendsOnline);
			showAll = true;
		}
		else if (friendsToShow.equals("friends"))
			friendsJson = EasyHttp.getWithToken(Links.GetFriendsOnline);
		else
			friendsJson = EasyHttp.getWithToken(Links.GetRecentChats);
		 
		friendsContainer.removeAll();
			
		if (friendsJson != null && !friendsJson.isEmpty())
		{
			FriendsList friends = Launcher.gson.fromJson(friendsJson, FriendsList.class);
			int friendsFound = 0;
				
			for (int i = 0; i < friends.Count; i++)
			{
				final FriendInfo friend = friends.Users[i];
					
				if (friend.ShowInviteLink)
				{
					friendsFound++;
						
					try
					{
						final JFriendButton friendButton = new JFriendButton(new ImageIcon(new URL(friend.Thumbnail)), friend.Name);
						friendButton.setToolTipText(friend.Name);
						friendButton.setFocusable(false);
						friendButton.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent e)
							{
								String s;
									
								if (friend.CanAcceptChats)
								{
									Object[] choices = {"Chat", "Party"};
									s = (String) JOptionPane.showInputDialog(window, "Select an action...", "Action Selection", JOptionPane.PLAIN_MESSAGE, null, choices, "Chat");
								}
								else
								{
									Object[] choices = {"Party"};
									s = (String) JOptionPane.showInputDialog(window, "Select an action...", "Action Selection", JOptionPane.PLAIN_MESSAGE, null, choices, "Party");
								}
									
								if (s != null)
								{
									if (s.equals("Chat"))
									{
										ChatManager.newChat(friend.ID, friend.Name);
									}
									else if (s.equals("Party"))
									{
										Launcher.party.inviteUser(friend.Name);
									}
								}
							}
						});
						friendsContainer.add(friendButton);
					}
					catch (Exception e)
					{
						Launcher.error(e.getMessage());
					}
				}
			}
				
			if (friendsFound == 0)
			{
					
			}
		}
			
		friendsContainer.revalidate();
		friendsContainer.repaint();
	};
}
