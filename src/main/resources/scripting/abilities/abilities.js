API.ABILITIES.register([
{
	name: "None",
	description: "Nothing special here",
	cost: 0,
},
{
	name: "Adaptability",
	description: "Powers up moves of the same type as the player. 2x STAB bonus instead of 1.5x",
	cost: 175,
	onDamageModifier: function(channel, damageCalc){
		damageCalc.STAB = 2;
	},
},
{
	name: "Aftermath",
	description: "If this player is knocked out with a contact move, that move's user loses 1/4 of its maximum HP, rounded down. If any active Pokemon has the Ability Damp, this effect is prevented.",
	cost: 125,
	onFaint: function(channel, player){
		if(player.lastAttacker != null && player != player.lastAttacker){
			var attacker = player.lastAttacker;
			if(!attacker.has(API.EFFECTS.NONVOLATILE.FAINTED) &&
					attacker.doesLastMoveHave(API.MOVES.FLAGS.MAKES_CONTACT)){
				API.sendMessage(channel, attacker.mention()+" took damage due to "+player.mention()+"'s aftermath!");
				attacker.damage(channel, attacker.getMaxHP()/4);
			}
			delete attacker;
		}
	},
},
{
	name: "Aerilate",
	description: "Normal-type moves become Flying type and have 1.3x power.",
	onDamageModifier: function(channel, damageCalc){
		if(damageCalc.type == API.TYPES.NORMAL){
			damageCalc.power *= 1.3;
			damageCalc.type = API.TYPES.FLYING;
		}
	},
	cost: 200,
},
{
	name: "Air Lock",
	description: "While active, the effects of weather conditions are disabled.",
	onBegin: function(channel, player){
	//TODO this glitches up with more than one cloud-nine/airlock
		if(player.battle != null){
			player.battle.set(API.EFFECTS.BATTLE.CLOUD_NINE, -1);
			API.sendMessage(channel, player.mention()+"'s Air Lock canceled all weather!");
		}
	},
	onEnd: function(channel, player){
		if(player.battle != null){
			player.battle.clear(API.EFFECTS.BATTLE.CLOUD_NINE);
			API.sendMessage(channel, player.mention()+"'s Air Lock faded away...");
		}
	},
	cost: 150,
},
{
	name: "Analytic",
	description: "Move power is multiplied by 1.3 if it is the last move in a turn. Does not affect Doom Desire and Future Sight.",
	onDamageModifier: function(channel, damageCalc){
		var attacker = damageCalc.attacker;
		if(attacker.battle != null){
			var battle = attacker.battle;
			if(battle.attacks.last().attacker == attacker){
				damageCalc.power *= 1.3;
			}
		}
	},
	cost: 100,
},
]);