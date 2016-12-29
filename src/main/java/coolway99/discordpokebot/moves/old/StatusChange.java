package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Messages;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class StatusChange extends OldMove{

	private static final int DEFAULT_POINTS_PER_LEVEL = 25;

	final Stats stat;
	final byte change;

	public StatusChange(Types type, int PP, int accuracy, int cost, Stats stat, int change,
						Battle_Priority priority, OldMoveFlags... flags){
		super(type, MoveCategory.STATUS, PP, -1, accuracy, cost, priority, flags);
		this.stat = stat;
		this.change = (byte) change;
	}

	public StatusChange(Types type, int PP, int accuracy, int cost, Stats stat, int change, OldMoveFlags... flags){
		this(type, PP, accuracy, cost, stat, change, Battle_Priority.P0, flags);
	}

	public StatusChange(Types type, int PP, int accuracy, Stats stat, int change, OldMoveFlags... flags){
		this(type, PP, accuracy, 25*change, stat, change, flags);
	}

	public StatusChange(Types type, int PP, Stats stat, int change, OldMoveFlags... flags){
		this(type, PP, -1, stat, change, flags);
	}

	@Override
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		if(this.getAccuracy() < 0 || willHit(this, attacker, defender, true)){
			switch(defender.getModifiedAbility()){
				case BIG_PECKS:{
					if(this.stat == Stats.DEFENSE && this.change < 0 && attacker != defender){ //TODO does not negate guard swap
						Pokebot.sendMessage(channel, defender.mention()+"'s ability prevents lowering it's defense");
						return BeforeResult.STOP;
					}
					break;
				}
				case CLEAR_BODY:{
					if(this.change < 0 && attacker != defender){
						Pokebot.sendMessage(channel, defender.mention()+"'s ability prevents lowering it's stats!");
					}
					break;
				}
				default:
					break;
			}
			StatHandler.changeStat(channel, defender, this.stat, this.change);
		} else {
			Messages.miss(channel, attacker);
		}
		return BeforeResult.STOP;
	}
}
