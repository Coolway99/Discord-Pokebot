package coolway99.discordpokebot.moves.old;

public class OldMoveSet{
	private final OldMove move;
	private int PP;

	public OldMoveSet(OldMove move){
		this.move = move;
		this.PP = move.getPP();
	}

	public OldMove getMove(){
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
