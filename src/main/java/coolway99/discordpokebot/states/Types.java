package coolway99.discordpokebot.states;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.old.OldMove;
import coolway99.discordpokebot.moves.MoveWrapper;

public enum Types{
	NULL(0x68A090),
	NORMAL(0xA8A878),
	FIGHTING(0xC03028),
	FLYING(0xA890F0),
	POISON(0xA890F0),
	GROUND(0xE0C068),
	ROCK(0xE0C068),
	BUG(0xA8B820),
	GHOST(0x705898),
	STEEL(0xB8B8D0),
	FIRE(0xF08030),
	WATER(0x6890F0),
	GRASS(0x78C850),
	ELECTRIC(0xF8D030),
	PSYCHIC(0xF85888),
	ICE(0x98D8D8),
	DRAGON(0x7038F8),
	DARK(0x705848),
	FAIRY(0xEE99AC)
	;

	private final int color;

	Types(int color){
		this.color = color;
	}

	public int getColor(){
		return this.color;
	}

	public static Types[] getImmune(Types type){
		switch(type){
			case NORMAL:{
				return new Types[]{
						GHOST
				};
			}
			case FLYING:{
				return new Types[]{
						GROUND
				};
			}
			case GROUND:{
				return new Types[]{
						ELECTRIC
				};
			}
			case GHOST:{
				return new Types[]{
						NORMAL,
						FIGHTING
				};
			}
			case STEEL:{
				return new Types[]{
						POISON
				};
			}
			case DARK:{
				return new Types[]{
						PSYCHIC
				};
			}
			case FAIRY:{
				return new Types[]{
						DRAGON
				};
			}
			default:{
				return new Types[]{};
			}
		}
	}
	
	//Get a list of types that this takes MORE damage from
	public static Types[] getEffective(Types type){
		switch(type){
			case NORMAL:{
				return new Types[]{
						FIGHTING
				};
			}
			case FIGHTING:{
				return new Types[]{
						FLYING,
						PSYCHIC,
						FAIRY
				};
			}
			case FLYING:{
				return new Types[]{
						ROCK,
						ELECTRIC,
						ICE
				};
			}
			case POISON:{
				return new Types[]{
						GROUND,
						PSYCHIC
				};
			}
			case GROUND:{
				return new Types[]{
						WATER,
						GRASS,
						ICE
				};
			}
			case ROCK:{
				return new Types[]{
						FIGHTING,
						GROUND,
						STEEL,
						WATER,
						GRASS
				};
			}
			case BUG:{
				return new Types[]{
						FLYING,
						ROCK,
						FIRE
				};
			}
			case GHOST:{
				return new Types[]{
						GHOST,
						DARK
				};
			}
			case STEEL:{
				return new Types[]{
						FIGHTING,
						GROUND,
						FIRE
				};
			}
			case FIRE:{
				return new Types[]{
						GROUND,
						ROCK,
						WATER
				};
			}
			case WATER:{
				return new Types[]{
						GRASS,
						ELECTRIC
				};
			}
			case GRASS:{
				return new Types[]{
						FLYING,
						POISON,
						BUG,
						FIRE,
						ICE
				};
			}
			case ELECTRIC:{
				return new Types[]{
						GROUND
				};
			}
			case PSYCHIC:{
				return new Types[]{
						BUG,
						GHOST,
						DARK
				};
			}
			case ICE:{
				return new Types[]{
						FIGHTING,
						ROCK,
						STEEL,
						FIRE
				};
			}
			case DRAGON:{
				return new Types[]{
						ICE,
						DRAGON,
						FAIRY
				};
			}
			case DARK:{
				return new Types[]{
						FIGHTING,
						BUG,
						FAIRY
				};
			}
			case FAIRY:{
				return new Types[]{
						POISON,
						STEEL
				};
			}
			default:{
				return new Types[]{};
			}
		}
	}
	
