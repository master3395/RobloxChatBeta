import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JButton;


public class JFriendButton extends JButton
{
	String username = "";
	
	public JFriendButton(ImageIcon icon, String name)
	{
		super(icon);
		this.username = name;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2d.setFont(new Font("Arial", Font.PLAIN, 14));
		g2d.setColor(new Color(0x111111));
		g2d.drawString(username, 10, 16);
	}
}