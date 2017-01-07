package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.moves.BattlePriority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.moves.MoveUtils;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

/**
 * For "multiturn" moves that have to be charged up first
 */
@Deprecated
public class ChargeMove extends OldMove{

	private final String chargeText;

	protected static final int CHARGING = 0;
	protected static final int ATTACKING = 1;

	public ChargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, String chargeText,
					  BattlePriority priority, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
		this.chargeText = chargeText;
	}

	public ChargeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, String chargeText,
					  OldMoveFlags... flags){
		this(type, moveCategory, PP, power, accuracy, cost, chargeText, BattlePriority.P0, flags);
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
		if(MoveUtils.checkParalysis(attacker)) return BeforeResult.STOP;
		Pokebot.sendMessage(channel, String.format(this.chargeText, attacker.mention()));
		attacker.lastMoveData = ATTACKING;
		return BeforeResult.STOP;
	}

	protected BeforeResult attacking(IChannel channel, Player attacker, Player defender){
		attacker.lastMoveData = CHARGING;
		return BeforeResult.CONTINUE;
	}
}
