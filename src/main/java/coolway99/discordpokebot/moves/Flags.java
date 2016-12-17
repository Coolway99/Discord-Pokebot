package coolway99.discordpokebot.moves;

public enum Flags{
	ALWAYS_HIT,
	HIT_THROUGH_SEMIINVUL,
	HIT_THROUGH_PROTECTION,
	UNTARGETABLE,
	BYPASSES_IMMUNITIES,
	MULTITURN, //If a move takes more than one turn
	FLIGHT, //If a move requires the use of flying
	CONTACT, //Overrides the default setting
	NO_CONTACT, //Overrides the default setting
	BALLBASED, //The move is based on balls or bombs
	GUST_VULNURABLE,
}