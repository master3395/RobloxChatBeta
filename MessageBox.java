import org.apache.commons.lang3.StringEscapeUtils;



public class MessageBox
{
	private String username;
	private String characterThumb;
	private String message;
	private int colorIndex = 0;
	
	public MessageBox(String username, String characterThumb, String message)
	{
		super();
		this.username = username;
		this.characterThumb = characterThumb;
		this.message = StringEscapeUtils.escapeHtml4(message);
	}
	
	public String getHtml() // TODO: Add player thumbnails
	{
		if (colorIndex++ >= Launcher.colors.length)
		{
			colorIndex = 0;
		}
		
		return "<div class='chatBox' style='background-color: #" + Integer.toHexString(Launcher.getNameColor(username).getRGB()).substring(2) + "'><b>" + this.username + ":</b> " + this.message + "</div>";
	}
}