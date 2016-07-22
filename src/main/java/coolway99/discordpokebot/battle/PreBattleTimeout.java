package coolway99.discordpokebot.battle;

public class PreBattleTimeout implements Runnable{
	
	private final PreBattle pre;
	private boolean hasNotified;
	private boolean canceled;
	
	public PreBattleTimeout(PreBattle pre){
		this.pre = pre;
		this.hasNotified = false;
		this.canceled = false;
	}
	
	
	@Override
	public void run(){
		if(this.canceled) return;
		this.pre.onTimer(this.hasNotified);
		this.hasNotified = true;
	}
	
	public void cancel(){
		this.canceled = true;
	}
}
