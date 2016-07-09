package coolway99.discordpokebot;

import java.util.TimerTask;

public class PreBattleTimout extends TimerTask{
	
	private final PreBattle pre;
	private final boolean hasNotified;
	
	public PreBattleTimout(PreBattle pre, boolean hasNotified){
		this.pre = pre;
		this.hasNotified = hasNotified;
	}
	
	
	@Override
	public void run(){
		this.pre.onTimer(this.hasNotified);
	}

}
