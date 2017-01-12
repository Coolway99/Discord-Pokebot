package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Messages;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Effects;
import sx.blah.discord.handle.obj.IChannel;

public class AttackLogic{

	public static void attack(IChannel channel, Player attacker, MoveWrapper move, Player defender){
		//Check if we are frozen and fail to break out of it
		if(!checkFrozen(channel, attacker, move)) return;
		//Check if we are asleep and fail to wake up
		if(!checkSleep(channel, attacker, move)) return;
		//Attacker onBeforeAttack for any applicable items
		//Defender onBeforeDefend for any applicable items
		if(move.displayUsedMoveText()) Messages.usedMove(channel, attacker, move);
		if(!move.onTry(channel, attacker, defender)){
			Messages.fail(channel, attacker);
			return;
		}
		//Accuracy check
		if(!checkAccuracy(channel, attacker, move, defender)){
			Messages.miss(channel, attacker);
			return;
		}
		//Take note of the attackerHP before the logic
		int attackerHP = attacker.HP;
		//Take note of the defenderHP before the logic
		int defenderHP = defender.HP;
		//Run onBeforeAttack
		if(!move.onBeforeAttack(channel, attacker, defender)) return;
		//Actually do the attack
		move.onAttack(channel, attacker, defender);
		//Calculate recoil
		int recoil = attackerHP - attacker.HP;
		//Calculate damage
		int damage = defenderHP - defender.HP;
		attacker.getModifiedAbility().onAttack(channel, attacker, move, defender);
		//Check the item for anything after a direct attack
		//Attacking-side
		//defender.getModifiedItem().onAttack(channel, attacker, move, defender);
		//Defending-side
		defender.getModifiedItem().onHit(channel, attacker, move, defender);
		//Run any secondary effects for the move
		move.onSecondary(channel, attacker, defender, damage);
		//Attacker onAfterAttack for any applicable items
		//Defender onAfterDefend for any applicable items
		if(attacker.has(Effects.NonVolatile.FAINTED)){
			attacker.getModifiedAbility().onFaint(channel, attacker);
		}
	}

	public static boolean checkAccuracy(IChannel channel, Player attacker, MoveWrapper move, Player defender){
		double accuracy = move.getAccuracy(channel, attacker, defender) / 100D;
		accuracy *= attacker.getAccuracy();
		accuracy /= defender.getEvasion();
		return Pokebot.diceRoll(accuracy * 100D);
	}
	
	public static boolean checkFrozen(IChannel channel, Player attacker, MoveWrapper move){
		if(attacker.has(Effects.NonVolatile.FROZEN)){
			if(move.getFlags().contains(MoveFlags.CAN_DEFROST) || Pokebot.diceRoll(20)){
				attacker.cureNV();
				Messages.unfreeze(channel, attacker);
			} else {
				Messages.isFrozen(channel, attacker);
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkSleep(IChannel channel, Player attacker, MoveWrapper move){
		if(attacker.has(Effects.NonVolatile.SLEEP)){
			//TODO if the move is snore or sleep talk, then it will work
			if(attacker.counter-- <= 0){
				Messages.wokeUp(channel, attacker);
			} else {
				Messages.isAsleep(channel, attacker);
				return false;
			}
		}
		return true;
	}
}