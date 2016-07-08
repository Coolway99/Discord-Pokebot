package coolway99.discordpokebot;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import sx.blah.discord.handle.obj.IChannel;

public class Battle{

	public final IChannel channel;
	private final List<Player> participants;
	/**
	 * Set the timeout for a turn on the battle
	 */
	private final int turnTime;
	private final Timer timer;
	
	public Battle(IChannel channel, int turnTime, List<Player> participants){
		this.channel = channel;
		this.participants = participants;
		this.timer= new Timer("Battle"+this.toString(), true);
		this.turnTime = turnTime;
		for(Player player : participants){
			player.battle = this;
		}
		StringBuilder builder = new StringBuilder("A battle has started between ");
		Iterator<Player> i = participants.iterator();
		while(i.hasNext()){
			Player player = i.next();
			builder.append(player.getUser().mention());
			if(i.hasNext()){
				builder.append(", "); //TODO make this neater
			} else {
				builder.append('!');
			}
		}
		Pokebot.sendMessage(channel, builder.toString());
	}
	
	public IChannel getChannel(){
		return this.channel;
	}
	
	public List<Player> getParticipants(){
		return this.participants;
	}
	
	public void onAttack(Player attacker, Moves move, Player defender){
		//TODO
	}
	
	public void removeParticipant(Player player){
		this.participants.remove(player);
		player.battle = null; //TODO perhaps heal them?
		if(this.participants.size() == 1){
			player = this.participants.get(0);
			this.participants.clear();
			BattleManager.onBattleWon(this, player);
			player.battle = null;
			this.timer.cancel();
		}
	}
	
	public void addParticipant(Player player){
		this.participants.add(player);
	}
}
