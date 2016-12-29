package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Messages;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.moves.MoveUtils;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

/**
 * The main object of typical multi-hit moves, used for convenience.
 */
public class MultiHitMove extends OldMove{
	/*(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Battle_Priority priority,
	Flags... flags){*/

	private static final float[] chances = {1/3F, 1/3F, 1/6F, 1/6F}; //They sum to 100
	private static final byte offset = 1; //Because it's 2-5

	/**
	 * Used for a multi-hit move that always uses the same number of hits
	 * @param type The {@link Types Type} of the move
	 * @param moveCategory The {@link MoveCategory} of the move (Physical, Status, etc.)
	 * @param PP The max PP of the move
	 * @param singleHitPower How much each hit does
	 * @param accuracy The initial accuracy of the move
	 * @param cost The cost of this move
	 * @param flags The {@link OldMoveFlags} of the move
	 */
	public MultiHitMove(Types type, MoveCategory moveCategory, int PP, int singleHitPower, int accuracy, int cost, OldMoveFlags... flags){
		super(type, moveCategory, PP, singleHitPower, accuracy, cost, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		if(willHit(this, attacker, defender, true)){
			int timesHit = MoveUtils.getTimesHit(MultiHitMove.offset, MultiHitMove.chances);
			int damage = 0;
			for(int x = 0; x < timesHit; x++){
				damage += getDamage(attacker, this, defender);
			}
			Messages.multiHit(channel, defender, timesHit, damage);
			defender.HP = Math.max(0, defender.HP-damage);
		} else {
			Messages.miss(channel, attacker);
		}
		return BeforeResult.STOP;
	}

}
