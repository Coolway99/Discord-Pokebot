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
	PP: 25,
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
	cost: (80*3),
	flags: [FLAGS.MAKES_CONTACT, FLAGS.CAN_BE_MIRRORED, FLAGS.PUNCH_BASED],
	onAttack: API.MOVES.standardMultiHit,
},
{
	id: "payday",
	name: "Pay Day",
	accuracy: 100,
	power: 40,
	pp: 20,
	cost: (80*3),
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
			API.UTILS.burn(context.channel, defender);
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
			API.UTILS.freeze(context.channel, defender);
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
	onAttack: API.MOVES.standardMultiHit,
	onSecondary: function(context, attacker, defender){
		if(API.diceRoll(10)){
			API.UTILS.paralyze(context.channel, defender);
		}
	}
},
{
	id: "scratch",
	name: "Scratch",
	power: 40,
	pp: 35,
},
]);

/*	"vicegrip": {
		num: 11,
		accuracy: 100,
		basePower: 55,
		category: "Physical",
		desc: "No additional effect.",
		shortDesc: "No additional effect.",
		id: "vicegrip",
		name: "Vice Grip",
		pp: 30,
		priority: 0,
		flags: {contact: 1, protect: 1, mirror: 1},
		secondary: false,
		target: "normal",
		type: "Normal",
		contestType: "Tough",
	},

	"guillotine": {
		num: 12,
		accuracy: 30,
		basePower: 0,
		category: "Physical",
		desc: "Deals damage to the target equal to the target's maximum HP. Ignores accuracy and evasiveness modifiers. This attack's accuracy is equal to (user's level - target's level + 30)%, and fails if the target is at a higher level. Pokemon with the Ability Sturdy are immune.",
		shortDesc: "OHKOs the target. Fails if user is a lower level.",
		id: "guillotine",
		name: "Guillotine",
		pp: 5,
		priority: 0,
		flags: {contact: 1, protect: 1, mirror: 1},
		ohko: true,
		secondary: false,
		target: "normal",
		type: "Normal",
		contestType: "Cool",
	},

	"razorwind": {
		num: 13,
		accuracy: 100,
		basePower: 80,
		category: "Special",
		desc: "Has a higher chance for a critical hit. This attack charges on the first turn and executes on the second. If the user is holding a Power Herb, the move completes in one turn.",
		shortDesc: "Charges, then hits foe(s) turn 2. High crit ratio.",
		id: "razorwind",
		name: "Razor Wind",
		pp: 10,
		priority: 0,
		flags: {charge: 1, protect: 1, mirror: 1},
		onTry: function (attacker, defender, move) {
			if (attacker.volatiles['twoturnmove']) {
				if (attacker.volatiles['twoturnmove'].duration === 2) return null;
				attacker.removeVolatile(move.id);
				return;
			}
			this.add('-prepare', attacker, move.name, defender);
			if (!this.runEvent('ChargeMove', attacker, defender, move)) {
				this.add('-anim', attacker, move.name, defender);
				if (move.spreadHit) {
					attacker.addVolatile('twoturnmove', defender);
					attacker.volatiles['twoturnmove'].duration = 1;
				}
				return;
			}
			attacker.addVolatile('twoturnmove', defender);
			return null;
		},
		critRatio: 2,
		secondary: false,
		target: "allAdjacentFoes",
		type: "Normal",
		contestType: "Cool",
	},*/

delete FLAGS;