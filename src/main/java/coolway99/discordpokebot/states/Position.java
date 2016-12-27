package coolway99.discordpokebot.states;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Position{
	//L2(-2),
	L1(-1),
	CENTER(0),
	R1(1),
	//R2(2),
	;

	private final int pos;

	Position(int pos){
		this.pos = pos;
	}

	@Contract(pure = true)
	public static Position getPos(int pos){
		switch(pos){
			//case -2: return L2;
			case -1: return L1;
			case 0: return CENTER;
			case 1: return R1;
			//case 2: return R2;
			default: return CENTER;
		}
	}

	public int getNumber(){
		return this.pos;
	}

	public int getDistance(@NotNull Position p){
		int p1 = this.getNumber();
		int p2 = p.getNumber();
		return Math.abs(p2 - p1);
	}
}
