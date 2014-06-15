import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class ChatWindow extends JFrame
{
	public int userid;
	public String username;
	
	private ArrayList<MessageBox> boxes = new ArrayList<MessageBox>();
	private ArrayList<ConversationInfo> messages = new ArrayList<ConversationInfo>();
	private JEditorPane chats;
	
	public ChatWindow(int userid, String username)
	{
		super("Chat with " + username);
		this.userid = userid;
		this.username = username;
		
		setIconImages(Launcher.cache.iconImages);
		setSize(new Dimension(300, 500));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		pane.setComponentOrientation(ComponentOrientation.UNKNOWN);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		chats = new JEditorPane("text/html", "<html></html>");
		chats.setEditable(false);
		chats.setBackground(getBackground());
		
		HTMLEditorKit kit = new HTMLEditorKit();
		chats.setEditorKit(kit);
		
		StyleSheet styles = kit.getStyleSheet();
		styles.addRule(".chatBox { border-color: #333333; border-style: solid; border-width: 1px; border-radius: 3px; padding-top: 0px; padding-bottom: 2px; padding-left: 6px; padding-right: 4px; margin-bottom: 3px; }");
		styles.addRule(".b { font-family: Arial; }");
		System.out.println(pane.getBackground());
		
		JScrollPane chatsScroll = new JScrollPane(chats);
		chatsScroll.setBorder(BorderFactory.createEmptyBorder());
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		add(chatsScroll, gbc);
		
		final JTextArea chatBox = new JTextArea();
		chatBox.setFocusable(true);
		chatBox.setBackground(new Color(0xE2E2E2));
		chatBox.setFont(new Font("Arial", Font.PLAIN, 14));
		chatBox.setForeground(new Color(0x222222));
		chatBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(), BorderFactory.createEmptyBorder(2, 4, 2, 4)));
		chatBox.setWrapStyleWord(true);
		chatBox.setLineWrap(true);
		chatBox.addKeyListener(new KeyListener()
		{
			private boolean shiftDown = false;
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER && !this.shiftDown)
				{
					sendMessage(chatBox.getText());
					chatBox.setText("");
					e.consume();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					chatBox.insert("\n", chatBox.getCaretPosition());
				}
				else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
					this.shiftDown = true;
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_SHIFT)
					this.shiftDown = false;
			}

			@Override
			public void keyTyped(KeyEvent e) {}
		});
		
		JScrollPane chatBoxScroll = new JScrollPane(chatBox);
		chatBoxScroll.setPreferredSize(new Dimension(0, 40));
		chatBoxScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x797979)));
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.PAGE_END;
		pane.add(chatBoxScroll, gbc);
		
		setVisible(true);
	}
	
	public void sendMessage(String message)
	{
		if (message != null && !message.isEmpty())
		{
			try
			{
				String response = EasyHttp.postWithToken(String.format(Links.SendChatMessage, "%s", this.userid), "message=" + URLEncoder.encode(message, "UTF-8"));
				
				if (response.equals("OK") || response.equals("{\"Error\" : \"\"}"))
				{
					addMessage(Launcher.userName, Launcher.userId, "", message); // TOOD: Should I just get rid of this thumb transfering?
				}
				else if (!response.isEmpty())
					Launcher.error(response);
			}
			catch (Exception e)
			{
				Launcher.error(e.getMessage() + " | " + e.getCause());
			}
		}
	}
	
	public void addMessage(String speakerName, int speakerId, String speakerThumb, String message)
	{
		boxes.add(new MessageBox(speakerName, speakerThumb, message));
		String html = "";
		
		for (int i = 0; i < boxes.size(); i++)
		{
			html += boxes.get(i).getHtml();
		}
		
		chats.setText("<html><body style='font-family: Century Gothic'>" + html + "</body></html>");
		
		if (!isFocused() && !isActive() && Launcher.properties.getProperty("Chat Sounds").equals(true))
			Launcher.chatSound();
		
		if (!isVisible())
			setVisible(true);
	}
	
	public boolean getMessageRecieved(ConversationInfo message)
	{
		boolean recieved = false;
		
		if (message.SenderUserID == Launcher.userId)
		{
			recieved = true;
		}
		else
		{
			for (int i = 0; i < messages.size(); i++)
			{
				ConversationInfo oldMsg = messages.get(i);
				
				if (oldMsg.Message.equals(message.Message) && oldMsg.SenderUserName.equals(message.SenderUserName) && oldMsg.TimeSent.equals(message.TimeSent))
				{
					recieved = true;
				}
			}
		}
		
		if (!recieved)
			messages.add(message);
		
		return recieved;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		requestFocusInWindow();
		
		Point parentPoint = Launcher.defaultFrame.getLocation();
		Dimension parentSize = Launcher.defaultFrame.getSize();
		setLocation(new Point(parentPoint.x + parentSize.width + 6, parentPoint.y - 5));
	}
}