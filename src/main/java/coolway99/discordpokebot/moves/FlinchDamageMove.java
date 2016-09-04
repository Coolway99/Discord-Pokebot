package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class FlinchDamageMove extends DamageMove{

	private final float chance;

	public FlinchDamageMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, float chance,
							Battle_Priority priority, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
		this.chance = chance;
	}

	public FlinchDamageMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, float chance,
							Flags... flags){
		this(type, moveType, PP, power, accuracy, cost, chance, Battle_Priority.P0, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		if(diceRoll(this.chance)) flinch(channel, defender);
	}
}
