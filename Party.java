import java.net.URLEncoder;
import java.util.ArrayList;

public class Party
{
	private PartyInfo info;
	private boolean isActive = false;
	public boolean firstInvite = false;
	public PartyWindow window = null;
	
	private static ArrayList<ChattedListener> chattedListeners = new ArrayList<ChattedListener>();
	private ArrayList<ConversationInfo> messages = new ArrayList<ConversationInfo>();
	
	public Party()
	{
		window = new PartyWindow(this);
	}
	
	public void inviteFirst(String toInvite)
	{
		boolean worked = true;
		
		try
		{
			String response = EasyHttp.getWithToken(String.format(Links.CreateParty, "%s", toInvite));
			
			this.info = Launcher.gson.fromJson(response, PartyInfo.class);
			
			if (info.Error != null && !info.Error.isEmpty())
				Launcher.error(info.Error);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			worked = false;
		}
		
		this.isActive = worked;
	}
	
	public boolean leaveParty()
	{
		boolean worked = true;
		
		try
		{
			isActive = false;
			firstInvite = false;
			info = null;
			String response = EasyHttp.getWithToken(String.format(Links.KickFromParty, "%s", Launcher.userId));
			ErrorInfo errorInfo = Launcher.gson.fromJson(response, ErrorInfo.class);

			if (errorInfo.Error.isEmpty())
				System.out.println("Succesfully left party");
			else
				System.out.println("Failed to leave party. " + errorInfo.Error);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			worked = false;
		}
		
		return worked;
	}
	
	public void kickFromParty(int userId)
	{
		String response = EasyHttp.getWithToken(String.format(Links.KickFromParty, "%s", userId));
		ErrorInfo errorInfo = Launcher.gson.fromJson(response, ErrorInfo.class);

		if (errorInfo.Error.isEmpty())
		{
			System.out.println("Succesfully kicked " + userId + " from party");
		}
		else
		{
			Launcher.error("Failed to kick from party. " + errorInfo.Error);
		}
	}
	
	public void inviteUser(String user)
	{
		if (firstInvite)
		{
			String response = EasyHttp.getWithToken(String.format(Links.InviteToParty, "%s", user));
			
			if (response.isEmpty())
			{
				Launcher.error("Something went wrong!");
			}
			else
			{
				ErrorInfo info = Launcher.gson.fromJson(response, ErrorInfo.class);
				
				if (info.Error != null)
				{
					if (!info.Error.isEmpty())
						Launcher.error(info.Error);
				}
				else
					Launcher.error("Something went wrong.");
			}
		}
		else
			inviteFirst(user);
		
		this.window.setVisible(true);
	}
	
	public void sendMessage(String message)
	{
		if (message != null && !message.isEmpty())
		{
			try
			{
				String response = EasyHttp.postWithToken(String.format(Links.SendMessage, "%s", this.info.PartyGuid), "message=" + URLEncoder.encode(message, "UTF-8"));
				
				if (response.equals("OK") || response.equals("{\"Error\" : \"\"}"))
				{
					for (int i = 0; i < chattedListeners.size(); i++)
					{
						chattedListeners.get(i).onChatted(Launcher.userName, Launcher.userId, this.getThumbFromId(Launcher.userId), message);
					}
				}
				else if (!response.isEmpty())
					Launcher.error(message);
			}
			catch (Exception e)
			{
				Launcher.error(e.getMessage() + " | " + e.getCause());
			}
		}
	}
	
	/**
	 * <b>0</b> = worked<br>
	 * <b>1</b> = error<br>
	 * <b>2</b> = disabled party
	 */
	public int doUpdate()
	{
		int success = 0;
	
		if (isActive)
		{
			String response = "";
			
			try
			{
				response = EasyHttp.getWithToken(Links.GetPartyInfo);
				this.info = Launcher.gson.fromJson(response, PartyInfo.class);
				
				for (int i = 0; i < info.Conversation.length; i++)
				{
					if (info.Conversation[i] != null)
					{
						ConversationInfo message = info.Conversation[i];
						
						if (!this.getMessageRecieved(message))
						{
							for (int j = 0; j < chattedListeners.size(); j++)
							{
								chattedListeners.get(j).onChatted(message.SenderUserName, message.SenderUserID, this.getThumbFromId(message.SenderUserID), message.Message);
							}
						}
					}
					else
						break;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				success = 1;
			}
		}
		else
			success = 2;
		
		return success;
	}
	
	public String getThumbFromId(int userId)
	{
		String thumb = "";
		
		for (int i = 0; i < info.Members.length; i++)
		{
			if (info.Members[i] != null && info.Members[i].UserID == userId)
			{
				thumb = info.Members[i].Thumbnail;
			}
			else
				break;
		}
		
		if (thumb.isEmpty())
		{
			thumb = "http://t0.rbxcdn.com/69775a68824621abd41d69d9a6c8b308"; // TODO: Better image?
		}
		
		return thumb;
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
			messages.add(message); // Probably should do this in a more official way, but oh well!
		
		return recieved;
	}
	
	public void addStatusListener(ChattedListener chattedListener)
	{
		chattedListeners.add(chattedListener);
	}
}
