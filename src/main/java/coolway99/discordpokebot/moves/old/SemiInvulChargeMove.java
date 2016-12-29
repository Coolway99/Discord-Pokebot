package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class SemiInvulChargeMove extends ChargeMove{
	public SemiInvulChargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, String chargeText,
							   Battle_Priority priority, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, chargeText, priority, flags);
	}

	public SemiInvulChargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, String chargeText,
							   OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, chargeText, flags);
	}

	@Override
	protected BeforeResult charging(IChannel channel, Player attacker, Player defender){
		BeforeResult ret = super.charging(channel, attacker, defender);
		if(attacker.lastMoveData != CHARGING) attacker.set(Effects.VBattle.SEMI_INVULNERABLE);
		return ret;
	}

	@Override
	protected BeforeResult attacking(IChannel channel, Player attacker, Player defender){
		attacker.remove(Effects.VBattle.SEMI_INVULNERABLE);
		return super.attacking(channel, attacker, defender);
	}
}
