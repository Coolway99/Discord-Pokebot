package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class StatChangeDamageMove extends DamageMove{

	private final float chance;
	private final int change;
	private final Stats stat;
	private final Who who;

	public StatChangeDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, float chance,
								Stats stat, int change, Who who, Battle_Priority priority, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
		this.chance = chance;
		this.change = change;
		this.stat = stat;
		this.who = who;
	}

	public StatChangeDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, float chance,
								Stats stat, int change, Who who, OldMoveFlags... flags){
		this(type, moveCategory, PP, power, accuracy, cost, chance, stat, change, who, Battle_Priority.P0, flags);
	}

	public StatChangeDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, float chance,
								Stats stat, int change, Who who, OldMoveFlags... flags){
		this(type, moveCategory, PP, power, accuracy, power+((change/10)*5), chance, stat, change, who, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		if(Pokebot.diceRoll(this.chance)){
			switch(this.who){
				case ATTACKER:{
					StatHandler.changeStat(channel, attacker, this.stat, this.change);
					break;
				}
				case DEFENDER:{
					switch(defender.getModifiedAbility()){
						case BIG_PECKS:{
							if(this.stat == Stats.DEFENSE && this.change < 0 && attacker != defender){ //TODO does not negate guard swap
								Pokebot.sendMessage(channel, defender.mention()+"'s ability prevents lowering it's defense!");
								return;
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
					break;
				}
			}
		}
	}

	public enum Who{
		ATTACKER,
		DEFENDER
	}
}
