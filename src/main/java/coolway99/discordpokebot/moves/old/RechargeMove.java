package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

/**
 * A move that, if hits, forces the user to recharge next turn. Does not apply out of battle
 */
public class RechargeMove extends OldMove{
	public RechargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, Battle_Priority priority,
						OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
	}

	public RechargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, flags);
	}

	public RechargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		attacker.set(Effects.VBattle.RECHARGING);
	}
}
