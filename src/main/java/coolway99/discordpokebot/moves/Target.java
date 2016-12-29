package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.states.Position;

public enum Target{
	ADJACENT_FOE, //Can target an adjacent foe, but not an ally
	ADJACENT_ALLY, //Can target an adjacent ally
	ADJACENT, //Can target an adjacent foe or ally
	ANY_BUT_USER, //Can target any pokemon except the user
	SELF, //Can only target themselves

	WILL_HIT_ADJACENT_FOE(true), //WILL target all adjacent enemies
	WILL_HIT_ADJACENT(true), //WILL target all adjacent players
	WILL_HIT_ALL_FOES(true), //WILL target all enemies
	WILL_HIT_ALL_ALLIES(true), //WILL target all allies, including the user
	WILL_HIT_ALL(true), //WILL HIT EVERYONE
	;

	public final boolean willHitAllTargets;

	Target(){
		this.willHitAllTargets = false;
	}

	Target(boolean willHitAllTargets){
		this.willHitAllTargets = willHitAllTargets;
	}

	public boolean canHit(Position attacker, Position defender, boolean areSameSide){
		int distance = attacker.getDistance(defender);
		switch(this){
			case WILL_HIT_ADJACENT:
			case ADJACENT:
				return areSameSide ? distance == 1 : distance <= 1;
			case ADJACENT_ALLY:
				return areSameSide && distance == 1;
			case WILL_HIT_ADJACENT_FOE:
			case ADJACENT_FOE:
				return !areSameSide && distance <= 1;
			case SELF:
				return areSameSide && distance == 0;
			case ANY_BUT_USER:
				return !areSameSide || distance != 0;
			case WILL_HIT_ALL_FOES:
				return !areSameSide;
			case WILL_HIT_ALL_ALLIES:
				return areSameSide;
			case WILL_HIT_ALL:
				return true;
		}
		return false;
	}
}
