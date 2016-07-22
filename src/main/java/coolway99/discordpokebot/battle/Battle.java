package coolway99.discordpokebot.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.TreeMap;

import coolway99.discordpokebot.MoveConstants;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Effects.Volatile;
import coolway99.discordpokebot.states.Moves;
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
			builder.append(player.user.mention());
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
		synchronized(this.attacks){
			if(this.attacks.containsKey(attacker)) {
				Pokebot.sendMessage(channel,
						attacker.user.mention() + " you've already sent your attack!");
				return;
			}
			this.attacks.put(attacker, new IAttack(attacker, move, defender));
			if( !channel.getID().equals(this.channel.getID())) {
				Pokebot.sendMessage(this.channel,
						attacker.user.mention() + " sent in their attack from another channel!");
			} else {
				Pokebot.sendMessage(this.channel,
						attacker.user.mention() + " submitted their attack");
			}
			attacker.lastMove = move;
			attacker.lastTarget = (move.hasTarget() ? defender : null);
			if(move.hasTarget())
				defender.lastAttacker = attacker; //TODO free-for-all battles might make this weird...
			if(this.attacks.size() == this.participants.size()) {
				this.timer.cancel();
				this.onTurn();
			}
		}
	}
	
	//Called for moves that auto-set themselves
	public void onAutoAttack(Player attacker, Moves move, Player defender){
		synchronized(this.attacks){
			this.attacks.put(attacker, new IAttack(attacker, move, defender));
			Pokebot.sendMessage(this.channel, attacker.user.mention() + " has a multiturn attack!");
		}
	}
	
	//This is called every time the BattleTurnTimer times out, or if onAttack is completely filled
	public void onTurn(){
		synchronized(this.attacks){
			Pokebot.sendMessage(this.channel, "Processing attacks");
			Pokebot.startBatchMessages(this);
			//We make a "note" of those who hadAttacks, to prevent flinch from glitching things up
			//TODO Perhaps make it a single list for the IAttacks, and have flinching remove it on-attack-time
			final List<Player> hadAttacks = Arrays
					.asList(this.attacks.keySet().toArray(new Player[0]));
			attackLoop: for(IAttack attack : this.attacks.values()) {
				if( !this.participants.contains(attack.defender)) {
					Pokebot.sendBatchableMessage(this.channel, attack.attacker.mention()
							+ " went to attack, but there was no target!");
					continue;
				}
				//We know it's sorted by speed, so only the fastest go first
				if(Moves.attack(this.channel, attack)) {
					if(attack.defender.lastMove == Moves.DESTINY_BOND) {
						Pokebot.sendBatchableMessage(this.channel, attack.attacker.mention()
								+ " was taken down with " + attack.defender.mention());
						attack.attacker.HP = 0;
					}
					if(playerFainted(attack.defender)) {
						Pokebot.endBatchMessages();
						return;
					}
					if(attack.defender.ability == Abilities.AFTERMATH) {
						for(Player player : this.participants) {
							if(player == attack.defender)
								continue;
							if(player.ability == Abilities.DAMP) {
								this.preventExplosion(player, attack.defender);
								//If the player has <= 0 HP, then the after-attacks check will catch it
								continue attackLoop;
							}
						}
						//Else we actually do damage
						attack.attacker.HP -= attack.attacker.getMaxHP() / 4;
					}
				}
				//Both a recoil check and a failsafe
				if(attack.attacker.HP <= 0) {
					Pokebot.sendBatchableMessage(this.channel,
							attack.attacker.mention() + " fainted!");
					if(playerFainted(attack.attacker)) {
						Pokebot.endBatchMessages();
						return;
					}
				}
			}
			//TODO after-turn things
			//Doing various checks for damage
			for(Player player : this.participants) {
				switch(player.getNV()){
					case BURN: {
						//TODO Check for ability heatproof
						player.HP = Math.max(0, player.HP - (player.getMaxHP() / 8));
						Pokebot.sendBatchableMessage(this.channel,
								player.mention() + " took damage for it's burn!");
						break;
					}
					case POISON: {
						//TODO check for poison heal ability
						player.HP = Math.max(0, player.HP - (player.getMaxHP() / 8));
						Pokebot.sendBatchableMessage(this.channel,
								player.mention() + " took damage from poison!");
						break;
					}
					case TOXIC: {
						//TODO check for poison heal ability
						player.HP = Math.max(0,
								player.HP - (player.getMaxHP() * ++player.counter / 16));
						Pokebot.sendBatchableMessage(this.channel,
								player.mention() + " took damage from poison!");
						break;
					}
					default:
						break;
				}
				if(player.HP <= 0) {
					Moves.faintMessage(this.channel, player);
					if(playerFainted(player))
						return;
				}
			}
			//We check for those who didn't do anything:
			this.threatenTimeout.removeAll(hadAttacks); //Those that attacked this turn get forgiven if they were absent previously
			for(Player player : this.threatenTimeout) {
				this.onSafeLeaveBattle(player);
				Pokebot.sendBatchableMessage(this.channel,
						player.mention() + " got eliminated for inactivity!");
			}
			this.threatenTimeout.clear();
			if(checkDefaultWin())
				return;
			this.threatenTimeout.addAll(this.participants);
			this.threatenTimeout.removeAll(hadAttacks);
			for(Player player : this.threatenTimeout) {
				Pokebot.sendBatchableMessage(this.channel, "If " + player.user.mention()
						+ " doesn't attack the next turn, they're out!");
				player.lastTarget = null;
				player.lastMove = Moves.NULL;
				player.lastAttacker = null;
			}
			this.attacks.clear();
			Pokebot.sendBatchableMessage(this.channel,
					"Begin next turn, you have " + this.turnTime + " seconds to make your attack");
			Pokebot.endBatchMessages();
			this.timer = new BattleTurnTimeout(this);
			Pokebot.timer.schedule(this.timer, Pokebot.secondsToMiliseconds(this.turnTime));
			for(Player player : this.participants) {
				this.onPostTurn(player);
			}
		}
	}
	
	public void preventExplosion(Player player, Player attacker){
		Pokebot.sendBatchableMessage(this.channel, "But "+player.mention()+"'s DAMP prevented"
				+ attacker.mention()+"'s explosion!");
	}
	
	/**
	 * Returns true if the battle has ended
	 */
	public boolean playerFainted(Player player){
		onSafeLeaveBattle(player);
		if(this.participants.size() == 1){
			player = this.participants.get(0);
			this.participants.clear();
			BattleManager.onBattleWon(this, player);
			this.timer.cancel();
			return true;
		}
		return false;
	}
	
	//Should we stop execution?
	public boolean checkDefaultWin(){
		if(this.participants.size() == 1){
			Player player = this.participants.remove(0);
			this.onLeaveBattle(player);
			Pokebot.sendBatchableMessage(this.channel, player.user.mention()+" won the battle by default!");
			BattleManager.battles.remove(this);
			return true;
		}
		if(this.participants.size() <= 0){
			Pokebot.sendBatchableMessage(this.channel, "Nobody won...");
			BattleManager.battles.remove(this);
			return true;
		}
		return false;
	}
	
	public boolean onLeaveBattle(Player player){
		BattleManager.onExitBattle(player);
		this.participants.remove(player);
		this.attacks.remove(player);
		this.threatenTimeout.remove(player);
		return checkDefaultWin();
	}
	
	private void onSafeLeaveBattle(Player player){
		BattleManager.onExitBattle(player);
		this.participants.remove(player);
	}
	
	public void addParticipant(Player player){
		this.participants.add(player);
	}
	
	//This is ran after all the battle logic
	/**
	 * Used to run things like post-turn damage.
	 * 
	 * Attacks are already reset by this point, 
	 * so any multi-turn attacks can auto-queue themselves without consequence
	 */
	public void onPostTurn(Player player){
		
		switch(player.lastMove){
			case FLY:{
				switch(player.lastMovedata){
					case MoveConstants.FLYING:{
						//This attack has charged up
						this.onAutoAttack(player, player.lastMove, player.lastTarget);
						break;
					}
					default:{
						break;
					}
				}
				break;
			}
			default:
				break;
		}
		
		/*{
			Iterator<Effects.Volatile> i = player.vEffects.iterator();
			while(i.hasNext()){
				switch(i.next()){
					case FLINCH:{
						//in this case, 
						continue;
					}
					default:
						continue;
				}
			}
		}*/
	}

	//Flinching can only occur if the attacker attacked first.
	public void flinch(Player player){
		this.attacks.remove(player);
		player.remove(Volatile.FLINCH);
	}
	
	@Override
	public int compare(Player o1, Player o2){
		return o2.getSpeedStat() - o1.getSpeedStat();
	}

}
