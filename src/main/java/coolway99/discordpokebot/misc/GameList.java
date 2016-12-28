package coolway99.discordpokebot.misc;

@SuppressWarnings("SpellCheckingInspection")
public enum GameList{
	POKEMON("Pokémon"),
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
	SM("Sun/Moon"),
	//TODO DPPt remakes

	DEV("with 10% more unstable code!"),
	;
	
	private final String name;
	
	GameList(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
}
