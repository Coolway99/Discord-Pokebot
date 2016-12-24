package coolway99.discordpokebot.misc;

@SuppressWarnings("SpellCheckingInspection")
public enum GameList{
	/*POKEMON("Pokémon"),
	PKMN("Pkmn"),
	GO("Pokémon Go"),
	//GO2("our servers are down"),
	//GO3("Pokémon Go Fuck Yourself"),
	POKERMON("some poker, mon"),
	POKE_A_MON("poke a mon"),
	RYGB("Red/Yellow/Green/Blue"),
	RSE("Ruby/Sapphire/Emerald"),
	GSC("Gold/Silver/Crystal"),
	FRLG("FireRed/LeafGreen"),
	DPPT("Diamond/Pearl/Platinum"),
	HGSS("HeartGold/SoulSilver"),
	BW("Black/White"),
	B2W2("Black2/White2"),
	XY("the ones we don't mention"),
	ORAS("Omega Ruby/Alpha Saphire"),
	//TODO DPPt remakes
	SM("Sun/Moon")*/

	DISCORD_BROKE_STUFF("discord broke everything, that's why the bot didn't work for a bit!"),
	NEW_VERSION("overhaul in development!"),
	TEASE("soon to have new move system where users can easily contribute moves? Sign me up."),
	TEASE2("next update mmight have items~"),
	PLEASE("please help with development! Contact Coolway#7694 for details"),
	;
	
	private final String name;
	
	GameList(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
}
