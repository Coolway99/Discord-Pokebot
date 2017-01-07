

var MoveAPI = Java.type("coolway99.discordpokebot.moves.MoveAPI");
var AbilityAPI = Java.type("coolway99.discordpokebot.abilities.AbilityAPI");
var Pokebot = Java.type("coolway99.discordpokebot.Pokebot");

var API = {
	TYPES: Java.type("coolway99.discordpokebot.states.Types"),
	STATS: Java.type("coolway99.discordpokebot.states.Stats"),
	SUBSTATS: Java.type("coolway99.discordpokebot.states.SubStats"),

	EFFECTS:{
		NONVOLATILE: Java.type("coolway99.discordpokebot.states.Effects.NonVolatile"),
		VOLATILE: Java.type("coolway99.discordpokebot.states.Effects.Volatile"),
		VBATTLE: Java.type("coolway99.discordpokebot.states.Effects.VBattle"),
		BATTLE: Java.type("coolway99.discordpokebot.battles.BattleEffects"),
	},

	MOVES:{
		CATEGORY: Java.type("coolway99.discordpokebot.moves.MoveCategory"),
		FLAGS: Java.type("coolway99.discordpokebot.moves.MoveFlags"),
		TARGET: Java.type("coolway99.discordpokebot.moves.Target"),
		UTILS: Java.type("coolway99.discordpokebot.moves.MoveUtils"),

		register: MoveAPI.register,
	},

	ABILITIES:{

		register: AbilityAPI.register,
	},

	RANDOM: Pokebot.ran,
	MESSAGES: Java.type("coolway99.discordpokebot.Messages"),


	diceRoll: Pokebot.diceRoll,
	sendMessage: Pokebot.sendMessage,
};

delete AbilityAPI;
delete MoveAPI;
delete Pokebot;
Object.freeze(API);

//This is the example (default) move:
/*{
	//The type of the move, defaults to normal
	type: API.TYPES.NORMAL,
	//The power of the move, must be defined
	power: 0,
	//The category of the move, defaults to physical
	category: API.MOVE.CATEGORY.PHYSICAL,
	//The PP of the move, must be defined
	pp: 0,
	//The accuracy of the move, defaults to 100
	accuracy: 100,
	//The priority of the move, defaults to 0
	priority: 0,
	//The cost of the move, defaults to what power is defined as
	cost: power,
	//Any flags the move has, defaults to unable to hit through protect, can be mirror moved, and makes contact
	flags: [API.MOVE.FLAGS.MAKES_CONTACT, API.MOVE.FLAGS.CAN_BE_MIRRORED],
	//How the move hits other pokemon, defaults to adjacent (the normal configuration)
	target: API.MOVE.TARGET.ADJACENT,

	//Is ran before even accuracy is checked, can be null. Returns a boolean, if false the move fails
	onTry: null,
	//Is ran right before the attack hits, can be null. Returns a boolean
	onBefore: null,
	//Is ran as the attack hits, aka how the attack is processed. If null, defaults to a standard damage attack
	onAttack: null,
	//Is ran after the attack hits, used for any secondary effects
	onSecondary: null,

	//The "display name" of the move
	name: "Default Move",
	//The description of the move, can be excluded
	description: "There is no description yet for this move.",

	//The message of the move, this depends on what type of move it is
	message: null,
	//If to display "player x used move y!" automatically or not,
	displayUsedMove: true,

},*/