package coolway99.discordpokebot.states;

//TODO Implement all of this inside Battles
public class Effects{
	//Can only have one of these at a time
	public enum NonVolatile{
		NORMAL,
		BURN,
		FROZEN, /*Prevents all movement, Fire-type moves used on the defender will thaw it, and
		a select number of fire-type moves used by the afflicted will also thaw it*/
		PARALYSIS, //Prevents attacking 1/4th of the time. Also 1/4th's speed
		POISON, //Does 1/8th MAXHP damage unless other special conditions apply
		TOXIC, //Starts at 1/16th MAXHP damage, then goes up by +1/16th every turn
		SLEEP,
		FAINTED, //Used to signify a player has fainted
	}
	
	//A pokemon can be affected by any combination of these at a time
	public enum Volatile{
		CONFUSION,
		CURSE, //Only if used by a ghost type. Will cause the user to lose 1/4th of MAXHP per turn
		EMBARGO, //Prevents held items from being used
		ENCORE, //Forces the pokemon to repeat the last attack
		FLINCH, //Only lasts for one turn, does not carry over to the next one, so you must attack first
		HEAL_BLOCK, //Prevents the user from healing
		IDENTIFICATION, //The opponent's evasion multiplier will not be factored in
		NIGHTMARE, //A sleeping pokemon loses 1/4th of their HP per turn, wears off when the afflicted awakes
		PARTIALLY_TRAPPED, /*Does not affect ghosts, will do 1/8th MAXHP damage per turn.
		Lasts only 4-5 turns unless a grip-claw is held, then it lasts 7 turns
		Rapid spin removes this status, and the target can still attack during this state
		The user of the move however cannot attack
		*/
		PERISH_SONG, //All those that hear this song will faint after 3 turns, including the user
		SEEDING, //aka Leech Seed. Grass type pokemon are immune, those aflicted lose 1/8th MAXHP per turn and
		//and the opponent is healed by the same amount
		TAUNT, //Cannot use any non-damaging moves for 3 turns
		TELEKINETIC_LEVITATION, //Immune to ground-type moves, spikes, toxic spikes, and arena trap for 3 turns
		//However, all moves except OHKO moves hit the target regardless, except when semiinvul is in effect
		TORMENT //Cannot use the same move twice in a row. Struggle will be forced to be used every second turn if there is only one move left
	}
	
	//Effects that are for the battle and affect it
	public enum VBattle{
		ABILITY_BLOCK, //This pokemon's ability is blocked, this isn't an effect in the real game
		AQUA_RING, //Restores 1/16th of it's HP every turn
		ENDURE, //The pokemon will survive with at least 1HP
		//TODO Instead of put this here, it's going to be battle-wide
		//CENTER_OF_ATTENTION, //In multi-battles, this pokemon is the only one anyone can hit
		DEFENSE_CURL, //This causes the power of Rollout and IceBall to be doubled
		CHARGING, //Unless a power herb is used, this is Turn 1 of Sky Attack and Razor Wind (Glowing and Whipping Up A Whirlwind combined)
		ROOTING, //Ingrain restores 1/16th HP every turn, but is forcefully grounded
		MAGIC_COAT, //Reflect most status-moves back at the user
		MAGNETIC_LEVIATION, //Immune to ground-type, spikes, and toxic spikes, and arena trap for 5 turns
		MINIMIZE, //Some attacks do double damage and always hit
		PROTECTION, //Is immune to attacks and negative status moves, but if it's hit then it's removed. Lasts 1 turn
		RECHARGING, //Cannot attack next turn
		SEMI_INVULNERABLE, //Cannot be attacked except by certain moves and status's.
		SUBSTITUTE, //Uses up to 1/4th of it's total HP and will create a substitute that will absorb the damage until it breaks
		TAKING_AIM, //negates Semi_Invul, makes the next damage move always hit
		TAKING_IN_SUNLIGHT, //Unless a power herb is used or it's harsh sunlight, this is turn 1 of Solar Beam
		WITHDRAWING, //Unless a power herb is used, this is turn one of skull bash. Also boosts defence
	}
}
