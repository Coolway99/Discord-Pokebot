package coolway99.discordpokebot.battles;

import coolway99.discordpokebot.Messages;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.moves.MoveUtils;
import coolway99.discordpokebot.states.Effects;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Battle{

	public final IChannel channel;
	protected final ArrayList<Player> participants;
	/**
	 * Used to make kick those who are inactive from the battle
	 */
	protected final ArrayList<Player> threatenTimeout;
	/**
	 * The set of attacks. It's a tree set, so when it's iterated through the top speed goes first
	 */
	protected final TreeSet<IAttack> attacks;
	/**
	 * The list of players who have already submitted an attack
	 */
	protected final ArrayList<Player> attackers;
	protected int currentTurn = 0;
	/**
	 * Set the timeout for a turn on the battle
	 */
	protected final int turnTime;
	/**
	 * Holds the timer for timing out a turn on a battle. If hasStarted = false this holds the battle expire timeout
	 */
	public ScheduledFuture timer;
	protected final HashMap<BattleEffects, Integer> battleEffects;

	/**
	 * If this battle has started or not
	 */
	protected boolean hasStarted = false;

	public Battle(IChannel channel, int turnTime, ArrayList<Player> participants){
		this.channel = channel;
		this.participants = participants;
		while(this.participants.size() > this.getMaxPlayers()){
			this.participants.remove(this.getMaxPlayers());
		}
		this.participants.trimToSize();
		this.turnTime = turnTime;
		this.attacks = new TreeSet<>(new AttackComparator());
		this.attackers = new ArrayList<>(participants.size());
		this.threatenTimeout = new ArrayList<>(2);
		this.battleEffects = new HashMap<>();
		for(Player player : participants){
			player.battle = this;
		}
	}

	public void setupBattle(){
		this.timer.cancel(true);
		this.hasStarted = true;
		StringBuilder builder = new StringBuilder("A battle has started between ");
		if(this.participants.size() == 2){
			builder.append(this.participants.get(0).mention()).append(" and ");
			builder.append(this.participants.get(1).mention()).append("!");
		} else {
			for(int x = 0; x < this.participants.size(); x++){
				Player player = this.participants.get(x);
				builder.append(player.mention());
				if(x != this.participants.size()-1) builder.append(", ");
				if(x == this.participants.size()-2) builder.append("and ");
				if(x == this.participants.size()-1) builder.append("!");
			}
		}
		builder.append("\nYou have ");
		builder.append(this.turnTime*2);
		builder.append(" seconds to make your first move!");
		this.sendMessage(builder.toString());
		this.timer = Pokebot.timer.schedule(this::onTurn, this.turnTime*2, TimeUnit.SECONDS);
	}

	public void joinBattle(Player player){
		if(this.hasStarted){
			this.sendMessage(player.mention()+", this battle has already started!");
		}
		if(this.participants.size()+1 > this.getMaxPlayers()){
			this.sendMessage(player.mention()+", this battle is full!");
		}
		if(player.inBattle()){
			this.sendMessage(player.mention()+", you're already in a battle!");
		}
		player.battle = this;
		this.participants.add(player);
		this.sendMessage(player.mention()+" has joined the battle!");
	}

	public int getNumberOfPlayers(){
		return this.participants.size();
	}

	protected void sendMessage(String message){
		Pokebot.sendMessage(this.channel, message);
	}

	public ArrayList<Player> getParticipants(){
		return this.participants;
	}

	public void onInputAttack(IChannel channel, Player attacker, MoveSet moveSet, Player defender){
		synchronized(this.attacks){
			if(this.attackers.contains(attacker)){
				this.sendMessage(attacker.mention()+" you've already sent your attack!");
				return;
			}
			if(!this.checkPosition(channel, attacker, moveSet, defender)) return;
			this.attackers.add(attacker);
			this.attacks.add(new IAttack(attacker, moveSet, defender));
			if(!channel.getID().equals(this.channel.getID())){
				this.sendMessage(attacker.mention()+" sent in their attack from another channel!");
			} else {
				this.sendMessage(attacker.mention()+" submitted their attack");
			}
			if(this.attacks.size() == this.participants.size()){
				this.timer.cancel(false);
				//If we're calling this in the event handler thread, we don't want to bog it down
				Pokebot.timer.execute(this::onTurn);
			}
		}
	}

	public abstract boolean checkPosition(IChannel channel, Player attacker, MoveSet moveSet, Player defender);

	//Called for moves that auto-set themselves
	/*protected void onAutoAttack(Player attacker, OldMoveSet move, Player defender){
		synchronized(this.attacks){
			this.attacks.put(attacker, new IAttack(attacker, move, defender));
			this.sendMessage(attacker.mention()+" has a multiturn attack!");
		}
	}*/

	//This is called every time the BattleTurnTimer times out, or if onAttack is completely filled
	public void onTurn(){
		this.currentTurn++;
		this.timer.cancel(false);
		synchronized(this.attacks){
			this.sendMessage("Processing attacks");
			for(IAttack attack : this.attacks){
				if(attack.isCanceled()) continue;
				attack.cancel();
				this.threatenTimeout.remove(attack.attacker);
				if(attack.attacker.has(Effects.NonVolatile.FAINTED)) continue;
				//We know it's sorted by speed, so only the fastest go first
				this.attackLogic(attack);
			}
			//TODO immediate after-turn things
			this.afterTurn();
			if(this.checkWin()){
				BattleManager.runningBattles.remove(this);
				this.participants.forEach(BattleManager::onExitBattle);
				return;
			}
			this.checkForInactivity();
			if(this.checkDefaultWin()){
				BattleManager.runningBattles.remove(this);
				this.participants.forEach(BattleManager::onExitBattle);
				return;
			}
			this.participants.removeIf(player -> {
				if(player.has(Effects.NonVolatile.FAINTED)){
					BattleManager.onExitBattle(player);
					return true;
				}
				return false;
			});
			this.attacks.clear();
			this.sendMessage("Begin next turn, you have "+this.turnTime+" seconds to make your attack");
			this.timer = Pokebot.timer.schedule(this::onTurn, this.turnTime, TimeUnit.SECONDS);
		}
	}

	protected void afterTurn(){
		//Here we do battle-wide effects
		this.battleEffects.replaceAll((effect, integer) -> integer-1);
		this.battleEffects.keySet().removeIf(effect -> {
			if(this.battleEffects.get(effect) <= 0){
				this.sendMessage(effect+" faded away from the field!");
				return true;
			}
			return false;
		});
		//Doing various checks for damage and other things
		for(Player player : this.participants){
			//player.getModifiedItem().onPostTurn(this.channel, player);
			if(player.has(Effects.Volatile.FLINCH)){
				player.remove(Effects.Volatile.FLINCH);
				Messages.stopFlinching(this.channel, player);
			}
			MoveUtils.doEffectDamage(this.channel, player);
			/*if(player.has(Effects.NonVolatile.FAINTED)){
				this.onSafeLeaveBattle(player);
			}*/
		}
	}

	protected void checkForInactivity(){
		//We check for those who didn't do anything:
		for(Player player : this.threatenTimeout){
			this.onSafeLeaveBattle(player);
			this.sendMessage(player.mention()+" got eliminated for inactivity!");
		}
		if(this.checkDefaultWin()) return;
		this.threatenTimeout.clear();
		this.threatenTimeout.addAll(this.participants);
		this.threatenTimeout.removeAll(this.attackers);
		for(Player player : this.threatenTimeout){
			this.sendMessage("If "+player.mention()+" doesn't attack the next turn, they're out!");
			player.lastTarget = null;
			player.lastMove = null;
			player.lastAttacker = null;
		}
		//this.participants.forEach(this::onPostTurn);
	}

	/*protected boolean attackLogic(final IAttack attack){
		//Check for flinch status
		if(attack.attacker.has(Effects.Volatile.FLINCH)){
			this.sendMessage("But "+attack.attacker.mention()+" is flinching!");
			attack.attacker.remove(Effects.Volatile.FLINCH);
			return false;
		}
		if(!this.participants.contains(attack.defender)){
			this.sendMessage(attack.attacker.mention()
					+" went to attack, but there was no target!");
			return false;
		}
		if(OldMove.attack(this.channel, attack)){
			//if(attack.defender.lastMove.getMove() == Move.REGISTRY.get("DESTINY_BOND")){
				this.sendMessage(attack.attacker.mention()
						+" was taken down with "+attack.defender.mention());
				attack.attacker.HP = 0;
			//}
			//if(attack.attacker.lastMove.getMove() == Move.REGISTRY.get("AFTER_YOU")){
				IAttack defenderAttack = this.attacks.get(attack.defender);
				if(defenderAttack != null && !defenderAttack.isCanceled()){
					this.sendMessage(attack.attacker.mention()+" made "+attack.defender.mention()+" go next!");
					//this.attackLogic(defenderAttack.get());
					//defenderAttack.get().cancel();
				} else {
					this.sendMessage("But there wasn't anything to do!"); //TODO better message
				}
			//}
			this.onSafeLeaveBattle(attack.defender);
			if(attack.defender.hasAbility(Abilities.AFTERMATH)){
				for(Player player : this.participants){
					if(player == attack.defender)
						continue;
					if(player.hasAbility(Abilities.DAMP)){
						this.preventExplosion(player, attack.defender);
						//If the player has <= 0 HP, then the after-attacks check will catch it
						return false;
					}
				}
				//Else we actually do damage
				attack.attacker.HP -= attack.attacker.getMaxHP()/4;
			}
		}
		return false;
	}*/

	public abstract void attackLogic(IAttack attack);

	public void set(BattleEffects effect, int duration){
		this.battleEffects.put(effect, duration);
	}

	public boolean has(BattleEffects effect){
		return this.battleEffects.keySet().contains(effect);
	}

	/*//This is in this class, because only battles prevent explosions
	protected void preventExplosion(Player player, Player attacker){
		this.sendMessage("But "+player.mention()+"'s DAMP prevented"
				+attacker.mention()+"'s explosion!");
	}*/

	/*if(this.participants.size() == 1){
			Player winner = this.participants.get(0);
			this.participants.clear();
			BattleManager.onBattleWon(this, winner);
			this.timer.cancel(false);
			return true;
		}*/

	protected abstract boolean checkWin();

	//Should we stop execution?
	protected boolean checkDefaultWin(){
		if(this.participants.size() == 1){
			Player player = this.participants.remove(0);
			BattleManager.onExitBattle(player);
			this.participants.clear();
			this.attacks.clear();
			this.threatenTimeout.clear();
			this.sendMessage(player.mention()+" won the battle by default!");
			BattleManager.runningBattles.remove(this);
			return true;
		}
		if(this.participants.size() <= 0){
			this.sendMessage("Nobody won...");
			BattleManager.runningBattles.remove(this);
			return true;
		}
		return false;
	}

	public void onLeaveBattle(Player player){
		BattleManager.onExitBattle(player);
		this.participants.remove(player);
		this.attackers.remove(player);
		this.threatenTimeout.remove(player);
		this.checkDefaultWin();
	}

	protected void onSafeLeaveBattle(Player player){
		BattleManager.onExitBattle(player);
		this.participants.remove(player);
	}

	/*//This is ran after all the battle logic

	/**
	 * Used to run things like post-turn damage.
	 *//*
	protected void onPostTurn(Player player){
		//if(player.lastMoveData != MoveConstants.NOTHING) this.onAutoAttack(player, player.lastMove, player.lastTarget);
		for(Effects.VBattle effect : player.getVB()){
			switch(effect){
				case RECHARGING:{
					IAttack fakeAttack = new IAttack(player, null, null);
					fakeAttack.cancel();
					this.sendMessage(player.mention()+" must recharge!");
					this.attacks.put(player, fakeAttack);
					continue;
				}
				default:
					break;
			}
		}
	}*/

	public abstract int getMaxPlayers();

	public abstract int getMinPlayers();

	public abstract String getBattleType();
}