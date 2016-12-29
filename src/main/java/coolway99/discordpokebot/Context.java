package coolway99.discordpokebot;

import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.moves.MoveWrapper;
import sx.blah.discord.handle.obj.IChannel;

/**
 * A class created to keep context between different states.
 */
public class Context{
	public IChannel channel = null;
	public Battle battle = null;
	public MoveWrapper move = null;
}
