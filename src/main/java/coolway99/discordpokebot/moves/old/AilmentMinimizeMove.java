package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.moves.BattlePriority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

@Deprecated
public class AilmentMinimizeMove extends MinimizeMove{
	private final int chance;
	private final AilmentEffect ailment;

	public AilmentMinimizeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost,
							   int chance, AilmentEffect ailment, BattlePriority priority, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
		this.chance = chance;
		this.ailment = ailment;
	}

	public AilmentMinimizeMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost,
							   int chance, AilmentEffect ailment, OldMoveFlags... flags){
		this(type, moveCategory, PP, power, accuracy, cost, chance, ailment, BattlePriority.P0, flags);
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
