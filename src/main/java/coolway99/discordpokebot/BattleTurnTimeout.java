package coolway99.discordpokebot;

import java.util.TimerTask;

public class BattleTurnTimeout extends TimerTask{
	
	private final Battle battle;
	
	public BattleTurnTimeout(Battle battle){
		this.battle = battle;
	}

	@Override
	public void run(){
		this.battle.onTurn();
	}
}
