package coolway99.discordpokebot.moves.old;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.moves.BattlePriority;
import coolway99.discordpokebot.moves.MoveCategory;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

@Deprecated
public class EffectDamageMove extends DamageMove{

	private final RunEffect effect;
	private final float chance;

	public EffectDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost,
							float chance, RunEffect effect, BattlePriority priority, OldMoveFlags... flags){
		super(type, moveCategory, PP, power, accuracy, cost, priority, flags);
		this.effect = effect;
		this.chance = chance;
	}

	public EffectDamageMove(Types type, MoveCategory moveCategory, int PP, int power, int accuracy, int cost, float chance,
							RunEffect effect, OldMoveFlags... flags){
		this(type, moveCategory, PP, power, accuracy, cost, chance, effect, BattlePriority.P0, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		if(Pokebot.diceRoll(this.chance)) this.effect.runEffect(channel, attacker, defender, damage);
	}

	@FunctionalInterface
	protected interface RunEffect{
		void runEffect(IChannel channel, Player attacker, Player defender, int damage);
	}
}
