package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class AilmentMinimizeMove extends MinimizeMove{
	private final int chance;
	private final AilmentEffect ailment;

	public AilmentMinimizeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost,
							 int chance, AilmentEffect ailment, Battle_Priority priority, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
		this.chance = chance;
		this.ailment = ailment;
	}

	public AilmentMinimizeMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost,
							 int chance, AilmentEffect ailment, Flags... flags){
		this(type, moveType, PP, power, accuracy, cost, chance, ailment, Battle_Priority.P0, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		if(defender.inBattle() && diceRoll(this.chance)){
			this.ailment.run(channel, defender);
		}
	}

	@FunctionalInterface
	public interface AilmentEffect{
		void run(IChannel channel, Player defender);
	}
}
