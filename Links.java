public class Links
{
	/**
	 * The link to create a party.
	 * <br><br>
	 * <b>%s</b> token<br>
	 * <b>%s</b> username<br>
	 */
	public static final String CreateParty = "http://www.roblox.com/chat/party.ashx?token=%s&reqtype=createAndInvite&userName=%s";
	
	/**
	 * The link to invite somebody to a party.
	 * <br><br>
	 * <b>%s</b> tolen<br>
	 * <b>%s</b> username<br>
	 */
	public static final String InviteToParty = "http://www.roblox.com/chat/party.ashx?token=%s&reqtype=inviteUser&userName=%s";
	
	/**
	 * The link to send a message to a party.
	 * <br><br>
	 * <b>%s</b> token<br>
	 * <b>%s</b> partyGuid<br>
	 */
	public static final String SendMessage = "http://www.roblox.com/chat/send.ashx?token=%s&partyGuid=%s";
	
	/**
	 * The link to leave a party. Use own user id to kick self.
	 * <br><br>
	 * <b>%s</b> token<br>
	 * <b>%d</b> userID<br>
	 */
	public static final String KickFromParty = "http://www.roblox.com/chat/party.ashx?token=%s&reqtype=removeUser&userID=%d";
	
	/**
	 * The link to get updated info for party.
	 * <br><br>
	 * <b>%s</b> token<br>
	 */
	public static final String GetPartyInfo = "http://www.roblox.com/chat/party.ashx?token=%s&reqtype=get";
	
	/**
	 * The link to get all friends online.
	 * <br><br>
	 * <b>%s</b> token
	 */
	public static final String GetFriendsOnline = "http://www.roblox.com/chat/friendhandler.ashx?cmd=friends&token=%s";
	
	/**
	 * The link to get all best friends online.
	 * <br><br>
	 * <b>%s</b> token
	 */
	public static final String GetBestFriendsOnline = "http://www.roblox.com/chat/friendhandler.ashx?cmd=bestfriends&token=%s";
	
	/**
	 * The link to get all the people you've recently talked to.
	 * <br><br>
	 * <b>%s</b> token
	 */
	public static final String GetRecentChats = "http://www.roblox.com/chat/friendhandler.ashx?cmd=recents&token=%s";
	
	/**
	 * The link to get all of the current chats.
	 * <br><br>
	 * <b>%s</b> Open chats (userid + %2C spacer?) <i>TODO: Figure out where to put %2C</i><br>
	 * <b>%s</b> Active chats (same as above)
	 */
	public static final String GetChatInfo = "http://www.roblox.com/chat/get.ashx?reqType=getallchatswithdata&openChatTabs=%s&fullget=true&activechatids=%s&getstatusinfo=false&getpartystatus=false&timeZoneOffset=360";
	
	/**
	 * The link to send a chat message.
	 * <br><br>
	 * <b>%s</b> token
	 * <b>%d</b> userid
	 */
	public static final String SendChatMessage = "http://www.roblox.com/chat/send.ashx?token=%s&recipientUserId=%d";
	
	/**
	 * The link to get the username of a player from their id.
	 * <br><br>
	 * <b>%d</b> userid
	 */
	public static final String GetUserInfo = "http://api.roblox.com/users/%d";
	
	/**
	 * The link to login. Must be a post with username=%s&password=%s
	 */
	public static final String Login = "https://www.roblox.com/newlogin";
	
	/**
	 * The link to accept party invites.
	 * <br><br>
	 * <b>%s</b> token
	 */
	public static final String AcceptPartyInvite = "http://www.roblox.com/chat/party.ashx?token=%s&reqtype=acceptInvite";
}