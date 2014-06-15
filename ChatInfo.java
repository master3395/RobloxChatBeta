public class ChatInfo
{
	public String Error;
	public Chat[] Chats = new Chat[1000]; // If they have support for more than 1k chats at once I'm going to have to kill somebody
	
	class Chat
	{
		public String Thumbnail;
		public boolean Online;
		public boolean CanAcceptChats;
		public int SenderID;
		public String SenderUserName;
		public boolean HasNewMessages;
		public boolean CachedOnClient;
		public boolean ShowInviteLink;
		public ConversationInfo[] Conversation = new ConversationInfo[1000];
	}
}