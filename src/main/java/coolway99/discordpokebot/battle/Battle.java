package coolway99.discordpokebot.battle;

import coolway99.discordpokebot.MoveConstants;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Moves;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Battle{

	public final IChannel channel;
	private final List<Player> participants;
	/**
	 * Used to make kick those who are inactive from the battle
	 */
	private final ArrayList<Player> threatenTimeout;
	/**
	 * The map of players to attack. It's a tree map, so when it's iterated through the top speed goes first
	 */
	//private final TreeMap<Player, IAttack> attacks;
	private final BattleMap attacks;
	/**
	 * Set the timeout for a turn on the battle
	 */
	private final int turnTime;
	private final BattleTurnTimeout timer;
	private final HashMap<BattleEffects, Integer> battleEffects;

	public Battle(IChannel channel, int turnTime, List<Player> participants){
		this.channel = channel;
		this.participants = participants;
		this.turnTime = turnTime;
		this.attacks = new BattleMap();
		this.threatenTimeout = new ArrayList<>(2);
		this.battleEffects = new HashMap<>();
		for(Player player : participants){
			player.battle = this;
		}
		StringBuilder builder = new StringBuilder("A battle has started between ");
		Iterator<Player> i = participants.iterator();
		while(i.hasNext()){
			Player player = i.next();
			builder.append(player.mention());
			if(i.hasNext()){
				builder.append(", "); //TODO make this neater
			} else {
				builder.append('!');
			}
		}
		builder.append("\nYou have ");
		builder.append(this.turnTime*2);
		builder.append(" seconds to make your first move!");
		this.sendMessage(builder.toString());
		this.timer = new BattleTurnTimeout(this);
		Pokebot.timer.schedule(this.timer, this.turnTime*2, TimeUnit.SECONDS);
	}

	private void sendMessage(String message){
		Pokebot.sendMessage(this.channel, message);
	}

	public List<Player> getParticipants(){
		return this.participants;
	}

	public void onAttack(IChannel channel, Player attacker, Moves move, Player defender){
		synchronized(this.attacks){
			if(this.attacks.containsKey(attacker)){
				this.sendMessage(attacker.mention()+" you've already sent your attack!");
				return;
			}
			this.attacks.put(attacker, new IAttack(attacker, move, defender));
			if(!channel.getID().equals(this.channel.getID())){
				this.sendMessage(attacker.mention()+" sent in their attack from another channel!");
			} else {
				this.sendMessage(attacker.mention()+" submitted their attack");
			}
			attacker.lastMove = move;
			attacker.lastTarget = move.has(Moves.Flags.UNTARGETABLE) ? null : defender;
			if(!move.has(Moves.Flags.UNTARGETABLE))
				defender.lastAttacker = attacker; //TODO free-for-all battles might make this weird...
			if(this.attacks.size() == this.participants.size()){
				this.timer.cancel();
				this.onTurn();
			}
		}
	}

	//Called for moves that auto-set themselves
	private void onAutoAttack(Player attacker, Moves move, Player defender){
		synchronized(this.attacks){
			this.attacks.put(attacker, new IAttack(attacker, move, defender));
			this.sendMessage(attacker.mention()+" has a multiturn attack!");
		}
	}

	//This is called every time the BattleTurnTimer times out, or if onAttack is completely filled
	public void onTurn(){
		this.timer.cancel();
		synchronized(this.attacks){
			this.sendMessage("Processing attacks");
			//We make a "note" of those who hadAttacks, to prevent flinch from glitching things up
			//TODO Perhaps make it a single list for the IAttacks, and have flinching remove it on-attack-time
			for(Player player : this.attacks.keySet()){
				IAttack attack = this.attacks.get(player);
				if(attack.isCanceled()) continue;
				attack.cancel();
				this.threatenTimeout.remove(attack.attacker);
				//We know it's sorted by speed, so only the fastest go first
				this.attackLogic(attack);
				//Both a recoil check and a failsafe
				if(attack.attacker.HP <= 0){
					this.sendMessage(attack.attacker.mention()+" fainted!");
					if(this.playerFainted(attack.attacker)){
						return;
					}
				}
			}
			//TODO after-turn things
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
				switch(player.getNV()){
					case BURN:{
						//TODO Check for ability heatproof
						player.HP = Math.max(0, player.HP-(player.getMaxHP()/8));
						this.sendMessage(player.mention()+" took damage for it's burn!");
						break;
					}
					case POISON:{
						if(player.hasAbility(Abilities.POISON_HEAL)){
							Moves.heal(this.channel, player, player.getMaxHP()/8);
							break;
						}
						player.HP = Math.max(0, player.HP-(player.getMaxHP()/8));
						this.sendMessage(player.mention()+" took damage from poison!");
						break;
					}
					case TOXIC:{
						if(player.hasAbility(Abilities.POISON_HEAL)){
							Moves.heal(this.channel, player, player.getMaxHP()/8);
							++player.counter;
							break;
						}
						player.HP = Math.max(0,
								player.HP-(player.getMaxHP()*++player.counter/16));
						this.sendMessage(player.mention()+" took damage from poison!");
						break;
					}
					default:
						break;
				}
				if(player.HP <= 0){
					Moves.faintMessage(this.channel, player);
					if(this.playerFainted(player)){return;}
				}
				if(player.has(Effects.Volatile.FLINCH)){
					this.sendMessage(player.mention()+" stopped cringing!");
					player.remove(Effects.Volatile.FLINCH);
				}
			}
			//We check for those who didn't do anything:
			for(Player player : this.threatenTimeout){
				this.onSafeLeaveBattle(player);
				this.sendMessage(player.mention()+" got eliminated for inactivity!");
			}
			if(this.checkDefaultWin())
				return;
			this.threatenTimeout.clear();
			this.threatenTimeout.addAll(this.participants);
			this.threatenTimeout.removeAll(this.attacks.keySet());
			for(Player player : this.threatenTimeout){
				this.sendMessage("If "+player.mention()
						+" doesn't attack the next turn, they're out!");
				player.lastTarget = null;
				player.lastMove = Moves.NULL;
				player.lastAttacker = null;
			}
			this.attacks.clear();
			this.sendMessage("Begin next turn, you have "+this.turnTime+" seconds to make your attack");
			Pokebot.timer.schedule(this.timer, this.turnTime, TimeUnit.SECONDS);
			this.participants.forEach(this::onPostTurn);
		}
	}

	private void attackLogic(final IAttack attack){
		//Check for flinch status
		if(attack.attacker.has(Effects.Volatile.FLINCH)){
			this.sendMessage("But "+attack.attacker.mention()+" is flinching!");
			attack.attacker.remove(Effects.Volatile.FLINCH);
			return;
		}
		if(!this.participants.contains(attack.defender)){
			this.sendMessage(attack.attacker.mention()
					+" went to attack, but there was no target!");
			return;
		}
		if(Moves.attack(this.channel, attack)){
			if(attack.defender.lastMove == Moves.DESTINY_BOND){
				this.sendMessage(attack.attacker.mention()
						+" was taken down with "+attack.defender.mention());
				attack.attacker.HP = 0;
			}
			if(attack.attacker.lastMove == Moves.AFTER_YOU){
				IAttack defenderAttack = this.attacks.get(attack.defender);
				if(defenderAttack != null && !defenderAttack.isCanceled()){
					this.sendMessage(attack.attacker.mention()+" made "+attack.defender.mention()+" go next!");
					//this.attackLogic(defenderAttack.get());
					//defenderAttack.get().cancel();
				} else {
					this.sendMessage("But there wasn't anything to do!"); //TODO better message
				}
			}
			if(this.playerFainted(attack.defender)){
				return;
			}
			if(attack.defender.hasAbility(Abilities.AFTERMATH)){
				for(Player player : this.participants){
					if(player == attack.defender)
						continue;
					if(player.hasAbility(Abilities.DAMP)){
						this.preventExplosion(player, attack.defender);
						//If the player has <= 0 HP, then the after-attacks check will catch it
						return;
					}
				}
				//Else we actually do damage
				attack.attacker.HP -= attack.attacker.getMaxHP()/4;
			}
		}
	}

	public void set(BattleEffects effect, int duration){
		this.battleEffects.put(effect, duration);
	}

	public boolean has(BattleEffects effect){
		return this.battleEffects.keySet().contains(effect);
	}

	//This is in this class, because only battles prevent explosions
	private void preventExplosion(Player player, Player attacker){
		this.sendMessage("But "+player.mention()+"'s DAMP prevented"
				+attacker.mention()+"'s explosion!");
	}

	/**
	 * Returns true if the battle has ended
	 */
	private boolean playerFainted(Player player){
		this.onSafeLeaveBattle(player);
		if(this.participants.size() == 1){
			Player winner = this.participants.get(0);
			this.participants.clear();
			BattleManager.onBattleWon(this, winner);
			this.timer.cancel();
			return true;
		}
		return false;
	}

	//Should we stop execution?
	private boolean checkDefaultWin(){
		if(this.participants.size() == 1){
			Player player = this.participants.remove(0);
			this.onLeaveBattle(player);
			this.sendMessage(player.mention()+" won the battle by default!");
			BattleManager.battles.remove(this);
			return true;
		}
		if(this.participants.size() <= 0){
			this.sendMessage("Nobody won...");
			BattleManager.battles.remove(this);
			return true;
		}
		return false;
	}

	public void onLeaveBattle(Player player){
		BattleManager.onExitBattle(player);
		this.participants.remove(player);
		this.attacks.remove(player);
		this.threatenTimeout.remove(player);
	}

	private void onSafeLeaveBattle(Player player){
		BattleManager.onExitBattle(player);
		this.participants.remove(player);
	}

	//This is ran after all the battle logic

	/**
	 * Used to run things like post-turn damage.
	 * <p>
	 * Attacks are already reset by this point,
	 * so any multi-turn attacks can auto-queue themselves without consequence
	 */
	private void onPostTurn(Player player){
		switch(player.lastMove){
			case FLY:{
				switch(player.lastMoveData){
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

	public enum BattleEffects{
		GRAVITY,
		TRICK_ROOM
	}
}