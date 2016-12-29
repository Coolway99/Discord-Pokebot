package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Types;

public class DamageMove extends OldMove{

	public DamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, Battle_Priority priority,
					  OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
	}

	public DamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, flags);
	}

	public DamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, flags);
	}
}
