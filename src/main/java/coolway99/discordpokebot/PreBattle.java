package coolway99.discordpokebot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class PreBattle{

	public final IUser host;
	public final IChannel channel;
	public final List<Player> participants;
	public final int turnTimer;
	private final Timer waitTimer;
	
	public PreBattle(IChannel channel, Player host, int turnTimer){
		this.channel = channel;
		this.host = host.getUser();
		this.turnTimer = turnTimer;
		this.participants = new ArrayList<>();
		this.participants.add(host);
		this.waitTimer = new Timer("PreBattle Timeout", true); //TODO
	}
	
	public void onBattleStarting(){
		this.waitTimer.cancel();
	}
}
