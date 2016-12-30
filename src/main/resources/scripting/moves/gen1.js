var FLAGS = API.MOVES.FLAGS;

API.registerMoves([
{
	id: "pound",
	name: "Pound",
	power: 40,
	pp: 35,
	description: "The target is physically pounded with a long tail, a foreleg, or the like.",
},
{
	id: "karatechop",
	name: "Karate Chop",
	type: API.TYPES.FIGHTING,
	power: 50,
	pp: 25,
	critRatio: 2, //Unused, no idea if I will actually use it
	description: "The target is attacked with a sharp chop.",// Critical hits land more easily.",
},
{
	id: "doubleslap",
	name: "Double Slap",
	accuracy: 85,
	power: 15,
	pp: 10,
	cost: 45,
	onAttack: API.MOVES.standardMultiHit,
	description: "The target is slapped repeatedly, back and forth, two to five times in a row.",
},
{
	id: "cometpunch",
	name: "Comet Punch",
	accuracy: 85,
	power: 18,
	pp: 15,
	cost: (18*3),
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.PUNCH_BASED],
	onAttack: API.MOVES.standardMultiHit,
	description: "The target is hit with a flurry of punches that strike two to five times in a row.",
},
{
	id: "megapunch",
	name: "Mega Punch",
	accuracy: 85,
	power: 80,
	pp: 20,
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.PUNCH_BASED],
	description: "The target is slugged by a punch thrown with muscle-packed power.",
},
{
	id: "payday",
	name: "Pay Day",
	power: 40,
	pp: 20,
	flags: [FLAGS.CAN_BE_MIRRORED],
	onSecondary: function(context){
		API.sendMessage(context.channel, "Coins scattered everywhere!");
	},
	description: "Numerous coins are hurled at the target to inflict damage.",
},
{
	id: "firepunch",
	name: "Fire Punch",
	type: API.TYPES.FIRE,
	accuracy: 100,
	power: 75,
	pp: 15,
	cost: 80,
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.PUNCH_BASED],
	onSecondary: function(context, attacker, defender){
		if(API.diceRoll(10)){
			API.MOVES.UTILS.burn(context.channel, defender);
		}
	},
	description: "The target is punched with a fiery fist. This may also leave the target with a burn.",
},
{
	id: "icepunch",
	name: "Ice Punch",
	type: API.TYPES.ICE,
	accuracy: 100,
	power: 75,
	pp: 15,
	cost: 80,
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.PUNCH_BASED],
	onSecondary: function(context, attacker, defender){
		if(API.diceRoll(10)){
			API.MOVES.UTILS.freeze(context.channel, defender);
		}
	},
	description: "The target is punched with an icy fist. This may also leave the target frozen.",
},
{
	id: "thunderpunch",
	name:"Thunder Punch",
	type: API.TYPES.ELECTRIC,
	accuracy: 100,
	power: 75,
	pp: 15,
	cost: 80,
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.PUNCH_BASED],
	onSecondary: function(context, attacker, defender){
		if(API.diceRoll(10)){
			API.MOVES.UTILS.paralyze(context.channel, defender);
		}
	},
	description: "The target is punched with an electrified fist. This may also leave the target with paralysis.",
},
{
	id: "scratch",
	name: "Scratch",
	power: 40,
	pp: 35,
	description: "Hard, pointed, sharp claws rake the target to inflict damage.",
},
{
	id: "vicegrip",
	name: "Vice Grip",
	power: 55,
	pp: 30,
	description: "The target is gripped and squeezed from both sides to inflict damage",
},
{
	id: "guillotine",
	name: "Guillotine",
	accuracy: function(context, attacker, defender){
		//When context == null, we're calling this to get the display accuracy of the move
		if(context == null) return 30;
		//onTry is ran before the accuracy check, therefore we know that this move will work
		return (attacker.level - defender.level) + 30;
	},
	pp: 5,
	cost: 120,
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.OHKO],
	onTry: function(context, attacker, defender){
		return attacker.level >= defender.level;
	},
	power: 9999,
	description: "A vicious, tearing attack with big pincers. The target faints instantly if this attack hits.",
},
{
	id: "razorwind",
	name: "Razor Wind",
	category: API.MOVES.CATEGORY.SPECIAL,
	accuracy: function(context, attacker, defender){
		if(context == null) return 100;
		if(context.battle == null){
			return 100;
		} else {
			if(attacker.lastMoveData == 1){
				return 100;
			} else {
				return 10000; //The "charge" should always hit
			}
		}
	},
	power: 80,
	pp: 10,
	cost: 100,
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.REQUIRES_CHARGE],
	critRatio: 2,
	onAttack: function(context, attacker, defender){
		API.MOVES.UTILS.chargeMove(context, attacker, this, defender);
	},
	target: API.MOVES.TARGET.WILL_HIT_ADJACENT_FOES,
	message: "%s is whipping up a whirlwind!",
	displayUsedMove: false,
	description: "In this two-turn attack, blades of wind hit opposing Pokémon on the second turn.",// Critical hits land more easily.",
},
]);

delete FLAGS;