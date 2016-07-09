package coolway99.discordpokebot;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class PreBattle{

	public final IUser host;
	public final IChannel channel;
	public final List<Player> participants;
	public final int turnTimer;
	public TimerTask waitTimer;
	
	public PreBattle(IChannel channel, Player host, int turnTimer){
		this.channel = channel;
		this.host = host.getUser();
		this.turnTimer = turnTimer;
		this.participants = new ArrayList<>();
		this.participants.add(host);
		this.waitTimer = new PreBattleTimout(this, false);
		Pokebot.timer.schedule(this.waitTimer, Pokebot.minutesToMiliseconds(BattleManager.BATTLE_TIMEOUT-1));
	}
	
	public void onTimer(boolean alreadyNotified){
		if(alreadyNotified){
			BattleManager.preBattles.remove(this.host);
			Pokebot.sendMessage(this.channel, "The battle started by "+this.host.mention()+" has expired");
			return;
		} //TODO perhaps these messages are vauge? The battle hasn't "started" yet
		Pokebot.sendMessage(this.channel, "The battle started by "+this.host.mention()+" will expire in one minute!");
		this.waitTimer = new PreBattleTimout(this, true);
		Pokebot.timer.schedule(this.waitTimer, Pokebot.minutesToMiliseconds(1));
	}
	
	public void onBattleStarting(){
		this.waitTimer.cancel();
	}
}
