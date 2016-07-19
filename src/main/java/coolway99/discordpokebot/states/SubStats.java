package coolway99.discordpokebot.states;

public enum SubStats{
	BASE(0),
	IV(1),
	EV(2);

	private final int index;

	private SubStats(int x){
		this.index = x;
	}

	public int getIndex(){
		return this.index;
	}

	public static SubStats getSubStatFromIndex(int i){
		switch(i){
			case 0:
				return BASE;
			case 1:
				return IV;
			case 2:
				return EV;
			default:
				return null;
		}
	}

	public static SubStats getSubStatFromString(String s){
		if(s == null) return SubStats.BASE;
		switch(s.toLowerCase()){
			case "d": //Sometimes called "DV"
			case "dv":
			case "i":
			case "iv":{
				return SubStats.IV;
			}
			case "ev":
			case "e":{
				return SubStats.EV;
			}
			default:{
				return SubStats.BASE;
			}
		}
	}
}