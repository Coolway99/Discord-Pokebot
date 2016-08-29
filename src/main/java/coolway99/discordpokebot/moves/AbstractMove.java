package coolway99.discordpokebot.moves;

import com.sun.istack.internal.Nullable;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class AbstractMove extends Move{

	private final BeforeFunction before;
	private final AfterFunction after;

	protected AbstractMove(@Nullable BeforeFunction before, @Nullable AfterFunction after, Types type, MoveType moveType,
			int PP, int power, int accuracy, int cost, Battle_Priority priority, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
		this.before = before == null ? (a, b, c) -> BeforeResult.CONTINUE : before;
		this.after = after == null ? (a, b, c, d) -> {} : after;
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		return this.before.runBefore(channel, attacker, defender);
	}


	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		this.after.runAfter(channel, attacker, defender, damage);
	}

	@FunctionalInterface
	private interface BeforeFunction{
		BeforeResult runBefore(IChannel channel, Player attacker, Player defender);
	}

	@FunctionalInterface
	private interface AfterFunction{
		void runAfter(IChannel channel, Player attacker, Player defender, int damage);
	}
}
