

/*function convert(x){
	return JSON.parse(JSON.stringify(x));
}*/

var NewMoves = Java.type("coolway99.discordpokebot.moves.rewrite.NewMoves").INSTANCE;

var API = {
	TYPES: Java.type("coolway99.discordpokebot.states.Types"),
	STATS: Java.type("coolway99.discordpokebot.states.Stats"),
	SUBSTATS: Java.type("coolway99.discordpokebot.states.SubStats"),

	MOVE: {
		CATEGORY: Java.type("coolway99.discordpokebot.moves.MoveType"),
		FLAGS: Java.type("coolway99.discordpokebot.moves.rewrite.MoveFlags"),
		TARGET: Java.type("coolway99.discordpokebot.moves.rewrite.Target"),


	},

	//This can also be thought of the default move:
	createMove: function(){
		return {
			//The type of the move, must be defined
			type: API.TYPES.NORMAL,
			//The power of the move, must be defined
			power: 0,
			//The category of the move, must be defined
			category: API.MOVE.CATEGORY.PHYSICAL,
			//The PP of the move, must be defined
			pp: 0,
			//The accuracy of the move, must be defined
			accuracy: 0,
			//The priority of the move, must be defined
			priority: 0,
			//The cost of the move, must be defined
			cost: 0,
			//Any flags the move has
			flags: [],
			//How the move hits other pokemon, must be defined
			target: API.MOVE.TARGET.ADJACENT,

			onBefore: null,
			onAttack: null,
			onSecondary: null,

			//The internal name/definition of the move, must be defined
			id: "default",
			//The external "display name" of the move, if not defined will equal the id.
			name: "Default Move",
		};
	},

	registerMove: NewMoves.register,

};

delete NewMoves;
Object.freeze(API);