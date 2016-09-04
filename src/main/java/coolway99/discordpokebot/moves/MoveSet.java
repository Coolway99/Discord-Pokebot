package coolway99.discordpokebot.moves;

public class MoveSet{
	private final Move move;
	private int PP;

	public MoveSet(Move move){
		this.move = move;
		this.PP = move.getPP();
	}

	public Move getMove(){
		return this.move;
	}

	public int getPP(){
		return this.PP;
	}

	public int getMaxPP(){
		return this.move.getPP();
	}

	public void setPP(int PP){
		this.PP = PP;
	}

	public void resetPP(){
		this.PP = this.move.getPP();
	}

	public boolean useMove(){
		return this.PP-- > 0;
	}

	public boolean canBeUsed(){
		return this.PP > 0;
	}
}
