package coolway99.discordpokebot.types;

import coolway99.discordpokebot.Moves;
import coolway99.discordpokebot.Player;

public enum Types{
	NULL,
	NORMAL,
	FIRE,
	GRASS,
	WATER
	;

	public static boolean isImmune(Types move, Types to){
		return false;
	}
	
	public static Types[] getResistive(Types type){
		switch(type){
			case FIRE:{
				return new Types[]{
						WATER
				};
			}
			case WATER:{
				return new Types[]{
						GRASS
				};
			}
			case GRASS:{
				return new Types[]{
						FIRE
				};
			}
			case NORMAL:
			default:{
				return new Types[]{
						
				};
			}
		}
	}
	
	public static Types[] getEffective(Types type){
		switch(type){
			case FIRE:{
				return new Types[]{
						GRASS
				};
			}
			case WATER:{
				return new Types[]{
						FIRE
				};
			}
			case GRASS:{
				return new Types[]{
						WATER
				};
			}
			case NORMAL:
			default:{
				return new Types[]{
						
				};
			}
		}
	}
	
	public static double getAttackMultiplier(Player attacker, Moves move, Player defender){
		if(isImmune(move.getType(), defender.primary) ||
				(defender.hasSecondaryType() ? isImmune(move.getType(), defender.secondary) : false))
			return 0;
		
		double multiplier = 1D; //We start with 1X damage
		
		for(Types type : getEffective(defender.primary)){
			if(move.getType() == type) multiplier /= 2;
		}
		if(defender.hasSecondaryType()){
			for(Types type : getEffective(defender.secondary)){
				if(move.getType() == type) multiplier /= 2;
			}
		}
		
		for(Types type : getResistive(defender.primary)){
			if(move.getType() == type) multiplier *= 2;
		}
		if(defender.hasSecondaryType()){
			for(Types type : getResistive(defender.secondary)){
				if(move.getType() == type) multiplier *= 2;
			}
		}
		
		if(attacker.primary == move.getType() || attacker.secondary == move.getType()){
			multiplier *= 1.5D; //TODO if a pokemon has adaptabilitity, then they get a +100% boost
		}
		
		return multiplier;	
	}
}
