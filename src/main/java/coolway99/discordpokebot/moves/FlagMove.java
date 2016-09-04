package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Types;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.obj.IChannel;

/**
 * A move that does nothing except set a flag
 */
public class FlagMove extends Move{

	private final String message;

	public FlagMove(Types type, int PP, int accuracy, int cost, @Nullable String message, Battle_Priority priority,
					Flags... flags){
		super(type, MoveType.STATUS, PP, -1, accuracy, cost, priority, flags);
		this.message = message;
	}

	public FlagMove(Types type, int PP, int accuracy, int cost, @Nullable String message, Flags... flags){
		this(type, PP, accuracy, cost, message, Battle_Priority.P0, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		if(this.message != null) Pokebot.sendMessage(channel, String.format(this.message, attacker.mention()));
		return BeforeResult.STOP;
	}
}
