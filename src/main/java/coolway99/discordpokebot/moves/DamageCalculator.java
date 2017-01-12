package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Types;
import org.jetbrains.annotations.NotNull;

public class DamageCalculator{

	@NotNull
	public final Player attacker;
	public final MoveWrapper move;
	public final Player defender;

	public double attack;
	public double defence;
	public double power;
	public Types type;
	public double STAB;
	public double additionalModifiers;

	public int randomVariation;

	private Integer damageCalculated = null;

	public DamageCalculator(@NotNull Player attacker, MoveWrapper move, Player defender){
		this.attacker = attacker;
		this.move = move;
		this.defender = defender;

		if(move.getCategory() == MoveCategory.SPECIAL){
			this.attack = attacker.getSpecialAttackStat();
			this.defence = defender.getDefenseStat();
		} else {
			this.attack = attacker.getAttackStat();
			this.defence = defender.getDefenseStat();
		}

		this.type = move.getType();
		this.STAB = attacker.hasType(move.getType()) ? 1.5 : 1;
		this.additionalModifiers = 1;

		this.randomVariation = Pokebot.ran.nextInt(100-85+1)+85+1; //Random chance, it would be 85-99 if there wasn't the +1
	}

	public int getDamage(){
		if(this.damageCalculated == null) return this.computeDamage();
		return this.damageCalculated;
	}

	public int computeDamage(){
		if(this.damageCalculated != null) return this.damageCalculated;
		double a = ((2*this.attacker.level)+10D)/250D;
		double modifier = this.STAB * Types.getTypeMultiplier(this.type, this.defender) * (this.randomVariation/100D);
		double b = this.attack / this.defence;
		double ret = ((a*b*this.power)+2)*modifier;
		this.damageCalculated = (int) ret;
		return (int) ret; //implicit math.floor
	}

	/**
	 * If this DamageCalculationHelper is "used", then the damage is already computed and cannot be changed
	 */
	public boolean isUsed(){
		return this.damageCalculated != null;
	}
}
