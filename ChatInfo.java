public class ChatInfo
{
	public String Error;
	public Chat[] Chats = {};
	
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
		public ConversationInfo[] Conversation = {};
	}
}