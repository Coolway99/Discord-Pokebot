package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

/**
 * This is for moves that are super-effective against users that have used minimize
 */
public class MinimizeMove extends DamageMove{

	public MinimizeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, Battle_Priority priority,
						OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
	}

	public MinimizeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, flags);
	}

	public MinimizeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		if(defender.has(Effects.VBattle.MINIMIZE)){
			return BeforeResult.ALWAYS_HIT_ADJUSTED_DAMAGE;
		}
		return BeforeResult.CONTINUE;
	}

	@Override
	protected int getAdjustedDamage(Player attacker, Player defender){
		return 2*getDamage(attacker, this, defender);
	}
}
