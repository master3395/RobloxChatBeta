import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class PartyWindow extends JFrame
{
	private Party theParty;
	private ArrayList<MessageBox> boxes = new ArrayList<MessageBox>();
	private PartyWindow window = this;
	
	public PartyWindow(Party party)
	{
		super("Party");
		theParty = party;
		Container pane = getContentPane();
		setIconImages(Launcher.cache.iconImages);
		
		setTitle("Party");
		setSize(new Dimension(300, 500));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		pane.setLayout(new GridBagLayout());
		pane.setComponentOrientation(ComponentOrientation.UNKNOWN);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		if (true) // TODO: Add something that only allows party creator to invite people
		{
			JButton invite = new JButton("Invite");
			invite.setFocusable(false);
			invite.setPreferredSize(new Dimension(0, 40));
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.5;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.PAGE_START;
			pane.add(invite, gbc);
			
			invite.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					String user = JOptionPane.showInputDialog(window, "Who do you wish to invite?");
					
					if (user != null && !user.isEmpty())
					{
						theParty.inviteUser(user);
					}
				}
			});
		}
		
		JButton leaveParty = new JButton("Leave");
		leaveParty.setFocusable(false);
		leaveParty.setPreferredSize(new Dimension(0, 40));
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		gbc.gridx = true ? 1 : 0; // TODO: Only moved to 1 if block above is active
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.PAGE_START;
		leaveParty.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				theParty.leaveParty(); // Has the possibility of returning an false if we're not in a party, but we'll just ignore that
				setVisible(false);
			}
		});
		pane.add(leaveParty, gbc);
		
		final JEditorPane chats = new JEditorPane("text/html", "<html></html>");
		chats.setEditable(false);
		chats.setBackground(getBackground());
		
		HTMLEditorKit kit = new HTMLEditorKit();
		chats.setEditorKit(kit);
		
		StyleSheet styles = kit.getStyleSheet();
		styles.addRule(".chatBox { background-color: #c2eeff; border-color: #333333; border-style: solid; border-width: 1px; border-radius: 3px; padding-top: 0px; padding-bottom: 2px; padding-left: 6px; padding-right: 4px; margin-bottom: 3px; }");
		styles.addRule(".b { font-family: Arial; }");
		
		JScrollPane chatsScroll = new JScrollPane(chats);
		chatsScroll.setBorder(BorderFactory.createEmptyBorder());
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		add(chatsScroll, gbc);
		
		final JScrollBar chatsVertical = chatsScroll.getVerticalScrollBar();
		
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
					theParty.sendMessage(chatBox.getText());
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
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.PAGE_END;
		pane.add(chatBoxScroll, gbc);
		
		party.addStatusListener(new ChattedListener()
		{
			@Override
			public void onChatted(String speakerName, int speakerId, String speakerThumb, String message)
			{
				boxes.add(new MessageBox(speakerName, speakerThumb, message));
				String html = "";
				
				for (int i = 0; i < boxes.size(); i++)
				{
					html += boxes.get(i).getHtml();
				}
				
				chats.setText("<html><body style='font-family: Century Gothic'>" + html + "</body></html>");
				
				if (!isFocused() && !isActive())
					Launcher.chatSound();
			}
		});
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
