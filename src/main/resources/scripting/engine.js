

/*function convert(x){
	return JSON.parse(JSON.stringify(x));
}*/

var MoveAPI = Java.type("coolway99.discordpokebot.moves.MoveAPI");
var Pokebot = Java.type("coolway99.discordpokebot.Pokebot");

var API = {
	TYPES: Java.type("coolway99.discordpokebot.states.Types"),
	STATS: Java.type("coolway99.discordpokebot.states.Stats"),
	SUBSTATS: Java.type("coolway99.discordpokebot.states.SubStats"),
	EFFECTS:{
		NONVOLATILE: Java.type("coolway99.discordpokebot.states.Effects.NonVolatile"),
		VOLATILE: Java.type("coolway99.discordpokebot.states.Effects.Volatile"),
		VBATTLE: Java.type("coolway99.discordpokebot.states.Effects.VBattle"),
	},

	MOVES: {
		CATEGORY: Java.type("coolway99.discordpokebot.moves.MoveCategory"),
		FLAGS: Java.type("coolway99.discordpokebot.moves.MoveFlags"),
		TARGET: Java.type("coolway99.discordpokebot.moves.Target"),
		UTILS: Java.type("coolway99.discordpokebot.moves.MoveUtils"),

		standardMultiHit: function(context, attacker, defender){
			var hits = API.MOVES.UTILS.getTimesHit(1, 1/3, 1/3, 1/6, 1/6);
			var damage = 0;
			for(var x = 0; x < hits; x++){
				//Even inside functions, using "this" in a move context will return the move (as a MoveWrapper) itself
				damage += API.MOVES.UTILS.dealDamage(attacker, this, defender);
			}
			API.MESSAGES.multiHit(context.channel, defender, hits, damage);
			if(defender.has(API.EFFECTS.NONVOLATILE.FAINTED)){
				API.MESSAGES.fainted(context.channel, defender);
			}
		},
	},

	RANDOM: Pokebot.ran,
	MESSAGES: Java.type("coolway99.discordpokebot.Messages"),


	diceRoll: Pokebot.diceRoll,
	sendMessage: Pokebot.sendMessage,

	registerMove: MoveAPI.registerMove,
	registerMoves: MoveAPI.registerMoves,

};

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

	//Is ran before the attack hits, can be null. Returns a boolean
	onBefore: null,
	//Is ran as the attack hits, aka how the attack is processed. If null, defaults to a standard damage attack
	onAttack: null,
	//Is ran after the attack hits, used for any secondary effects
	onSecondary: null,

	//The internal name/definition of the move, must be defined
	id: "default",
	//The external "display name" of the move, if not defined will equal the id which may be undesirable.
	name: "Default Move",
},*/