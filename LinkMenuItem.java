import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.JMenuItem;

public class LinkMenuItem extends JMenuItem
{
	public LinkMenuItem(String text, final String link)
	{
		super(text);
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI(link));
				}
				catch (Exception err)
				{
					Launcher.error(err.getMessage());
				}
			}
		});
	}
}
