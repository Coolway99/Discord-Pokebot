package coolway99.discordpokebot.moves.rewrite;

//This class would not be possible without showdown's moveScript source code database.

/* The following is taken directly from showdown source code:

List of flags and their descriptions:

authentic: Ignores a target's substitute.
bite: Power is multiplied by 1.5 when used by a Pokemon with the Ability Strong Jaw.
bullet: Has no effect on Pokemon with the Ability Bulletproof.
charge: The user is unable to make a moveScript between turns.
contact: Makes contact.
defrost: Thaws the user if executed successfully while the user is frozen.
distance: Can target a Pokemon positioned anywhere in a Triple Battle.
gravity: Prevented from being executed or selected during Gravity's effect.
heal: Prevented from being executed or selected during Heal Block's effect.
mirror: Can be copied by Mirror Move.
nonsky: Prevented from being executed or selected in a Sky Battle.
powder: Has no effect on Grass-type Pokemon, Pokemon with the Ability Overcoat, and Pokemon holding Safety Goggles.
protect: Blocked by Detect, Protect, Spiky Shield, and if not a Status moveScript, King's Shield.
pulse: Power is multiplied by 1.5 when used by a Pokemon with the Ability Mega Launcher.
punch: Power is multiplied by 1.2 when used by a Pokemon with the Ability Iron Fist.
recharge: If this moveScript is successful, the user must recharge on the following turn and cannot make a moveScript.
reflectable: Bounced back to the original user by Magic Coat or the Ability Magic Bounce.
snatch: Can be stolen from the original user and instead used by another Pokemon using Snatch.
sound: Has no effect on Pokemon with the Ability Soundproof.

*/

/**
 * A set of flags used for various common effects a moveScript can have
 */
public enum MoveFlags{
	HIT_THROUGH_SUBSTITUTE, //The moveScript can hit through a substitute
	BITE_BASED, //The moveScript is based on bite/biting
	BULLET_BASED, //The moveScript is based on bullets/balls
	REQUIRES_CHARGE, //The moveScript needs to be charged up first
	MAKES_CONTACT, //The moveScript makes contact
	CAN_DEFROST, //The moveScript can remove the defrost effect
	IGNORES_DISTANCE, //The moveScript ignores distance in triple battles
	GRAVITY_DISABLES, //Gravity disables the moveScript
	HEAL_BLOCK_DISABLES, //Heal Block disables the moveScript
	CAN_BE_MIRRORED, //The moveScript can be mirror moveScript'd
	NON_SKY, //The moveScript cannot be used in a sky battle
	POWDER_BASED, //The moveScript is powder based
	HIT_THROUGH_PROTECT, //The moveScript can hit through protect
	PULSE_BASED, //The moveScript is based on pulse
	PUNCH_BASED, //The moveScript is based on punching
	RECHARGE, //The user needs to recharge if the moveScript hits
	REFLECTABLE, //The moveScript can be reflected
	SNATCH, //Can be stolen via snatch and used
	SOUND_BASED, //The moveScript is based off of sound
}
