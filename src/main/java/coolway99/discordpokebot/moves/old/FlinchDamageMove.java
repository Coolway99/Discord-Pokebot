package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.moves.MoveUtils;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class FlinchDamageMove extends DamageMove{

	private final float chance;

	public FlinchDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, float chance,
							Battle_Priority priority, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
		this.chance = chance;
	}

	public FlinchDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, float chance,
							OldMoveFlags... flags){
		this(type, moveCategory, PP, power, accuracy, cost, chance, Battle_Priority.P0, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		if(Pokebot.diceRoll(this.chance)) MoveUtils.flinch(channel, defender);
	}
}
