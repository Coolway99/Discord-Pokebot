package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class AilmentDamageMove extends DamageMove{

	private final int chance;
	private final AilmentEffect ailment;

	public AilmentDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost,
							 int chance, AilmentEffect ailment, Battle_Priority priority, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
		this.chance = chance;
		this.ailment = ailment;
	}

	public AilmentDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost,
							 int chance, AilmentEffect ailment, OldMoveFlags... flags){
		this(type, moveCategory, PP, power, accuracy, cost, chance, ailment, Battle_Priority.P0, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		if(defender.inBattle() && Pokebot.diceRoll(this.chance)){
			this.ailment.run(channel, defender);
		}
	}

	@FunctionalInterface
	public interface AilmentEffect{
		void run(IChannel channel, Player defender);
	}
}