	//Get a list of types that this type takes less damage from
	//"Get the defender's type, if the move type equals one of these, take half damage"
	//IN: the defender's type. OUT: The types that do half damage to the defender
	public static Types[] getResistive(Types type){
		switch(type){
			case NORMAL:{
				return new Types[]{
						
				};
			}
			case FIGHTING:{
				return new Types[]{
						ROCK,
						BUG,
						DARK
				};
			}
			case FLYING:{
				return new Types[]{
						FIGHTING,
						BUG,
						GRASS
				};
			}
			case POISON:{
				return new Types[]{
						FIGHTING,
						POISON,
						BUG,
						GRASS,
						FAIRY
				};
			}
			case GROUND:{
				return new Types[]{
						POISON,
						ROCK
				};
			}
			case ROCK:{
				return new Types[]{
						NORMAL,
						FLYING,
						POISON,
						FIRE
				};
			}
			case BUG:{
				return new Types[]{
						FIGHTING,
						GROUND,
						GRASS
				};
			}
			case GHOST:{
				return new Types[]{
						POISON,
						BUG
				};
			}
			case STEEL:{
				return new Types[]{
						NORMAL,
						FLYING,
						ROCK,
						BUG,
						STEEL,
						GRASS,
						PSYCHIC,
						ICE,
						DRAGON,
						FAIRY
				};
			}
			case FIRE:{
				return new Types[]{
						BUG,
						STEEL,
						FIRE,
						GRASS,
						ICE,
						FAIRY
				};
			}
			case WATER:{
				return new Types[]{
						STEEL,
						FIRE,
						WATER,
						ICE
				};
			}
			case GRASS:{
				return new Types[]{
						GROUND,
						WATER,
						GRASS,
						ELECTRIC
				};
			}
			case ELECTRIC:{
				return new Types[]{
						FLYING,
						STEEL,
						ELECTRIC
				};
			}
			case PSYCHIC:{
				return new Types[]{
						FIGHTING,
						PSYCHIC
				};
			}
			case ICE:{
				return new Types[]{
						ICE
				};
			}
			case DRAGON:{
				return new Types[]{
						FIRE,
						WATER,
						GRASS,
						ELECTRIC
				};
			}
			case DARK:{
				return new Types[]{
						GHOST,
						DARK
				};
			}
			case FAIRY:{
				return new Types[]{
						FIGHTING,
						BUG,
						DARK
				};
			}
			default:{
				return new Types[]{};
			}
		}
	}

	public static boolean isImmune(Player attacker, MoveWrapper move, Player defender){
		return isImmune(attacker, move.getType(), defender);
	}
	
	public static boolean isImmune(Player attacker, OldMove move, Player defender){
		return isImmune(attacker, move.getType(attacker), defender);
	}

	public static boolean isImmune(Player attacker, Types moveType, Player defender){
		for(Types type : getImmune(defender.primary)){
			if(moveType == type){
				return true;
			}
		}
		if(defender.hasSecondaryType()){
			for(Types type : getImmune(defender.secondary)){
				if(moveType == type){
					return true;
				}
			}
		}
		return false;
	}

	public static double getTypeMultiplier(Player attacker, MoveWrapper move, Player defender){
		return getTypeMultiplier(attacker, move.getType(), defender);
	}
	
	//THIS DOES NOT FACTOR IN STAB
	public static double getTypeMultiplier(Player attacker, OldMove move, Player defender){
		return getTypeMultiplier(attacker, move.getType(attacker), defender);
	}

	public static double getTypeMultiplier(Player attacker, Types moveType, Player defender){
		if(isImmune(attacker, moveType, defender)) return 0;
		
		double multiplier = 1D; //We start with 1X damage
		
		for(Types type : getResistive(defender.primary)){
			if(moveType == type){
				multiplier /= 2;
				break;
			}
		}
		if(defender.hasSecondaryType()){
			for(Types type : getResistive(defender.secondary)){
				if(moveType == type){
					multiplier /= 2;
					break;
				}
			}
		}
		
		for(Types type : getEffective(defender.primary)){
			if(moveType == type){
				multiplier *= 2;
				break;
			}
		}
		if(defender.hasSecondaryType()){
			for(Types type : getEffective(defender.secondary)){
				if(moveType == type){
					multiplier *= 2;
					break;
				}
			}
		}
		return multiplier;
	}
}