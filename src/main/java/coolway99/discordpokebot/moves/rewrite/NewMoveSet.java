package coolway99.discordpokebot.moves.rewrite;

public class NewMoveSet{
	private final MoveWrapper move;
	private int PP;

	public NewMoveSet(MoveWrapper move){
		this.move = move;
		this.PP = move.getPP();
	}

	public MoveWrapper getMove(){
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
