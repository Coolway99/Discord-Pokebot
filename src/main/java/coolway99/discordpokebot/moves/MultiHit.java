package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Arrays;

/**
 * The main object of typical multi-hit moves, used for convenience.
 */
public class MultiHit extends Move{
	/*(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Battle_Priority priority,
	Flags... flags){*/

	private static final float[] chances = {33.3F, 33.3F, 16.7F, 16.7F}; //They sum to 100
	private static final byte offset = 1; //Because it's 2-5

	/**
	 * Used for a multi-hit move that always uses the same number of hits
	 * @param type The {@link Types Type} of the move
	 * @param moveType The {@link coolway99.discordpokebot.moves.Move.MoveType} of the move (Physical, Status, etc.)
	 * @param PP The max PP of the move
	 * @param singleHitPower How much each hit does
	 * @param accuracy The initial accuracy of the move
	 * @param cost The cost of this move
	 * @param flags The {@link coolway99.discordpokebot.moves.Move.Flags} of the move
	 */
	public MultiHit(Types type, MoveType moveType, int PP, int singleHitPower, int accuracy, int cost, Flags... flags){
		super(type, moveType, PP, singleHitPower, accuracy, cost, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		if(willHit(this, attacker, defender, true)){
			int timesHit = getTimesHit();
			int damage = 0;
			for(int x = 0; x < timesHit; x++){
				damage += getDamage(attacker, this, defender);
			}
			Pokebot.sendMessage(channel, attacker.mention()+" attacked "+defender.mention()
					+" "+timesHit+" times for a total of "+damage+"HP of damage!");
			defender.HP = Math.max(0, defender.HP-damage);
		} else {
			missMessage(channel, attacker);
		}
		return BeforeResult.STOP;
	}

	private static int getTimesHit(){
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
}
