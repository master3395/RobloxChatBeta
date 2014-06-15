import java.awt.Image;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Cache
{
	private ArrayList<CacheItem> items = new ArrayList<CacheItem>();
	
	public Image boxTL = null;
	public Image boxTR = null;
	public Image boxBL = null;
	public Image boxBR = null;
	public ImageIcon partyIcon = null;
	public ImageIcon chatIcon = null;
	
	public ArrayList<Image> iconImages = new ArrayList<Image>();
	
	public Cache()
	{
		try
		{
			boxTL = ImageIO.read(getClass().getResource("/imgs/ChatTopLeft.png"));
			boxTR = ImageIO.read(getClass().getResource("/imgs/ChatTopRight.png"));
			boxBL = ImageIO.read(getClass().getResource("/imgs/ChatBottomLeft.png"));
			boxBR = ImageIO.read(getClass().getResource("/imgs/ChatBottomRight.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		iconImages.add(new ImageIcon(getClass().getResource("/imgs/favicon.png")).getImage());
		iconImages.add(new ImageIcon(getClass().getResource("/imgs/favicon128.png")).getImage());
		iconImages.add(new ImageIcon(getClass().getResource("/imgs/favicon64.png")).getImage());
		iconImages.add(new ImageIcon(getClass().getResource("/imgs/favicon32.png")).getImage());
		iconImages.add(new ImageIcon(getClass().getResource("/imgs/favicon16.png")).getImage());
		
		partyIcon = new ImageIcon(getClass().getResource("/imgs/PartyIcon.png"));
		chatIcon = new ImageIcon(getClass().getResource("/imgs/ChatIcon.png"));
	}
	
	public void add(String key, Object value)
	{
		items.add(new CacheItem(key, value));
	}
	
	public Object get(String key)
	{
		Object toReturn = null;
		
		for (int i = 0; i < items.size(); i++)
		{
			if (items.get(i).key.equals(key))
			{
				toReturn = items.get(i).value;
				break;
			}
		}
		
		return toReturn;
	}
}