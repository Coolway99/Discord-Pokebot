package coolway99.discordpokebot.moves;

import com.sun.istack.internal.Nullable;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class UniqueMove extends Move{

	private final BeforeFunction before;
	private final AfterFunction after;

	public UniqueMove(@Nullable BeforeFunction before, @Nullable AfterFunction after, Types type, MoveType moveType,
					  int PP, int power, int accuracy, int cost, Battle_Priority priority, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
		this.before = before == null ? (a, b, c, d) -> BeforeResult.CONTINUE : before;
		this.after = after == null ? (a, b, c, d, e) -> {} : after;
	}

	public UniqueMove(BeforeFunction before, Types type, MoveType moveType, int PP, int power, int accuracy,
					  int cost, Battle_Priority priority, Flags... flags){
		this(before, null, type, moveType, PP, power, accuracy, cost, priority, flags);
	}

	public UniqueMove(AfterFunction after, Types type, MoveType moveType, int PP, int power, int accuracy, int cost,
					  Battle_Priority priority, Flags... flags){
		this(null, after, type, moveType, PP, power, accuracy, cost, priority, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		return this.before.runBefore(channel, attacker, this, defender);
	}


	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		this.after.runAfter(channel, attacker, this, defender, damage);
	}

	@FunctionalInterface
	protected interface BeforeFunction{
		BeforeResult runBefore(IChannel channel, Player attacker, Move move, Player defender);
	}

	@FunctionalInterface
	protected interface AfterFunction{
		void runAfter(IChannel channel, Player attacker, Move move, Player defender, int damage);
	}
}
