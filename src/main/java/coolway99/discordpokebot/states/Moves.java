package coolway99.discordpokebot.states;

import java.util.ArrayList;
import java.util.Arrays;

import coolway99.discordpokebot.MoveConstants;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.battle.IAttack;
import sx.blah.discord.handle.obj.IChannel;

//Fire punch, Ice punch, Thunder punch, Signal Beam, and Relic song are all the same, except with different effects
public enum Moves{
	//ENUM_NAME(Type, isSpecial", PP, Power, Accuracy (either int or double)
	//hasTarget, hasBefore, hasAfter, cost),
	NULL(Types.NULL, MoveType.PHYSICAL, 0, 0, 0), //TODO perhaps make this Struggle
	ABSORB(Types.GRASS, MoveType.SPECIAL, 25, 20, 100, true, false, 40), //Double it's cost because it heals
	ACID(Types.POISON, MoveType.SPECIAL, 30, 40, 100, false, true, 45), //Has a chance of lowering special defense
	ACID_ARMOR(Types.POISON, MoveType.STATUS, 20, -1, -1, false, true, false, 50), //Increases the user's defense by two stages
	ACID_SPRAY(Types.POISON, MoveType.SPECIAL, 20, 40, 100, false, true, 100), //Lowers special defense by two
	ACROBATICS(Types.FLYING, MoveType.PHYSICAL, 15, 55, 100, true, false, 120), //If the user has no item, the power is doubled
	ACUPRESSURE(Types.NORMAL, MoveType.STATUS, 30, -1, -1, true, 100),
	//TODO AERIAL ACE
	BIND(Types.NORMAL, MoveType.PHYSICAL, 20, 15, 85), //TODO Multiturn
	COMET_PUNCH(Types.NORMAL, MoveType.PHYSICAL, 15, 18, 85, true, false),
	CUT(Types.NORMAL, MoveType.PHYSICAL, 30, 50, 95),
	DOUBLE_KICK(Types.FIGHTING, MoveType.PHYSICAL, 30, 30, 100, true, false), //TODO hits twice
	DOUBLE_SLAP(Types.NORMAL, MoveType.PHYSICAL, 10, 15, 85, true, false), //Same as Comet Punch
	FAIRY_WIND(Types.FAIRY, MoveType.SPECIAL, 30, 40, 100), //Same as scratch
	FIRE_PUNCH(Types.FIRE, MoveType.PHYSICAL, 15, 75, 100, false, true, 80),
	FLY(Types.FLYING, MoveType.PHYSICAL, 15, 90, 95, true, false, 120), //Multiturn, boosted cost because of semiinvul
	ICE_PUNCH(Types.ICE, MoveType.PHYSICAL, 15, 75, 100, false, true, 80),
	GUILLOTINE(Types.NORMAL, MoveType.PHYSICAL, 5, -1, 30, true, false, 50),
	//Affects fly and other moves like that, dealing double damage. +10 cost because of that
	GUST(Types.FLYING, MoveType.SPECIAL, 35, 40, 100, 50), //Same as scratch, affects fly, bounce, etc
	JUMP_KICK(Types.FIGHTING, MoveType.PHYSICAL, 10, 100, 95, true, false), //Has recoil
	KARATE_CHOP(Types.FIGHTING, MoveType.PHYSICAL, 25, 50, 100),
	MEGA_KICK(Types.NORMAL, MoveType.PHYSICAL, 5, 120, 75),
	MEGA_PUNCH(Types.NORMAL, MoveType.PHYSICAL, 20, 80, 85),
	//PAY_DAY(Types.NORMAL, MoveType.Physical, 20, 80, 85), //Same as mega punch, but drops money
	POUND(Types.NORMAL, MoveType.PHYSICAL, 35, 40, 100), //Same as scratch
	POISON_TAIL(Types.POISON, MoveType.PHYSICAL, 25, 50, 100, false, true, 60), //Same as Karate-Chop, Adding +10 cost for poison chance
	RELIC_SONG(Types.NORMAL, MoveType.SPECIAL, 10, 75, 100, false, true, 80), //TODO sleep
	SCRATCH(Types.NORMAL, MoveType.PHYSICAL, 35, 40, 100), //Same as scratch
	SIGNAL_BEAM(Types.BUG, MoveType.SPECIAL, 15, 75, 100, false, true, 80), //TODO confusion
	SLAM(Types.NORMAL, MoveType.PHYSICAL, 20, 80, 75),
	SPLASH(Types.WATER, MoveType.PHYSICAL, 999, 0, 100, true, false, 200), //Fine, you people win
	STEAM_ROLLER(Types.BUG, MoveType.PHYSICAL, 20, 65, 100, true, false), //Same as Stomp
	STOMP(Types.NORMAL, MoveType.PHYSICAL, 20, 65, 100, true, false),
	THUNDER_PUNCH(Types.ELECTRIC, MoveType.PHYSICAL, 15, 75, 100, false, true, 80),
	VICE_GRIP(Types.NORMAL, MoveType.PHYSICAL, 30, 55, 100),
	WATER_GUN(Types.WATER, MoveType.SPECIAL, 25, 40, 100), //Same as scratch
	//WHIRLWIND does not apply
	RAZOR_WIND(Types.NORMAL, MoveType.SPECIAL, 10, 80, 100), //TODO It's a multiturn-attack
	SWORDS_DANCE(Types.NORMAL, MoveType.STATUS, 20, -1, -1, false, true, false, 50), //Status attack
	WING_ATTACK(Types.FLYING, MoveType.SPECIAL, 35, 60, 100),
	VINE_WHIP(Types.GRASS, MoveType.PHYSICAL, 25, 45, 100),
	;
	
