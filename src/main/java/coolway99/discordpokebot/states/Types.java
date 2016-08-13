package coolway99.discordpokebot.states;

import coolway99.discordpokebot.Player;

public enum Types{
	NULL,
	NORMAL,
	FIGHTING,
	FLYING,
	POISON,
	GROUND,
	ROCK,
	BUG,
	GHOST,
	STEEL,
	FIRE,
	WATER,
	GRASS,
	ELECTRIC,
	PSYCHIC,
	ICE,
	DRAGON,
	DARK,
	FAIRY
	;

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
	
	public static boolean isImmune(Player attacker, Moves move, Player defender){
		for(Types type : getImmune(defender.primary)){
			if(move.getType(attacker) == type){
				return true;
			}
		}
		if(defender.hasSecondaryType()){
			for(Types type : getImmune(defender.secondary)){
				if(move.getType(attacker) == type){
					return true;
				}
			}
		}
		return false;
	}
	
	//THIS DOES NOT FACTOR IN STAB
	public static double getTypeMultiplier(Player attacker, Moves move, Player defender){
		if(isImmune(attacker, move, defender)) return 0;
		
		double multiplier = 1D; //We start with 1X damage
		
		for(Types type : getResistive(defender.primary)){
			if(move.getType(attacker) == type){
				multiplier /= 2;
				break;
			}
		}
		if(defender.hasSecondaryType()){
			for(Types type : getResistive(defender.secondary)){
				if(move.getType(attacker) == type){
					multiplier /= 2;
					break;
				}
			}
		}
		
		for(Types type : getEffective(defender.primary)){
			if(move.getType(attacker) == type){
				multiplier *= 2;
				break;
			}
		}
		if(defender.hasSecondaryType()){
			for(Types type : getEffective(defender.secondary)){
				if(move.getType(attacker) == type){
					multiplier *= 2;
					break;
				}
			}
		}
		return multiplier;
	}
}