import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;


public class OptionMenuItem extends JCheckBoxMenuItem
{
	public OptionMenuItem(final String property)
	{
		super(property);
		setSelected(Launcher.properties.getProperty(property, "true").equals("true"));
		
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Launcher.properties.setProperty(property, String.valueOf(getState()));
			}
		});
	}
}
