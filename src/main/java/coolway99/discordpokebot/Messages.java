package coolway99.discordpokebot;

import coolway99.discordpokebot.moves.old.OldMove;
import coolway99.discordpokebot.moves.MoveWrapper;
import sx.blah.discord.handle.obj.IChannel;

//Used for containing all the standard messages
public class Messages{

	public static void usedMove(IChannel channel, Player attacker, MoveWrapper move){
		Pokebot.sendMessage(channel, attacker.mention()+" used "+move.getDisplayName()+"!");
	}

	public static void fainted(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+" fainted!");
	}

	public static void tookDamage(IChannel channel, Player defender, int damage){
		Pokebot.sendMessage(channel, defender.mention()+" lost "+damage+"HP!");
	}

	public static void tookDamageWithRemaining(IChannel channel, Player defender, int damage){
		Pokebot.sendMessage(channel, defender.mention()+" lost "+damage+"HP! They have "+defender.HP+"HP left!");
	}

	public static void oneHitKO(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+" was OHKO'd!");
	}

	public static void dealtDamage(IChannel channel, Player attacker, int damage){
		Pokebot.sendMessage(channel, attacker.mention()+" dealt "+damage+"HP of damage!");
	}
	
	public static void multiHit(IChannel channel, Player defender, int timesHit, int damage){
		Pokebot.sendMessage(channel, defender.mention()+" got hit "+timesHit+" times for a total loss of "+damage+"HP!");
	}

	public static void substitute(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, "But "+defender.mention()+"'s substitute blocked it!");
	}

	public static void attackMessage(IChannel channel, Player attacker, OldMove move){
		Pokebot.sendMessage(channel, attacker.mention()+" used "+move.getName()+'!');
	}

	private static void recoil(IChannel channel, Player attacker, int recoil){
		Pokebot.sendMessage(channel, attacker.mention()+" took "+recoil+"HP of damage from recoil!");
	}

	public static void fail(IChannel channel, Player attacker){
		Pokebot.sendMessage(channel, "But "+attacker.mention()+"'s move failed!");
	}

	public static void miss(IChannel channel, Player attacker){
		Pokebot.sendMessage(channel, "But "+attacker.mention()+" missed!");
	}

	public static void wokeUp(IChannel channel, Player attacker){
		Pokebot.sendMessage(channel, attacker.mention()+" woke up!");
	}

	public static void isAsleep(IChannel channel, Player attacker){
		Pokebot.sendMessage(channel, attacker.mention()+" is fast asleep!");
	}

	public static void unfreeze(IChannel channel, Player attacker){
		Pokebot.sendMessage(channel, attacker.mention()+" thawed out!");
	}

	public static void frozen(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+" was frozen!");
	}

	public static void isFrozen(IChannel channel, Player attacker){
		Pokebot.sendMessage(channel, attacker.mention()+" is frozen solid!");
	}

	public static void immuneFreeze(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+"'s type is immune to freezing!");
	}

	public static void paralyzed(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+" is paralyzed! They might not be able to move!");
	}

	public static void immuneParalysis(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+"'s type is immune to paralysis!");
	}

	public static void isParalyzed(IChannel channel, Player attacker){
		Pokebot.sendMessage(channel, attacker.mention()+"is paralyzed, they can't move!");
	}

	public static void burned(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+" was burned!");
	}

	public static void immuneBurn(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+"'s type is immune to burns!");
	}

	public static void isBurned(IChannel channel, Player defender){
		Pokebot.sendMessage(channel, defender.mention()+" took damage from their burn!");
	}
}
