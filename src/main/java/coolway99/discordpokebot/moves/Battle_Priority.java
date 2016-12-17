package coolway99.discordpokebot.moves;

public enum Battle_Priority{
	P5(+5),
	P4(+4),
	P3(+3),
	P2(+2),
	P1(+1),
	P0(0),
	N1(-1),
	N2(-2),
	N3(-3),
	N4(-4),
	N5(-5),
	N6(-6),
	N7(-7);

	private final int priority;

	Battle_Priority(int priority){
		this.priority = priority;
	}

	public int getPriority(){
		return this.priority;
	}

	public static Battle_Priority getPriority(String num){
		try{
			return getPriority(Integer.parseInt(num));
		} catch(NumberFormatException e){
			return P0;
		}
	}

	public static Battle_Priority getPriority(int num){
		switch(num){
			case 5: return P5;
			case 4: return P4;
			case 3: return P3;
			case 2: return P2;
			case 1: return P1;
			case 0: return P0;
			case -1: return N1;
			case -2: return N2;
			case -3: return N3;
			case -4: return N4;
			case -5: return N5;
			case -6: return N6;
			case -7: return N7;

			default: return P0;
		}
	}
}
