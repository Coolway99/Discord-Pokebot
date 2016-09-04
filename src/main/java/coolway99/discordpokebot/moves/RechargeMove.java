package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

/**
 * A move that, if hits, forces the user to recharge next turn. Does not apply out of battle
 */
public class RechargeMove extends Move{
	public RechargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Battle_Priority priority,
						Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
	}

	public RechargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, flags);
	}

	public RechargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, Flags... flags){
		super(type, moveType, PP, power, accuracy, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		attacker.set(Effects.VBattle.RECHARGING);
	}
}
