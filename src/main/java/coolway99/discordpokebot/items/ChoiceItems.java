/*package coolway99.discordpokebot.items;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.states.Stats;
import sx.blah.discord.handle.obj.IChannel;

public abstract class ChoiceItems{

	private MoveSet lockedMove = null;
	private final Stats boostedStat;

	private ChoiceItems(int cost, Stats stat){
		//super(cost);
		this.boostedStat = stat;
	}

	@Override
	public MoveSet onMove(IChannel channel, Player attacker, MoveSet move, Player defender){
		MoveSet set = move;
		if(this.lockedMove == null){
			this.lockedMove = move;
		}
		if(this.lockedMove != move){
			set = this.lockedMove;
		}
		if(set.getPP() <= 0); //TODO struggle
		return set;
	}

	public float modifyAttack(IChannel channel, Player attacker, Move move, Player defender){
		//if(move.)
	}
}*/