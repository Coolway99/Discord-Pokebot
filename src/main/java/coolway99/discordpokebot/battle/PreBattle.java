package coolway99.discordpokebot.battle;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PreBattle{

	private final IUser host;
	public final IChannel channel;
	public final ArrayList<Player> participants;
	public final int turnTimer;
	public final PreBattleTimeout waitTimer;
	
	public PreBattle(IChannel channel, Player host, int turnTimer){
		this.channel = channel;
		this.host = host.user;
		this.turnTimer = turnTimer;
		this.participants = new ArrayList<>();
		this.participants.add(host);
		this.waitTimer = new PreBattleTimeout(this);
		Pokebot.timer.schedule(this.waitTimer, BattleManager.BATTLE_TIMEOUT-1, TimeUnit.MINUTES);
	}

	@SuppressWarnings("BooleanParameter")
	public void onTimer(boolean alreadyNotified){
		if(alreadyNotified){
			BattleManager.preBattles.remove(this.host);
			Pokebot.sendMessage(this.channel, "The battle invite by "+this.host.mention()+" has expired");
			return;
		}
		Pokebot.sendMessage(this.channel, "The invite for the battle hosted by "+this.host.mention()+" will expire in " +
				"one minute!");
		Pokebot.timer.schedule(this.waitTimer, 1, TimeUnit.MINUTES);
	}

	public void onBattleStarting(){
		this.waitTimer.cancel();
	}
}
