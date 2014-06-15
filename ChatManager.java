import java.util.ArrayList;

public class ChatManager
{
	public static ArrayList<ChatWindow> chats = new ArrayList<ChatWindow>();
	
	public static ChatWindow newChat(int userid, String username)
	{
		for (int i = 0; i < chats.size(); i++)
		{
			if (chats.get(i).userid == userid)
			{
				chats.get(i).setVisible(true);
				return chats.get(i);
			}
		}
		
		ChatWindow chat = new ChatWindow(userid, username);
		chats.add(chat);
		return chat;
	}
	
	public static void doUpate()
	{
		String activeChats = "";
		
		for (int i = 0; i < chats.size(); i++)
		{
			activeChats += chats.get(i).userid + "%2C";
		}
		
		String response = "";
			
		try
		{
			response = EasyHttp.getNoToken(String.format(Links.GetChatInfo, activeChats, activeChats));
			ChatInfo info = Launcher.gson.fromJson(response, ChatInfo.class);
				
			for (int cIndex = 0; cIndex < info.Chats.length; cIndex ++)
			{
				boolean found = false;
				
				for (int i = 0; i < chats.size(); i++)
				{
					ChatWindow chat = chats.get(i);
					
					if (info.Chats[cIndex].SenderID == chat.userid)
					{
						found = true;
						
						for (int j = 0; j < info.Chats[cIndex].Conversation.length; j++)
						{
							if (info.Chats[cIndex].Conversation[j] != null)
							{
								ConversationInfo message = info.Chats[cIndex].Conversation[j];
								
								if (!chat.getMessageRecieved(message))
								{
									chat.addMessage(message.SenderUserName, message.SenderUserID, "", message.Message);
								}
							}
							else
								break;
						}
					}
				}
				
				if (!found)
				{
					ChatWindow chat = ChatManager.newChat(info.Chats[cIndex].SenderID, info.Chats[cIndex].SenderUserName);
					
					if (info.Chats[cIndex].SenderID == chat.userid)
					{
						for (int j = 0; j < info.Chats[cIndex].Conversation.length; j++)
						{
							if (info.Chats[cIndex].Conversation[j] != null)
							{
								ConversationInfo message = info.Chats[cIndex].Conversation[j];
								
								if (!chat.getMessageRecieved(message))
								{
									chat.addMessage(message.SenderUserName, message.SenderUserID, "", message.Message);
								}
							}
							else
								break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
