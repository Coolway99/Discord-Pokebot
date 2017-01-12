package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Messages;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class MoveUtils{


	public static DamageCalculator getDamage(IChannel channel, Player attacker, MoveWrapper move, Player defender){
		DamageCalculator damageCalc = new DamageCalculator(attacker, move, defender);
		attacker.getModifiedAbility().onDamageModifier(channel, damageCalc);
		defender.getModifiedItem().onDamageModifier(channel, damageCalc);
		//TODO items
		return damageCalc;
	}

	//A helper method to combine the two
	public static int dealDamage(IChannel channel, Player attacker, MoveWrapper move, Player defender){
		return defender.damage(getDamage(channel, attacker, move, defender).getDamage());
	}

	public static int getTimesHit(int offset, float... chances){
		float ran = Pokebot.ran.nextFloat()*100;
		float i = 0;
		int times = offset;
		for(float chance : chances){
			times++;
			i += chance;
			if(ran <= i) return times;
		}
		return chances.length+offset;
	}

	public static void standardMultiHit(IChannel channel, Player attacker, MoveWrapper move, Player defender){
		multiHitMove(channel, attacker, move, defender, 1, 1/3F, 1/3F, 1/6F, 1/6F);
	}

	public static void multiHitMove(IChannel channel, Player attacker, MoveWrapper move, Player defender,
									int offset, float... times){
		int hits = getTimesHit(offset, times);
		int damage = 0;
		for(int x = 0; x < hits; x++){
			damage += getDamage(channel, attacker, move, defender).getDamage();
		}
		Messages.multiHit(channel, defender, hits, damage);
		if(defender.has(Effects.NonVolatile.FAINTED)){
			Messages.fainted(channel, defender);
		}
	}

	public static void chargeMove(IChannel channel, Player attacker, MoveWrapper move, Player defender){
		if(attacker.battle == null) attacker.lastMoveData = 1;
		switch(attacker.lastMoveData){
			case 1:{
				attacker.lastMoveData = 0;
				Messages.usedMove(channel, attacker, move);
				dealDamage(channel, attacker, move, defender);
				break;
			}
			case 0:
			default:{
				attacker.lastMoveData = 1;
				Pokebot.sendMessage(channel, String.format(move.getMessage(), attacker.mention()));
				//We assume we're in a battle due to the null check at the start of the function
				attacker.battle.propagateAttack(attacker);
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
		if(defender.has(Effects.NonVolatile.POISON) || defender.has(Effects.NonVolatile.TOXIC)) return;
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		if(defender.hasType(Types.POISON) || defender.hasType(Types.STEEL)){
			Messages.immunePoison(channel, defender);
		} else {
			defender.set(Effects.NonVolatile.POISON);
			Messages.poisoned(channel, defender);
		}
	}

	public static void toxic(IChannel channel, Player defender){
		if(defender.has(Effects.NonVolatile.TOXIC) || defender.has(Effects.NonVolatile.POISON)) return;
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		if(defender.hasType(Types.POISON) || defender.hasType(Types.STEEL)){
			Messages.immunePoison(channel, defender);
		} else {
			defender.set(Effects.NonVolatile.TOXIC);
			defender.counter = 0;
			Messages.badlyPoisoned(channel, defender);
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
		Messages.sleep(channel, defender);

	}

	public static void flinch(IChannel channel, Player defender){
		if(defender.has(Effects.VBattle.SUBSTITUTE)){
			Messages.substitute(channel, defender);
			return;
		}
		defender.set(Effects.Volatile.FLINCH); //TODO Check for abilities
		Messages.flinched(channel, defender);
		//We assume this is only called within-battle
	}

	public static void confuse(IChannel channel, Player defender){
		//TODO More research into confusion
		defender.set(Effects.Volatile.CONFUSION);
		Messages.confused(channel, defender);
	}

	public static void doEffectDamage(IChannel channel, Player player){
		player.getModifiedItem().onBeforeEffect(channel, player);
		switch(player.getNV()){
			case BURN:{
				//TODO Check for ability heatproof
				int damage = player.getMaxHP() / 8;
				player.damage(damage);
				Messages.isBurned(channel, player, damage);
				break;
			}
			case POISON:{
				/*TODO if(player.hasAbility(Abilities.POISON_HEAL)){
					OldMove.heal(this.channel, player, player.getMaxHP()/8);
					break;
				}*/
				int damage = player.getMaxHP() / 8;
				player.damage(damage);
				Messages.isPoisoned(channel, player, damage);
				break;
			}
			case TOXIC:{
				/* TODO if(player.hasAbility(Abilities.POISON_HEAL)){
					OldMove.heal(this.channel, player, player.getMaxHP()/8);
					++player.counter;
					break;
				}*/
				int damage = (int) (player.getMaxHP()*(++player.counter/16D));
				player.damage(damage);
				Messages.isPoisoned(channel, player, damage);
				break;
			}
			default:
				break;
		}
		player.getModifiedItem().onAfterEffect(channel, player);
	}
}
