package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.states.Types;

public class DamageCalculationHelper{

	private final Player attacker;
	private final MoveWrapper move;
	private final Player defender;

	public double attack;
	public double defence;
	public double power;
	public double typeAdvantage;
	public double STAB;
	public double additionalModifiers;

	public int randomVariation;

	public DamageCalculationHelper(Player attacker, MoveWrapper move, Player defender){
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

		this.typeAdvantage = Types.getTypeMultiplier(move, defender);
		this.STAB = attacker.hasType(move.getType()) ? 1.5 : 1;
		this.additionalModifiers = 1;

		this.randomVariation = Pokebot.ran.nextInt(100-85+1)+85+1; //Random chance, it would be 85-99 if there wasn't the +1
	}

	public int computeDamage(){
		double a = ((2*this.attacker.level)+10D)/250D;
		double modifier = this.STAB * this.typeAdvantage * (this.randomVariation/100D);
		double b = this.attack / this.defence;
		double ret = ((a*b*this.power)+2)*modifier;
		return (int) ret; //implicit math.floor
	}

	public void changeMoveType(Types type){
		this.typeAdvantage = Types.getTypeMultiplier(type, this.defender);
	}
}
