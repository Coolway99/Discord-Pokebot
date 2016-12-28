
FLAGS = API.MOVES.FLAGS;

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

},

]);