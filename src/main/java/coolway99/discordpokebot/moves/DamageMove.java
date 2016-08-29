package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.states.Types;

public class DamageMove extends Move{

	public DamageMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Battle_Priority priority,
						 Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
	}

	public DamageMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, flags);
	}

	public DamageMove(Types type, MoveType moveType, int PP, int power, int accuracy, Flags... flags){
		super(type, moveType, PP, power, accuracy, flags);
	}
}