	private final Types type;
	private final int power;
	private final MoveType moveType;
	private final int PP; //The default PP of the move
	private final double accuracy; //From 0 to 1
	private final boolean hasTarget;
	private final boolean hasBeforeEffect; //Will code be ran BEFORE attacking?
	private final boolean hasAfterEffect; //Will code be ran AFTER attacking?
	private final int cost; //How many points will this move use?
	
	private Moves(Types type, MoveType moveType, int PP, int power, double accuracy,
			boolean hasTarget, boolean hasBefore, boolean hasAfter, int cost){
		this.type = type;
		this.power = power;
		this.moveType = moveType;
		this.PP = PP;
		this.accuracy = accuracy; //TODO perhaps make it 0-100D
		this.hasTarget = hasTarget;
		this.hasBeforeEffect = hasBefore;
		this.hasAfterEffect = hasAfter;
		this.cost = cost;
	}
	
	private Moves(Types type, MoveType moveType, int PP, int power, int accuracy){
		this(type, moveType, PP, power, accuracy, true);
	}
	
	private Moves(Types type, MoveType moveType, int PP, int power, int accuracy, boolean hasTarget){
		this(type, moveType, PP, power, accuracy, hasTarget, false, false);
	}
	
	private Moves(Types type, MoveType moveType, int PP, int power, int accuracy,
			boolean hasBefore, boolean hasAfter){
		this(type, moveType, PP, power, accuracy, true, hasBefore, hasAfter);
	}
	
	private Moves(Types type, MoveType moveType, int PP, int power, int accuracy,
			boolean hasBefore, boolean hasAfter, int cost){
		this(type, moveType, PP, power, accuracy, true, hasBefore, hasAfter, cost);
	}
	
	private Moves(Types type, MoveType moveType, int PP, int power, int accuracy, boolean hasTarget, int cost){
		this(type, moveType, PP, power, accuracy, hasTarget, false, false, cost);
	}
	
	private Moves(Types type, MoveType moveType, int PP, int power, int accuracy, int cost){
		this(type, moveType, PP, power, accuracy, true, false, false, cost);
	}
	
	private Moves(Types type, MoveType moveType, int PP, int power, int accuracy,
			boolean hasTarget, boolean hasBefore, boolean hasAfter){
		this(type, moveType, PP, power, accuracy, hasTarget, hasBefore, hasAfter, power);
	}
	
	private Moves(Types type, MoveType moveType, int PP, int power, int accuracy,
			boolean hasTarget, boolean hasBefore, boolean hasAfter, int cost){
		this(type, moveType, PP, power, accuracy/100D, hasTarget, hasBefore, hasAfter, cost);
	}
	
	
	public boolean isSpecial(){
		return this.moveType == MoveType.SPECIAL;
	}
	
