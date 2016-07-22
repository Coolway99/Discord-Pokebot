package coolway99.discordpokebot.battle;

public class BattleTurnTimeout implements Runnable{
	
	private final Battle battle;
	private boolean canceled;
	
	public BattleTurnTimeout(Battle battle){
		this.battle = battle;
		this.canceled = false;
	}
	
	@Override
	public void run(){
		if(this.canceled) return;
		this.battle.onTurn();
	}
	
	public void cancel(){
		this.canceled = true;
	}
}
