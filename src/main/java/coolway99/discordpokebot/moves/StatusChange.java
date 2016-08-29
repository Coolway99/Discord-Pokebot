package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class StatusChange extends Move{

	private static final int DEFAULT_POINTS_PER_LEVEL = 25;

	final Stats stat;
	final byte change;

	public StatusChange(Types type, int PP, int accuracy, int cost, Stats stat, int change, Flags... flags){
		super(type, MoveType.STATUS, PP, -1, accuracy, cost, flags);
		this.stat = stat;
		this.change = (byte) change;
	}

	public StatusChange(Types type, int PP, int accuracy, Stats stat, int change, Flags... flags){
		this(type, PP, accuracy, 25*change, stat, change, flags);
	}

	public StatusChange(Types type, int PP, Stats stat, int change, Flags... flags){
		this(type, PP, -1, stat, change, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		if(this.getAccuracy() < 0 || willHit(this, attacker, defender, true)){

		}
		return BeforeResult.STOP;
	}
}
