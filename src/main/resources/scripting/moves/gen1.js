var FLAGS = API.MOVES.FLAGS;

API.registerMoves([
{
	id: "pound",
	name: "Pound",
	power: 40,
	pp: 35,
},
{
	id: "karatechop",
	name: "Karate Chop",
	type: API.TYPES.FIGHTING,
	power: 50,
	pp: 25,
	critRatio: 2, //Unused, no idea if I will actually use it
},
{
	id: "doubleslap",
	name: "Double Slap",
	accuracy: 85,
	power: 15,
	pp: 10,
	cost: 45,
	onAttack: API.MOVES.standardMultiHit,
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
},
{
	id: "megapunch",
	name: "Mega Punch",
	accuracy: 85,
	power: 80,
	pp: 20,
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.PUNCH_BASED],
},
{
	id: "payday",
	name: "Pay Day",
	accuracy: 100,
	power: 40,
	pp: 20,
	flags: [FLAGS.CAN_BE_MIRRORED],
	onSecondary: function(context){
		API.sendMessage(context.channel, "Coins scattered everywhere!");
	}
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
	}
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
	}
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
	}
},
{
	id: "scratch",
	name: "Scratch",
	power: 40,
	pp: 35,
},
{
		id: "vicegrip",
		name: "Vice Grip",
		power: 55,
		pp: 30,
},
/*{
		id: "guillotine",
		name: "Guillotine",
		accuracy: 30,
		power: -1,
		pp: 5,
		desc: "Deals damage to the target equal to the target's maximum HP. Ignores accuracy and evasiveness modifiers. This attack's accuracy is equal to (user's level - target's level + 30)%, and fails if the target is at a higher level. Pokemon with the Ability Sturdy are immune.",
		shortDesc: "OHKOs the target. Fails if user is a lower level.",
		ohko: true,
},*/
{
		id: "razorwind",
		name: "Razor Wind",
		category: API.MOVES.CATEGORY.SPECIAL,
		power: 80,
		pp: 10,
		desc: "Has a higher chance for a critical hit. This attack charges on the first turn and executes on the second. If the user is holding a Power Herb, the move completes in one turn.",
		shortDesc: "Charges, then hits foe(s) turn 2. High crit ratio.",
		flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.REQUIRES_CHARGE],
		critRatio: 2,
		target: API.MOVES.TARGET.WILL_HIT_ADJACENT_FOES,
},
]);

delete FLAGS;