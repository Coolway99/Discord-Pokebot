package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Messages;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.Context;
import coolway99.discordpokebot.states.Effects;

public class AttackLogic{

	public static void attack(Context context, Player attacker, MoveWrapper move, Player defender){
		//Check if we are frozen and fail to break out of it
		if(!checkFrozen(context, attacker, move)) return;
		//Check if we are asleep and fail to wake up
		if(!checkSleep(context, attacker, move)) return;
		//Attacker onBeforeAttack for any applicable items
		//Defender onBeforeDefend for any applicable items
		if(move.displayUsedMoveText()) Messages.usedMove(context.channel, attacker, move);
		if(!move.onTry(context, attacker, defender)){
			Messages.fail(context.channel, attacker);
			return;
		}
		//Accuracy check
		if(!checkAccuracy(context, attacker, move, defender)){
			Messages.miss(context.channel, attacker);
			return;
		}
		//Take note of the attackerHP before the logic
		int attackerHP = attacker.HP;
		//Take note of the defenderHP before the logic
		int defenderHP = defender.HP;
		//Run onBeforeAttack
		if(!move.onBeforeAttack(context, attacker, defender)) return;
		//Actually do the attack
		move.onAttack(context, attacker, defender);
		//Calculate recoil
		int recoil = attackerHP - attacker.HP;
		//Calculate damage
		int damage = defenderHP - defender.HP;
		attacker.getModifiedAbility().onAttack(context, attacker, move, defender);
		//Run any secondary effects for the move
		move.onSecondary(context, attacker, defender, damage);
		//Attacker onAfterAttack for any applicable items
		//Defender onAfterDefend for any applicable items
		if(attacker.has(Effects.NonVolatile.FAINTED)){
			attacker.getModifiedAbility().onFaint(context, attacker);
		}
	}

	public static boolean checkAccuracy(Context context, Player attacker, MoveWrapper move, Player defender){
		double accuracy = move.getAccuracy(context, attacker, defender) / 100D;
		accuracy *= attacker.getAccuracy();
		accuracy /= defender.getEvasion();
		return Pokebot.diceRoll(accuracy * 100D);
	}
	
	public static boolean checkFrozen(Context context, Player attacker, MoveWrapper move){
		if(attacker.has(Effects.NonVolatile.FROZEN)){
			if(move.getFlags().contains(MoveFlags.CAN_DEFROST) || Pokebot.diceRoll(20)){
				attacker.cureNV();
				Messages.unfreeze(context.channel, attacker);
			} else {
				Messages.isFrozen(context.channel, attacker);
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkSleep(Context context, Player attacker, MoveWrapper move){
		if(attacker.has(Effects.NonVolatile.SLEEP)){
			//TODO if the move is snore or sleep talk, then it will work
			if(attacker.counter-- <= 0){
				Messages.wokeUp(context.channel, attacker);
			} else {
				Messages.isAsleep(context.channel, attacker);
				return false;
			}
		}
		return true;
	}
}