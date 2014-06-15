public class PartyInfo
{
	public int CreatorID;
	public String GameGuid;
	public int PartyGameAsset;
	public String PartyGuid;
	public PartyMembers[] Members = new PartyMembers[50];
	public String CreatorName;
	public ConversationInfo[] Conversation = new ConversationInfo[1000]; // Will ROBLOX ever feed us over 1k messages? I hope not
	public String PartyLeaderIsInGame;
	public String Error;
}