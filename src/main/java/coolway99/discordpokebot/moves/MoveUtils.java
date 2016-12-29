package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Context;
import coolway99.discordpokebot.Messages;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class MoveUtils{

	public static double getStab(Player attacker, MoveWrapper move){
		if(attacker.hasType(move.getType())){
			if(attacker.hasAbility(Abilities.ADAPTABILITY)) return 2;
			return 1.5;
		}
		return 1;
	}

	public static int getDamage(Player attacker, MoveWrapper move, Player defender){
		return getDamage(attacker, move, defender, move.getPower());
	}

	//TODO Critical hits are purposefully removed, however look into putting them back or not depending on feedback
	public static int getDamage(Player attacker, MoveWrapper move, Player defender, int power){
		double modifier = getStab(attacker, move)//STAB
						*Types.getTypeMultiplier(attacker, move, defender) //Effectiveness
						//*getOtherModifiers(attacker, move, defender) TODO had to do with moves like gust
						*((Pokebot.ran.nextInt(100-85+1)+85+1)/100D) //Random chance, it would be 85-99 if there wasn't the +1
				;
		switch(attacker.getModifiedAbility()){
			case ANALYTIC:{
				modifier *= 1.3; //30% increase
				break;
			}
			case BLAZE:{
				if(move.getType() == Types.FIRE && attacker.HP < Math.floorDiv(attacker.getMaxHP(), 3)){
					modifier *= 1.5;
				}
				break;
			}
			default:
				break;
		}
		double a = ((2*attacker.level)+10D)/250D;
		double b;
		if(move.getCategory() == MoveCategory.SPECIAL){
			b = attacker.getSpecialAttackStat();
			b /= defender.getSpecialDefenseStat();
		} else {
			b = attacker.getAttackStat();
			b /= defender.getDefenseStat();
		}
		//power = (int) getPowerChange(attacker, move, defender, power); TODO has to do with abilities
		double ret = ((a*b*power)+2)*modifier;
		return (int) ret; //implicit math.floor
	}

	//A helper method to combine the two
	public static int dealDamage(IChannel channel, Player attacker, MoveWrapper move, Player defender){
		return defender.damage(channel, getDamage(attacker, move, defender));
	}

	//A helper method to combine the two
	public static int dealDamage(Player attacker, MoveWrapper move, Player defender){
		return defender.damage(getDamage(attacker, move, defender));
	}

	public static int getTimesHit(int offset, float... chances){
		float ran = Pokebot.ran.nextFloat()*100;
		float i = 0;
		int times = offset;
		for(int x = 0; x < chances.length; x++){
			times++;
			i += chances[x];
			if(ran <= i) return times;
		}
		return chances.length+offset;
	}

	public static void chargeMove(Context context, Player attacker, MoveWrapper move, Player defender){
		if(context.battle == null) attacker.lastMoveData = 1;
		switch(attacker.lastMoveData){
			case 1:{
				attacker.lastMoveData = 0;
				Messages.usedMove(context.channel, attacker, move);
				dealDamage(context.channel, attacker, move, defender);
				break;
			}
			case 0:
			default:{
				attacker.lastMoveData = 1;
				Pokebot.sendMessage(context.channel, String.format(move.getMessage(), attacker.mention()));
				//TODO this only occurs in battles, and therefore most re-queue the move
			}
		}
	}

	public static boolean checkParalysis(Player attacker){
		if(!attacker.has(Effects.NonVolatile.PARALYSIS)) return false;
		if(Pokebot.diceRoll(25)){
			//We assume paralysis only takes place in a battle
			Messages.isParalyzed(attacker.battle.channel, attacker);
			return false;
		}
		return true;
	}

	public static void burn(IChannel channel, Player defender){
		if(defender.has(Effects.NonVolatile.BURN)) return;
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		if(defender.hasType(Types.FIRE)){
			Messages.immuneBurn(channel, defender);
		} else {
			defender.set(Effects.NonVolatile.BURN);
			Messages.burned(channel, defender);
		}
	}

	public static void freeze(IChannel channel, Player defender){
		if(defender.has(Effects.NonVolatile.FROZEN)) return;
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		if(defender.hasType(Types.ICE)){
			Messages.immuneFreeze(channel, defender);
		} else {
			defender.set(Effects.NonVolatile.FROZEN);
			Messages.frozen(channel, defender);
		}
	}

	public static void paralyze(IChannel channel, Player defender){
		if(defender.has(Effects.NonVolatile.PARALYSIS)) return;
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		if(defender.hasType(Types.ELECTRIC)){
			Messages.immuneParalysis(channel, defender);
		} else {
			defender.set(Effects.NonVolatile.PARALYSIS);
			Messages.paralyzed(channel, defender);
		}
	}

	public static void poison(IChannel channel, Player defender){
		if(defender.has(Effects.NonVolatile.POISON)) return;
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		if(defender.hasType(Types.POISON) || defender.hasType(Types.STEEL)){
			Pokebot.sendMessage(channel, defender.mention()+"'s type is immune to "+"poison"+"!");
		} else {
			defender.set(Effects.NonVolatile.POISON);
			Pokebot.sendMessage(channel, defender.mention()+" was "+"poisoned"+"!");
		}
	}

	public static void toxic(IChannel channel, Player defender){
		if(defender.has(Effects.NonVolatile.TOXIC)) return;
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		if(defender.hasType(Types.POISON) || defender.hasType(Types.STEEL)){
			Pokebot.sendMessage(channel, defender.mention()+"'s type is immune to "+"poison"+"!");
		} else {
			defender.set(Effects.NonVolatile.TOXIC);
			defender.counter = 0;
			Pokebot.sendMessage(channel, defender.mention()+" was "+"badly poisoned"+"!");
		}
	}

	public static void sleep(IChannel channel, Player defender){
		if(defender.has(Effects.NonVolatile.SLEEP)) return;
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		defender.set(Effects.NonVolatile.SLEEP);
		defender.counter = Pokebot.ran.nextInt(3)+1;

	}

	public static void flinch(IChannel channel, Player defender){
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		defender.set(Effects.Volatile.FLINCH); //TODO Check for abilities
		Pokebot.sendMessage(channel, defender.mention()+" flinched!");
		//We assume this is only called within-battle
	}
}
