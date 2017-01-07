package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.BattlePriority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

@Deprecated
public class HarshRechargeMove extends OldMove{

	public HarshRechargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, BattlePriority priority,
							 OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
	}

	public HarshRechargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, flags);
	}

	public HarshRechargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		if(attacker.inBattle()) attacker.set(Effects.VBattle.RECHARGING);
		return BeforeResult.CONTINUE;
	}
}
