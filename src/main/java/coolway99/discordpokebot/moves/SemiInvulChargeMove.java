package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class SemiInvulChargeMove extends ChargeMove{
	public SemiInvulChargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, String chargeText,
							   Battle_Priority priority, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, chargeText, priority, flags);
	}

	public SemiInvulChargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, String chargeText,
							   Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, chargeText, flags);
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
