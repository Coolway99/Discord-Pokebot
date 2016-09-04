package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

/**
 * For "multiturn" moves that have to be charged up first
 */
public class ChargeMove extends Move{

	private final String chargeText;

	protected static final int CHARGING = 0;
	protected static final int ATTACKING = 1;

	public ChargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, String chargeText,
					  Battle_Priority priority, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
		this.chargeText = chargeText;
	}

	public ChargeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, String chargeText,
					  Flags... flags){
		this(type, moveType, PP, power, accuracy, cost, chargeText, Battle_Priority.P0, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		switch(attacker.lastMoveData){
			//noinspection DefaultNotLastCaseInSwitch
			default:
			case ChargeMove.CHARGING:{
				return this.charging(channel, attacker, defender);
			}
			case ChargeMove.ATTACKING:{
				return this.attacking(channel, attacker, defender);
			}
		}
	}

	protected BeforeResult charging(IChannel channel, Player attacker, Player defender){
		if(!attacker.inBattle()) return BeforeResult.CONTINUE;
		if(checkParalysis(attacker)) return BeforeResult.STOP;
		Pokebot.sendMessage(channel, String.format(this.chargeText, attacker.mention()));
		attacker.lastMoveData = ATTACKING;
		return BeforeResult.STOP;
	}

	protected BeforeResult attacking(IChannel channel, Player attacker, Player defender){
		attacker.lastMoveData = CHARGING;
		return BeforeResult.CONTINUE;
	}
}