	public MoveType getMoveType(){
		return this.moveType;
	}
	
	public int getPower(){
		return this.power;
	}
	
	public String getName(){
		return this.toString().replace('_', ' ');
	}
	
	public Types getType(){
		return this.type;
	}
	
	public int getPP(){
		return this.PP;
	}
	
	public int getCost(){
		return this.cost;
	}
	
	public double getAccuracy(){
		return this.accuracy;
	}
	
	public boolean hasBefore(){
		return this.hasBeforeEffect;
	}
	
	public boolean hasAfter(){
		return this.hasAfterEffect;
	}
	
	//Still run the normal battle logic?
	//If this move returns false, you have to manually damage the player then
	//Thankfully, there's still the getDamage() function that only gets the raw damage
	public boolean runBefore(IChannel channel, Player attacker, Player defender){
		switch(this){
			case ABSORB:{
				if(willHit(this, attacker, defender, true)){
					//TODO Bigroot increases restoration
					//TODO Liquid Ooze ability inverts this
					if(!defender.battleEffects.contains(Effects.VBattle.SUBSITUTE)
							&& !attacker.vEffects.contains(Effects.Volatile.HEAL_BLOCK)){
						int damage = getDamage(attacker, this, defender);
						if(damage > defender.HP) damage = defender.HP;
						defender.HP -= damage; //Don't have to check here, we did it above
						attackMessage(channel, attacker, this, defender, damage);
						//If the defender has liquid ooze ability, then the attacker is actually hurt by this amount
						heal(channel, attacker, damage/2); //BigRoot increases this to +80% instead of +50%
					} else {
						failMessage(channel, attacker);
					}
				}
				return false;
			}
			case ACID_ARMOR:{
				StatHandler.raiseStat(channel, attacker, Stats.DEFENSE, true);
				return false;
			}
			case ACROBATICS:{
				if(willHit(this, attacker, defender, true)){
					int damage = getDamage(attacker, this, defender, 110); //TODO power is 110 if the user has no held item
					defender.HP = Math.max(0, defender.HP - damage);
					attackMessage(channel, attacker, this, defender, damage);
				} else {
					missMessage(channel, attacker);
				}
				return false;
			}
			case ACUPRESSURE:{
				if(attacker != defender && defender.battleEffects.contains(Effects.VBattle.SUBSITUTE)){
					failMessage(channel, attacker);
					return false;
				}
				ArrayList<Stats> stats = new ArrayList<>(Arrays.asList(Stats.values()));
				while(!stats.isEmpty()){
					Stats stat = stats.remove(Pokebot.ran.nextInt(stats.size()));
					if(defender.modifiers[stat.getIndex()] < 6){
						StatHandler.raiseStat(channel, defender, stat, true);
						return false;
					}
				}
				failMessage(channel, attacker);
				return false;
			}
			case COMET_PUNCH:
			case DOUBLE_SLAP:{
				//TODO when items are in, each attack can be blocked
				//TODO protect and the stuff
				if(willHit(this, attacker, defender, true)){
					int timesHit = getTimesHit(5, 100, 33.3, 33.3, 16.7, 16.7);
					int damage = getDamage(attacker, this, defender)*timesHit;
					Pokebot.sendBatchableMessage(channel, attacker.user.mention()+" attacked "+defender.user.mention()
							+" "+timesHit+" times for a total of "+damage+"HP of damage!");
					defender.HP = Math.max(0, defender.HP - damage);
				} else {
					missMessage(channel, attacker);
				}
				return false;
			}
			case GUILLOTINE:{
				if(willHit(this, attacker, defender, false)){
					int damage = getDamage(attacker, this, defender, defender.HP);
					attackMessage(channel, attacker, this, defender, damage);
					defender.HP -= damage;
				} else {
					missMessage(channel, attacker);
				}
				return false;
			}
			case SWORDS_DANCE:{
				//Automatic checking is now done on status moves
				/*if(!defender.inBattle()){
					Pokebot.sendBatchableMessage(channel, "But it doesn't work here!");
					return false;
				}*/
				StatHandler.raiseStat(channel, attacker, Stats.ATTACK, true);
				return false;
			}
			case STOMP:
			case STEAM_ROLLER:{
				//TODO flinch
				break;
			}
			case JUMP_KICK:{
				if(willHit(this, attacker, defender, true)){
					return true;
				}
				Pokebot.sendBatchableMessage(channel, attacker.mention()
						+" missed and took crash damage instead!");
				attacker.HP = Math.max(0, attacker.HP - (attacker.getMaxHP()/2));
				return false;
			}
			case FLY:{
				if(attacker.inBattle()){
					//We can assume that both the attacker and defender are in battle, and
					//that it's the same battle
					switch(attacker.lastMovedata){
						case MoveConstants.NOTHING:{
							Pokebot.sendBatchableMessage(channel, attacker.user.mention()+" flew up high!");
							attacker.lastMovedata = MoveConstants.FLYING;
							attacker.battleEffects.add(Effects.VBattle.SEMI_INVULNERABLE);
							return false;
						}
						case MoveConstants.FLYING:{
							attacker.battleEffects.remove(Effects.VBattle.SEMI_INVULNERABLE);
							attacker.lastMovedata = MoveConstants.NOTHING;
							return true;
						}
						default:
							return false;
					}
				}
				return true;
			}
			case SPLASH:{
				Pokebot.sendBatchableMessage(channel, attacker.mention()+" used Splash!... but nothing happened.");
				return false;
			}
			default:
				break;
		}
		return true;
	}
	
