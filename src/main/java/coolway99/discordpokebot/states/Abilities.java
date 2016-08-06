package coolway99.discordpokebot.states;

public enum Abilities{
	MC_NORMAL_PANTS(0), //Does absolutely nothing
	ADAPTABILITY(75), //Increases stab to 2x instead of 1.5x
	AERILATE(150), //Changes all moves to flying type, Also increases power of all moves by 30%
	AFTERMATH(100), //Does damage to the attacker when this pokemon faints unless a pokemon with damp is on the field
	AIR_LOCK(80), //TODO Prevents weather effects, and makes moves that change the weather fail
	ANALYTIC(130), //TODO If the pokemon already attacked this turn, then it does 30% more damage
	//Anger Point raises attack upon taking a critical hit, which isn't going to be implemented
	//Anticipation isn't even worth adding
	//Arena Trap prevents the foe from fleeing
	AROMA_VEIL(100), //Prevents Taunt, Torment, Encore, Disable, Cursed Body, Heal Block, and Infatuation
	BULLETPROOF(80),
	DAMP(80), //Prevents Self-Destruct, Explosion, and the ability Aftermath from working
	
	NORMALIZE(100),
	POISON_HEAL(100),
	;
	
	private final int cost;
	
	Abilities(int cost){
		this.cost = cost;
	}
	
	public int getCost(){
		return this.cost;
	}
}
