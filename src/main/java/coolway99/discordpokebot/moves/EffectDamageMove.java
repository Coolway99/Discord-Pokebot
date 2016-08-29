package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class EffectDamageMove extends DamageMove{

	private final RunEffect effect;
	private final float chance;

	public EffectDamageMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost,
							float chance, RunEffect effect, Battle_Priority priority, Flags... flags){
		super(type, moveType, PP, power, accuracy, cost, priority, flags);
		this.effect = effect;
		this.chance = chance;
	}

	public EffectDamageMove(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, float chance,
							RunEffect effect, Flags... flags){
		this(type, moveType, PP, power, accuracy, cost, chance, effect, Battle_Priority.P0, flags);
	}

	@Override
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		if(diceRoll(this.chance)) this.effect.runEffect(channel, attacker, defender, damage);
	}

	@FunctionalInterface
	protected interface RunEffect{
		void runEffect(IChannel channel, Player attacker, Player defender, int damage);
	}
}
