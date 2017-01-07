package coolway99.discordpokebot.abilities;

@Deprecated
public enum OldAbilities{
	//Anger Point raises attack upon taking a critical hit, which isn't going to be implemented
	//Anticipation isn't even worth adding
	//Arena Trap prevents the foe from fleeing
	//AROMA_VEIL(100), //Prevents Taunt, Torment, Encore, Disable, Cursed Body, Heal Block, and Infatuation, aka moves that limit
	// their move choices
	//TODO Not really usable AURA_BREAK(80), //The effects of Aura abilities are reversed (Fairy Aura and Dark Aura)
	BULLETPROOF(80), //Protects the pokemon from some ball-based moves.
	/*TODO Implement these
	BAD_DREAMS(130), //Reduces a sleeping foe's HP by 1/8th
	//Battery raises the power of allies's special moves (GEN VII)
	//BATTLE_ARMOR The pokemon is protected against critical hits
	BERSERK(120), //(GEN VII) Raises special attack when HP is below half
	// Heart Swap, or Guard Split. Mold Breaker, Teravolt, and Turboblaze ignore this*/
	BIG_PECKS(50), //Protects the pokemon from defence-lowering attacks, except self-inflicted. Does not negate Guard Swap,
	BLAZE(60), //If the user has less than 1/3rd of it's HP remaining, fire-type moves are boosted by 50%
	/*
	//TODO CHEEK_POUCH // Restores 1/3rd MAX HP as well when the pokemon eats a berry, - after the effect is applied, only if
	// it's a held berry eaten by a move.
	CHLOROPHYLL(80), //Boosts the pokemon's speed in sunshine
	//Ignored from Teravolt, Moldbreaker, and Turboblaze. See page for list of moves that don't trigger it*/
	CLEAR_BODY(120), //Prevents the pokemon's stats from being lowered, does not prevent self-inflicted status conditions
	/*
	//CLOUD_NINE is the same as AIR_LOCK
	//TODO COLOR_CHANGE(125), //Changes type to that of the move*/
	//COMATOSE(180), //(GEN VII), Prevents any status condition except sleep. TODO Unknown the specifics of it
	DAMP(80), //Prevents Self-Destruct, Explosion, and the ability Aftermath from working
	
	NORMALIZE(100),
	POISON_HEAL(100),
	;
	
	private final int cost;
	
	OldAbilities(int cost){
		this.cost = cost;
	}
	
	public int getCost(){
		return this.cost;
	}
}
