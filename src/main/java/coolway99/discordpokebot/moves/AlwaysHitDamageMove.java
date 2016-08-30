package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.states.Types;

public class AlwaysHitDamageMove extends DamageMove{
	public AlwaysHitDamageMove(Types type, MoveType moveType, int PP, int power, int cost, Battle_Priority
			priority, Flags... flags){
		super(type, moveType, PP, power, -1, cost, priority, flags);
	}

}
