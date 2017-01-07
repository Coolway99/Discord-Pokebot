package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.BattlePriority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

@Deprecated
public class HPStealingMove extends OldMove{

	private final float percent;

	public HPStealingMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, float percentHealing,
						  BattlePriority priority, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
		this.percent = percentHealing;
	}

	public HPStealingMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, float percentHealing,
						  OldMoveFlags... flags){
		this(type, moveCategory, PP, power, accuracy, cost, percentHealing, BattlePriority.P0, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
			if(!attacker.has(Effects.Volatile.HEAL_BLOCK)){
				//TODO If the defender has liquid ooze ability, then the attacker is actually hurt by this amount
				//TODO BigRoot increases this to +80% instead of +50%
				heal(channel, attacker, (int) Math.floor(damage*(this.percent/100F)));
		}
	}
}
