package coolway99.discordpokebot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.TreeMap;

import sx.blah.discord.handle.obj.IChannel;

public class Battle implements Comparator<Player>{

	public final IChannel channel;
	private final List<Player> participants;
	/**
	 * Used to make kick those who are inactive from the battle
	 */
	private final ArrayList<Player> threatenTimeout;
	/**
	 * The map of players to attack. It's a tree map, so when it's iterated through the top speed goes first
	 */
	private final TreeMap<Player, IAttack> attacks;
	/**
	 * Set the timeout for a turn on the battle
	 */
	private final int turnTime;
	private TimerTask timer;
	
	public Battle(IChannel channel, int turnTime, List<Player> participants){
		this.channel = channel;
		this.participants = participants;
		this.turnTime = turnTime;
		this.attacks = new TreeMap<>(this);
		this.threatenTimeout = new ArrayList<>(2);
		
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
		builder.append("\nYou have ");
		builder.append(this.turnTime*2);
		builder.append(" seconds to make your first move!");
		Pokebot.sendMessage(channel, builder.toString());
		Pokebot.startBatchMessages(this);
		this.timer = new BattleTurnTimeout(this);
		Pokebot.timer.schedule(this.timer, Pokebot.secondsToMiliseconds(this.turnTime*2));
	}
	
	public IChannel getChannel(){
		return this.channel;
	}
	
	public List<Player> getParticipants(){
		return this.participants;
	}
	
	public void onAttack(IChannel channel, Player attacker, Moves move, Player defender){
		this.attacks.put(attacker, new IAttack(attacker, move, defender));
		if(!channel.getID().equals(this.channel.getID())){
			Pokebot.sendMessage(this.channel, attacker.user.mention()+" sent in their attack from another channel!");
		} else {
			Pokebot.sendMessage(this.channel, attacker.user.mention()+" submitted their attack");
		}
		if(this.attacks.size() == this.participants.size()){
			this.timer.cancel();
			this.onTurn();
		}
	}
	
	//This is called every time the BattleTurnTimer times out, or if onAttack is completely filled
	public void onTurn(){
		Pokebot.sendMessage(this.channel, "Processing attacks");
		Pokebot.startBatchMessages(this);
		for(IAttack attack : this.attacks.values()){
			//We know it's sorted by speed, so only the fastest go first
			if(Moves.attack(this.channel, attack)){
				if(playerFainted(attack.defender)){
					Pokebot.endBatchMessages();
					return;
				}
				if(attack.attacker.HP == 0){
					Pokebot.sendBatchableMessage(this.channel, attack.attacker.user.mention()+" fainted from recoil!");
					if(playerFainted(attack.attacker)){
						Pokebot.endBatchMessages();
						return;
					}
				}
			}
		}
		//TODO after-turn things
		this.threatenTimeout.removeAll(this.attacks.keySet()); //Those that attacked this turn get forgiven if they were absent previously
		Iterator<Player> i = this.threatenTimeout.iterator();
		while(i.hasNext()){
			Player player = i.next();
			i.remove();
			this.participants.remove(player);
			player.battle = null;
			Pokebot.sendBatchableMessage(this.channel, player.user.mention()+" got eliminated for inactivity!");
		}
		if(this.participants.size() < 2){
			if(this.participants.size() <= 0){
				Pokebot.sendBatchableMessage(this.channel, "Nobody won...");
				BattleManager.battles.remove(this);
				Pokebot.endBatchMessages();
				return;
			}
			//Else, there's one player remaining
			Pokebot.sendBatchableMessage(this.channel, this.participants.get(0).user.mention()+" won by default");
			this.participants.get(0).battle = null;
			BattleManager.battles.remove(this);
			Pokebot.endBatchMessages();
			return;
		}
		this.threatenTimeout.addAll(this.participants);
		this.threatenTimeout.removeAll(this.attacks.keySet());
		for(Player player : this.threatenTimeout){
			Pokebot.sendBatchableMessage(this.channel, "If "+player.user.mention()+" doesn't attack the next turn, they're out!");
		}
		this.attacks.clear();
		Pokebot.sendBatchableMessage(this.channel, "Begin next turn, you have "+this.turnTime+" seconds to make your attack");
		Pokebot.endBatchMessages();
		this.timer = new BattleTurnTimeout(this);
		Pokebot.timer.schedule(this.timer, Pokebot.secondsToMiliseconds(this.turnTime));
	}
	
	/**
	 * Returns true if the battle has ended
	 */
	public boolean playerFainted(Player player){
		//It's good to be thorough
		this.participants.remove(player);
		this.attacks.remove(player);
		this.threatenTimeout.remove(player);
		
		player.battle = null; //TODO perhaps heal them?
		if(this.participants.size() == 1){
			player = this.participants.get(0);
			this.participants.clear();
			BattleManager.onBattleWon(this, player);
			player.battle = null;
			this.timer.cancel();
			return true;
		}
		return false;
	}
	
	public void addParticipant(Player player){
		this.participants.add(player);
	}

	@Override
	public int compare(Player o1, Player o2){
		return o2.getSpeedStat() - o1.getSpeedStat();
	}
}
