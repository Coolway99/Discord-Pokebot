package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class HarshRechargeMove extends Move{

	public HarshRechargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Battle_Priority priority,
							 Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
	}

	public HarshRechargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, flags);
	}

	public HarshRechargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, Flags... flags){
		super(type, moveType, PP, power, accuracy, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		if(attacker.inBattle()) attacker.set(Effects.VBattle.RECHARGING);
		return BeforeResult.CONTINUE;
	}
}
