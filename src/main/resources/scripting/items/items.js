API.ITEMS.register([
{
	name: "None",
	description: "You have no item.",
	cost: 0,
},
{
	name: "Abomasite",
	description: "Seemingly does nothing...",
	cost: 0,
},
{
	name: "Absolite",
	description: "Seemingly does nothing...",
	cost: 0,
},
{
	name: "Absorb Bulb",
	description: "Raises holder's Sp. Atk by 1 stage if hit by a Water-type attack. Single use.",
	cost: 25,
	onFling: function(channel, damageCalc){
		damageCalc.power = 30;
	},
	onDamageModifier: function(channel, damageCalc){
		if(damageCalc.type == API.TYPES.WATER && attacker.useItem(this)){
			damageCalc.attacker.modifyStat(channel, API.STATS.SPECIAL_ATTACK, 1);
			damageCalc.attacker.usedItem();
		}
	},
},
{
	name: "Adamant Orb",
	description: "A worthless orb that may have power someday. Good for flinging at people.",
	cost: 30,
	onFling: function(channel, damageCalc){
		damageCalc.power = 60;
	},
	/*onBasePowerPriority: 6,
	onBasePower: function (basePower, user, target, move) {
		if (move && user.baseTemplate.species === 'Dialga' && (move.type === 'Steel' || move.type === 'Dragon')) {
			return this.chainModify([0x1333, 0x1000]);
		}
	},*/
	//description: "If holder is a Dialga, its Steel- and Dragon-type attacks have 1.2x power.",
},
{
	name: "Aerodactylite",
		description: "Seemingly does nothing...",
	cost: 0,
},
{
	name: "Aggronite",
	description: "Seemingly does nothing...",
	cost: 0,
},
{
	name: "Aguav Berry",
	description: "When eaten restores 1/8 max HP when at 1/2 max HP or less. May confuse.",
	cost: 50,
	isBerry: true,
	onNaturalGift: function(channel, damageCalc){
		damageCalc.power = 80;
		damageCalc.type = API.TYPES.DRAGON;
	},
	onHit: function(channel, attacker, move, defender){
		this.onAfterEffect(channel, defender);
	},
	onAfterEffect: function(channel, player){
		if(player.HP <= player.getMaxHP / 2){
			player.heal(channel, player.getMaxHP / 8);
			if(player.nature.decrease == API.STATS.SPECIAL_DEFENSE){
				API.MOVES.UTILS.confuse(channel, player);
			}
			player.usedItem();
		}
	},
},/*
"airballoon": {
	id: "airballoon",
	name: "Air Balloon",
	spritenum: 6,
	fling: {
		basePower: 10,
	},
	onStart: function (target) {
		if (!target.ignoringItem() && !this.getPseudoWeather('gravity')) {
			this.add('-item', target, 'Air Balloon');
		}
	},
	// airborneness implemented in battle-engine.js:BattlePokemon#isGrounded
	onAfterDamage: function (damage, target, source, effect) {
		this.debug('effect: ' + effect.id);
		if (effect.effectType === 'Move' && effect.id !== 'confused') {
			this.add('-enditem', target, 'Air Balloon');
			target.item = '';
			this.itemData = {id: '', target: this};
			this.runEvent('AfterUseItem', target, null, null, 'airballoon');
		}
	},
	onAfterSubDamage: function (damage, target, source, effect) {
		this.debug('effect: ' + effect.id);
		if (effect.effectType === 'Move' && effect.id !== 'confused') {
			this.add('-enditem', target, 'Air Balloon');
			target.setItem('');
		}
	},
	num: 541,
	gen: 5,
	description: "Holder is immune to Ground-type attacks. Pops when holder is hit.",
},
"alakazite": {
	id: "alakazite",
	name: "Alakazite",
	spritenum: 579,
	megaStone: "Alakazam-Mega",
	megaEvolves: "Alakazam",
	onTakeItem: function (item, source) {
		if (item.megaEvolves === source.baseTemplate.baseSpecies) return false;
		return true;
	},
	num: 679,
	gen: 6,
	description: "If holder is an Alakazam, this item allows it to Mega Evolve in battle.",
},
"altarianite": {
	id: "altarianite",
	name: "Altarianite",
	spritenum: 615,
	megaStone: "Altaria-Mega",
	megaEvolves: "Altaria",
	onTakeItem: function (item, source) {
		if (item.megaEvolves === source.baseTemplate.baseSpecies) return false;
		return true;
	},
	num: 755,
	gen: 6,
	description: "If holder is an Altaria, this item allows it to Mega Evolve in battle.",
},
"ampharosite": {
	id: "ampharosite",
	name: "Ampharosite",
	spritenum: 580,
	megaStone: "Ampharos-Mega",
	megaEvolves: "Ampharos",
	onTakeItem: function (item, source) {
		if (item.megaEvolves === source.baseTemplate.baseSpecies) return false;
		return true;
	},
	num: 658,
	gen: 6,
	description: "If holder is an Ampharos, this item allows it to Mega Evolve in battle.",
},
"apicotberry": {
	id: "apicotberry",
	name: "Apicot Berry",
	spritenum: 10,
	isBerry: true,
	naturalGift: {
		basePower: 100,
		type: "Ground",
	},
	onUpdate: function (pokemon) {
		if (pokemon.hp <= pokemon.maxhp / 4 || (pokemon.hp <= pokemon.maxhp / 2 && pokemon.hasAbility('gluttony'))) {
			pokemon.eatItem();
		}
	},
	onEat: function (pokemon) {
		this.boost({spd:1});
	},
	num: 205,
	gen: 3,
	description: "Raises holder's Sp. Def by 1 stage when at 1/4 max HP or less. Single use.",
},
"armorfossil": {
	id: "armorfossil",
	name: "Armor Fossil",
	spritenum: 12,
	fling: {
		basePower: 100,
	},
	num: 104,
	gen: 4,
	description: "Can be revived into Shieldon.",
},
"aspearberry": {
	id: "aspearberry",
	name: "Aspear Berry",
	spritenum: 13,
	isBerry: true,
	naturalGift: {
		basePower: 80,
		type: "Ice",
	},
	onUpdate: function (pokemon) {
		if (pokemon.status === 'frz') {
			pokemon.eatItem();
		}
	},
	onEat: function (pokemon) {
		if (pokemon.status === 'frz') {
			pokemon.cureStatus();
		}
	},
	num: 153,
	gen: 3,
	description: "Holder is cured if it is frozen. Single use.",
},
"assaultvest": {
	id: "assaultvest",
	name: "Assault Vest",
	spritenum: 581,
	fling: {
		basePower: 80,
	},
	onModifySpDPriority: 1,
	onModifySpD: function (spd) {
		return this.chainModify(1.5);
	},
	onDisableMove: function (pokemon) {
		let moves = pokemon.moveset;
		for (let i = 0; i < moves.length; i++) {
			if (this.getMove(moves[i].move).category === 'Status') {
				pokemon.disableMove(moves[i].id);
			}
		}
	},
	num: 640,
	gen: 6,
	description: "Holder's Sp. Def is 1.5x, but it can only select damaging moves.",
},
"audinite": {
	id: "audinite",
	name: "Audinite",
	spritenum: 617,
	megaStone: "Audino-Mega",
	megaEvolves: "Audino",
	onTakeItem: function (item, source) {
		if (item.megaEvolves === source.baseTemplate.baseSpecies) return false;
		return true;
	},
	num: 757,
	gen: 6,
	description: "If holder is an Audino, this item allows it to Mega Evolve in battle.",
},
"babiriberry": {
	id: "babiriberry",
	name: "Babiri Berry",
	spritenum: 17,
	isBerry: true,
	naturalGift: {
		basePower: 80,
		type: "Steel",
	},
	onSourceModifyDamage: function (damage, source, target, move) {
		if (move.type === 'Steel' && move.typeMod > 0 && (!target.volatiles['substitute'] || move.flags['authentic'] || (move.infiltrates && this.gen >= 6))) {
			if (target.eatItem()) {
				this.debug('-50% reduction');
				this.add('-enditem', target, this.effect, '[weaken]');
				return this.chainModify(0.5);
			}
		}
	},
	onEat: function () { },
	num: 199,
	gen: 4,
	description: "Halves damage taken from a supereffective Steel-type attack. Single use.",
},
"banettite": {
	id: "banettite",
	name: "Banettite",
	spritenum: 582,
	megaStone: "Banette-Mega",
	megaEvolves: "Banette",
	onTakeItem: function (item, source) {
		if (item.megaEvolves === source.baseTemplate.baseSpecies) return false;
		return true;
	},
	num: 668,
	gen: 6,
	description: "If holder is a Banette, this item allows it to Mega Evolve in battle.",
},
"beedrillite": {
	id: "beedrillite",
	name: "Beedrillite",
	spritenum: 628,
	megaStone: "Beedrill-Mega",
	megaEvolves: "Beedrill",
	onTakeItem: function (item, source) {
		if (item.megaEvolves === source.baseTemplate.baseSpecies) return false;
		return true;
	},
	num: 770,
	gen: 6,
	description: "If holder is a Beedrill, this item allows it to Mega Evolve in battle.",
},
"belueberry": {
	id: "belueberry",
	name: "Belue Berry",
	spritenum: 21,
	isBerry: true,
	naturalGift: {
		basePower: 100,
		type: "Electric",
	},
	onEat: false,
	num: 183,
	gen: 3,
	description: "Cannot be eaten by the holder. No effect when eaten with Bug Bite or Pluck.",
},
"berryjuice": {
	id: "berryjuice",
	name: "Berry Juice",
	spritenum: 22,
	fling: {
		basePower: 30,
	},
	onUpdate: function (pokemon) {
		if (pokemon.hp <= pokemon.maxhp / 2) {
			if (this.runEvent('TryHeal', pokemon) && pokemon.useItem()) {
				this.heal(20);
			}
		}
	},
	num: 43,
	gen: 2,
	description: "Restores 20 HP when at 1/2 max HP or less. Single use.",
},
"bigroot": {
	id: "bigroot",
	name: "Big Root",
	spritenum: 29,
	fling: {
		basePower: 10,
	},
	onTryHealPriority: 1,
	onTryHeal: function (damage, target, source, effect) {
		let heals = {drain: 1, leechseed: 1, ingrain: 1, aquaring: 1};
		if (heals[effect.id]) {
			return Math.ceil((damage * 1.3) - 0.5); // Big Root rounds half down
		}
	},
	num: 296,
	gen: 4,
	description: "Holder gains 1.3x HP from draining moves, Aqua Ring, Ingrain, and Leech Seed.",
},
"bindingband": {
	id: "bindingband",
	name: "Binding Band",
	spritenum: 31,
	fling: {
		basePower: 30,
	},
	// implemented in statuses
	num: 544,
	gen: 5,
	description: "Holder's partial-trapping moves deal 1/6 max HP per turn instead of 1/8.",
},
"blackbelt": {
	id: "blackbelt",
	name: "Black Belt",
	spritenum: 32,
	fling: {
		basePower: 30,
	},
	onBasePowerPriority: 6,
	onBasePower: function (basePower, user, target, move) {
		if (move && move.type === 'Fighting') {
			return this.chainModify([0x1333, 0x1000]);
		}
	},
	num: 241,
	gen: 2,
	description: "Holder's Fighting-type attacks have 1.2x power.",
},
"blacksludge": {
	id: "blacksludge",
	name: "Black Sludge",
	spritenum: 34,
	fling: {
		basePower: 30,
	},
	onResidualOrder: 5,
	onResidualSubOrder: 2,
	onResidual: function (pokemon) {
		if (pokemon.hasType('Poison')) {
			this.heal(pokemon.maxhp / 16);
		} else {
			this.damage(pokemon.maxhp / 8);
		}
	},
	num: 281,
	gen: 4,
	description: "Each turn, if holder is a Poison type, restores 1/16 max HP; loses 1/8 if not.",
},
"blackglasses": {
	id: "blackglasses",
	name: "Black Glasses",
	spritenum: 35,
	fling: {
		basePower: 30,
	},
	onBasePowerPriority: 6,
	onBasePower: function (basePower, user, target, move) {
		if (move && move.type === 'Dark') {
			return this.chainModify([0x1333, 0x1000]);
		}
	},
	num: 240,
	gen: 2,
	description: "Holder's Dark-type attacks have 1.2x power.",
},
"blastoisinite": {
	id: "blastoisinite",
	name: "Blastoisinite",
	spritenum: 583,
	megaStone: "Blastoise-Mega",
	megaEvolves: "Blastoise",
	onTakeItem: function (item, source) {
		if (item.megaEvolves === source.baseTemplate.baseSpecies) return false;
		return true;
	},
	num: 661,
	gen: 6,
	description: "If holder is a Blastoise, this item allows it to Mega Evolve in battle.",
},
"blazikenite": {
	id: "blazikenite",
	name: "Blazikenite",
	spritenum: 584,
	megaStone: "Blaziken-Mega",
	megaEvolves: "Blaziken",
	onTakeItem: function (item, source) {
		if (item.megaEvolves === source.baseTemplate.baseSpecies) return false;
		return true;
	},
	num: 664,
	gen: 6,
	description: "If holder is a Blaziken, this item allows it to Mega Evolve in battle.",
},
"blueorb": {
	id: "blueorb",
	name: "Blue Orb",
	spritenum: 41,
	onSwitchIn: function (pokemon) {
		if (pokemon.isActive && pokemon.baseTemplate.species === 'Kyogre') {
			this.insertQueue({pokemon: pokemon, choice: 'runPrimal'});
		}
	},
	onPrimal: function (pokemon) {
		let template = this.getTemplate('Kyogre-Primal');
		pokemon.formeChange(template);
		pokemon.baseTemplate = template;
		pokemon.details = template.species + (pokemon.level === 100 ? '' : ', L' + pokemon.level) + (pokemon.gender === '' ? '' : ', ' + pokemon.gender) + (pokemon.set.shiny ? ', shiny' : '');
		if (pokemon.illusion) {
			pokemon.ability = ''; // Don't allow Illusion to wear off
			this.add('-primal', pokemon.illusion);
		} else {
			this.add('detailschange', pokemon, pokemon.details);
			this.add('-primal', pokemon);
		}
		pokemon.setAbility(template.abilities['0']);
		pokemon.baseAbility = pokemon.ability;
	},
	onTakeItem: function (item, source) {
		if (source.baseTemplate.baseSpecies === 'Kyogre') return false;
		return true;
	},
	num: 535,
	gen: 6,
	description: "If holder is a Kyogre, this item triggers its Primal Reversion in battle.",
},
"blukberry": {
	id: "blukberry",
	name: "Bluk Berry",
	spritenum: 44,
	isBerry: true,
	naturalGift: {
		basePower: 90,
		type: "Fire",
	},
	onEat: false,
	num: 165,
	gen: 3,
	description: "Cannot be eaten by the holder. No effect when eaten with Bug Bite or Pluck.",
},
"brightpowder": {
	id: "brightpowder",
	name: "BrightPowder",
	spritenum: 51,
	fling: {
		basePower: 10,
	},
	onModifyAccuracy: function (accuracy) {
		if (typeof accuracy !== 'number') return;
		this.debug('brightpowder - decreasing accuracy');
		return accuracy * 0.9;
	},
	num: 213,
	gen: 2,
	description: "The accuracy of attacks against the holder is 0.9x.",
},
"buggem": {
	id: "buggem",
	name: "Bug Gem",
	isUnreleased: true,
	spritenum: 53,
	isGem: true,
	onSourceTryPrimaryHit: function (target, source, move) {
		if (target === source || move.category === 'Status') return;
		if (move.type === 'Bug') {
			if (source.useItem()) {
				this.add('-enditem', source, 'Bug Gem', '[from] gem', '[move] ' + move.name);
				source.addVolatile('gem');
			}
		}
	},
	num: 558,
	gen: 5,
	description: "Holder's first successful Bug-type attack will have 1.3x power. Single use.",
},*/
]);