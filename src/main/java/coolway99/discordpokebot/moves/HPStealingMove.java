package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class HPStealingMove extends Move{

	private final float percent;

	public HPStealingMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, float percentHealing,
						  Battle_Priority priority, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
		this.percent = percentHealing;
	}

	public HPStealingMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, float percentHealing,
						Flags... flags){
		this(type, moveType, PP, power, accuracy, cost, percentHealing, Battle_Priority.P0, flags);
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