	@SuppressWarnings("unused")
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		switch(this){
			case ACID:{
				if(defender.inBattle() && diceRoll(10))
					StatHandler.lowerStat(channel, defender, Stats.SPECIAL_DEFENSE, false);
				break;
			}
			case ACID_SPRAY:{
				//TODO if defense has bulletproof ability this attack does nothing
				if(defender.inBattle())
					StatHandler.lowerStat(channel, defender, Stats.SPECIAL_DEFENSE, true);
				break;
			}
			case FIRE_PUNCH:{
				if(defender.inBattle() && diceRoll(10)){
					if(!isType(defender, Types.FIRE)) defender.nvEffect = Effects.NonVolatile.BURN;
					burn(channel, defender);
				}
				break;
			}
			case ICE_PUNCH:{
				if(defender.inBattle() && diceRoll(10)){
					freeze(channel, defender);
				}
				break;
			}
			case THUNDER_PUNCH:{
				if(defender.inBattle() && diceRoll(10)){
					paralyze(channel, defender);
				}
				break;
			}
			case POISON_TAIL:{
				if(defender.inBattle() && diceRoll(10)){
					poison(channel, defender);
				}
				break;
			}
			default:
				break;
		}
	}
	
	public boolean hasTarget(){
		return this.hasTarget;
	}
	
	//Returns ifDefenderFainted
	public static boolean attack(IChannel channel, Player attacker, Moves move, Player defender){
		if(!attacker.inBattle() && move.getMoveType() == MoveType.STATUS){
			Pokebot.sendBatchableMessage(channel, "But it doesn't work here!");
			return false;
		}
		if(attacker.nvEffect == Effects.NonVolatile.FROZEN){
			//TODO if the attacker uses Fusion Flare, Flame Wheel, Sacred Fire, Flare Blitz
			//TODO or Scald, it will thaw them and/or the opponent out
			if(diceRoll(20)){
				attacker.nvEffect = Effects.NonVolatile.NORMAL;
				Pokebot.sendBatchableMessage(channel, attacker.mention()+" thawed out!");
			} else {
				Pokebot.sendBatchableMessage(channel, attacker.mention()+" is frozen solid!");
				return false;
			}
		}
		if(attacker.nvEffect == Effects.NonVolatile.SLEEP){
			//TODO if the move is snore or sleep talk, then it will work
			if(attacker.counter-- <= 0){
				Pokebot.sendBatchableMessage(channel, attacker.mention()+" woke up!");
			} else {
				Pokebot.sendBatchableMessage(channel, attacker.mention()+" is fast asleep!");
				return false;
			}
		}
		boolean cont = true;
		if(move.hasBefore()){
			cont = move.runBefore(channel, attacker, defender);
		}
		if(cont && willHit(move, attacker, defender, true)){
			//Do battle attack logic
			int damage = getDamage(attacker, move, defender);
			defender.HP = Math.max(0, defender.HP - damage);
			if(move.hasAfter()) move.runAfter(channel, attacker, defender, damage);
			attackMessage(channel, attacker, move, defender, damage);
		} else if(cont){ //we check here again to make sure cont wasn't what made it not run
			missMessage(channel, attacker);
		}
		if(attacker.HP == 0 && !attacker.inBattle()){
			//Checking for things like recoil
			faintMessage(channel, attacker);
		}
		if(defender.HP == 0){
			faintMessage(channel, defender);
			return true;
		}
		Pokebot.sendBatchableMessage(channel, defender.mention()+" has "+defender.HP+"HP left!");
		return false;
	}
	
	public static boolean attack(IChannel channel, IAttack attack){
		return attack(channel, attack.attacker, attack.move, attack.defender);
	}
	
	private static void attackMessage(IChannel channel, Player attacker, Moves move, Player defender, int damage){
		Pokebot.sendBatchableMessage(channel, attacker.mention()
				+" attacked "+defender.mention()+" with "+move.getName()
				+" for "+damage+" damage!");
	}
	
	private static void recoilMessage(IChannel channel, Player attacker){
		Pokebot.sendBatchableMessage(channel, attacker.mention()+" took damage from recoil!");
	}
	
	public static void faintMessage(IChannel channel, Player defender){
		Pokebot.sendBatchableMessage(channel, defender.mention()+" has fainted!");
	}
	
	private static void missMessage(IChannel channel, Player attacker){
		Pokebot.sendBatchableMessage(channel, "But "+attacker.mention()+" missed!");
	}
	
	private static void failMessage(IChannel channel, Player attacker){
		Pokebot.sendBatchableMessage(channel, "But "+attacker.mention()+"'s move failed!");
	}
	//Call AFTER you heal
	private static void heal(IChannel channel, Player attacker, int heal){
		if(heal < 0){
			attack(channel, attacker, -heal);
			return;
		}
		if(heal == 0){
			Pokebot.sendMessage(channel, "But it had no effect!");
			return;
		}
		attacker.HP = Math.min(attacker.getMaxHP(), attacker.HP+heal);
		if(attacker.HP == attacker.getMaxHP()){
			Pokebot.sendBatchableMessage(channel, attacker.mention()+" was fully healed!");
		} else {
			Pokebot.sendBatchableMessage(channel, attacker.mention()+" restored "+heal+"HP!");
		}
	}
	
	private static void attack(IChannel channel, Player defender, int damage){
		if(damage < 0){
			heal(channel, defender, -damage);
			return;
		}
		if(damage == 0){
			Pokebot.sendMessage(channel, "But it had no effect!");
			return;
		}
		defender.HP = Math.max(0, defender.HP - damage);
		Pokebot.sendBatchableMessage(channel, defender.mention()+" hurt themselves for "+damage+" damage!");
	}
	
	private static void burn(IChannel channel, Player defender){
		boolean isImmune = isType(defender, Types.FIRE);
		if(!isImmune){
			defender.nvEffect = Effects.NonVolatile.BURN;
		}
		effectMessage(channel, defender, isImmune, "burns", "burned");
	}
	
	private static void freeze(IChannel channel, Player defender){
		boolean isImmune = isType(defender, Types.ICE);
		if(!isImmune){
			defender.nvEffect = Effects.NonVolatile.FROZEN;
		}
		effectMessage(channel, defender, isImmune, "freezing", "frozen");
	}
	
	private static void paralyze(IChannel channel, Player defender){
		boolean isImmune = isType(defender, Types.ELECTRIC);
		if(!isImmune){
			defender.nvEffect = Effects.NonVolatile.PARALYSIS;
		}
		effectMessage(channel, defender, isImmune, "paralysis", "paralyzed");
	}
	
	private static void poison(IChannel channel, Player defender){
		boolean isImmune = isType(defender, Types.POISON) || isType(defender, Types.STEEL);
		if(!isImmune){
			defender.nvEffect = Effects.NonVolatile.POISON;
		}
		effectMessage(channel, defender, isImmune, "poison", "poisoned");
	}
	
	private static void toxic(IChannel channel, Player defender){
		boolean isImmune = isType(defender, Types.POISON) || isType(defender, Types.STEEL);
		if(!isImmune){
			defender.nvEffect = Effects.NonVolatile.TOXIC;
			defender.counter = 0;
		}
		effectMessage(channel, defender, isImmune, "poison", "badly poisoned");
	}
	
	private static void sleep(IChannel channel, Player defender){
		defender.nvEffect = Effects.NonVolatile.SLEEP;
		defender.counter = Pokebot.ran.nextInt(3)+1;
		
	}
	
	private static void effectMessage(IChannel channel, Player defender, boolean isImmune, String immune, String afflicted){
		if(isImmune){
			Pokebot.sendBatchableMessage(channel, defender.user.mention()+"'s type is immune to "+immune+"!");
		} else {
			Pokebot.sendBatchableMessage(channel, defender.user.mention()+" was "+afflicted+"!");
		}
	}
	
	@SuppressWarnings("unused")
	private static boolean runBattleLogic(Player attacker, Player defender){
		return attacker.inBattle() && defender.inBattle() && attacker.battle == defender.battle;
	}
	
	public static int getDamage(Player attacker, Moves move, Player defender){
		return getDamage(attacker, move, defender, move.getPower());
	}
	
	public static int getDamage(Player attacker, Moves move, Player defender, int power){
		double modifier =
				(isType(attacker, move.getType()) ? 1.5D : 1D) //STAB
				* Types.getTypeMultiplier(move, defender) //Effectiveness
				* getOtherModifiers(move, defender)
				* ((Pokebot.ran.nextInt(100-85)+85) / 100D) //Random chance
				;
		
		double a = ((2*attacker.level) + 10D) / 250D;
		double b;
		if(move.isSpecial()){
			b = attacker.getSpecialAttackStat();
			b /= defender.getSpecialDefenseStat();
		} else {
			b = attacker.getAttackStat();
			b /= defender.getDefenseStat();
		}
		
		return (int) (((a*b*power) + 2)*modifier);
	}
	
	public static int getOtherModifiers(Moves move, Player defender){
		switch(move){
			case GUST:{
				switch(defender.lastMove){
					case FLY:{
						if(defender.lastMovedata == MoveConstants.FLYING) return 2;
						break;
					}
					default:
						break;
				}
				break;
			}
			default:
				break;
		}
		return 1;
	}
	
	//Dice rolls for a hit, if not factoring in changes to accuracy and evasion, you can safely
	//pass in null for Attacker and Defender
	public static boolean willHit(Moves move, Player attacker, Player defender, boolean factorChanges){
		if(attacker.nvEffect == Effects.NonVolatile.PARALYSIS && diceRoll(25)){
			//This should only run in-battle
			Pokebot.sendBatchableMessage(attacker.battle.channel,
					attacker.user.mention()+" is paralyzed! They can't move!");
			return false;
		}
		if(defender.battleEffects.contains(Effects.VBattle.SEMI_INVULNERABLE)){
			switch(move){
				case GUST:{
					switch(defender.lastMove){
						case FLY:{
						//case BOUNCE:{
							return true;
						}
						default:
							return false;
					}
				}
				default:
					return false;
			}
		}
		double accuracy = move.getAccuracy();
		if(factorChanges){
			accuracy *= attacker.getAccuracy() / defender.getEvasion();
		}
		return (Pokebot.ran.nextDouble() < accuracy);
	}
	
	public static boolean isType(Player player, Types type){
		return player.primary == type || player.secondary == type;
	}
	
	public static boolean diceRoll(double chance){
		return Pokebot.ran.nextDouble()*100D <= chance;
	}
	
	public static int getTimesHit(int maxTimes, double...chances){
		int times = 0;
		while(times < maxTimes){
			if(!diceRoll(chances[times])){
				break;
			}
			times++;
		}
		return times;
	}
}
