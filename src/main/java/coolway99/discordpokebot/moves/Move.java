package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Messages;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.battle.IAttack;
import coolway99.discordpokebot.moves.rewrite.MoveUtils;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.TreeMap;

//Fire punch, Ice punch, Thunder punch, Signal Beam, and Relic song are all the same, except with different effects
//Any accuracy over 100 or over 1D will always hit, and will automatically have evasion and accuracy excluded
//For multi-hit moves, refer to PMD if needed
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public abstract class Move{

	public static final TreeMap<String, Move> REGISTRY = new TreeMap<>(String::compareToIgnoreCase);

	/*
	TODO Ally Switch can't be used here
	TODO Aromatic Mist can't apply just yet TODO Team Battles
	TODO Assist randomly uses an ally's move TODO Team Battles
	TODO Assurance deals double damage if the target has already taken damage that turn
	TODO Attract needs genders
	TODO Atomonize lowers user's weight
	TODO Baton Pass needs party pokemon
	TODO BEAT_UP(Types.DARK, MoveType.PHYSICAL, 10, -1, 100, Flags.NO_CONTACT) needs party pokemon
	TODO BELCH(Types.POISON, MoveType.SPECIAL, 10, 120, 90)
	TODO Bestow
	TODO Bide
	TODO This is going to be a bit tricky to implement. Look at BIDE
	TODO BIND(Types.NORMAL, MoveType.PHYSICAL, 20, 15, 85), //TODO Multiturn
	TODO Block
	TODO Blizzard
	TODO WHIRLWIND does not apply
	*/

	protected final Types type;
	protected final int power;
	protected final MoveType moveType;
	protected final int PP; //The default PP of the move
	protected final double accuracy; //From 0 to 1
	protected final Battle_Priority priority;
	protected final int cost; //How many points will this move use?
	protected final EnumSet<Flags> flags;

	protected String name;
	protected String displayName;

	public Move(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Battle_Priority priority,
		 Flags... flags){
		this.type = type;
		this.power = power;
		this.moveType = moveType;
		this.PP = PP;
		this.accuracy = accuracy/100D; //TODO perhaps make it 0-100D
		this.priority = priority;
		this.cost = cost;
		if(flags.length <= 0){
			this.flags = EnumSet.noneOf(Flags.class);
		} else {
			this.flags = EnumSet.copyOf(Arrays.asList(flags));
		}

		switch(moveType){
			case PHYSICAL:{
				if(!this.flags.contains(Flags.NO_CONTACT)) this.flags.add(Flags.CONTACT);
				break;
			}
			case SPECIAL:
			default:{
					if(!this.flags.contains(Flags.CONTACT)) this.flags.add(Flags.NO_CONTACT);
				break;
			}
		}
	}

	public Move(Types type, MoveType moveType, int PP, int power, int accuracy, int cost, Flags... flags){
		this(type, moveType, PP, power, accuracy, cost, Battle_Priority.P0, flags);
	}

	public Move(Types type, MoveType moveType, int PP, int power, int accuracy, Flags... flags){
		this(type, moveType, PP, power, accuracy, power, flags);
	}

	public String getName(){
		return this.name;
	}

	public String getDisplayName(){
		return this.displayName;
	}

	@SuppressWarnings("MethodOnlyUsedFromInnerClass")
	private void setName(String name){
		this.name = name;
	}

	@SuppressWarnings("MethodOnlyUsedFromInnerClass")
	private void setDisplayName(String name){
		this.displayName = name;
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

	public Types getType(Player attacker){
		return this.getType(attacker.getModifiedAbility());
	}

	public Types getType(Abilities ability){
		switch(ability){
			case NORMALIZE: return Types.NORMAL;
			case AERILATE: return Types.FLYING;

			default:
				break;
		}
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

	public int getPriority(){
		return this.priority.getPriority();
	}

	public boolean has(Flags flag){
		return this.flags.contains(flag);
	}



	//Still run the normal battle logic?
	//If this move returns false, you have to manually damage the player then
	//Thankfully, there's still the getDamage() function that only gets the raw damage
	public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
		return BeforeResult.CONTINUE;
	}

	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){}
	@SuppressWarnings("unused")
	/*public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		if(!defender.inBattle()) return; //So far, any move that has after-effects needs a battle
			case BLIZZARD:
				if(diceRoll(10)){
					freeze(channel, defender);
				}
				break;
			}
		}
	}*/
/*
	public BeforeResult runMultiturn(IChannel channel, Player attacker, Player defender){
			//TODO Bind
			/*case BIND:{
				if(attacker.lastMoveData == MoveConstants.NOTHING){
					//TODO Rapid spin
					//TODO Grip claw
					//TODO Binding band
				}
			}*//*
		}
	}*/

	protected int getAdjustedDamage(Player attacker, Player defender){
		return 0;
	}

	public static void registerMoves(){
		System.out.println("Registering moves...");

		REGISTRY.put("ARM_THRUST", new MultiHitMove(Types.FIGHTING, MoveType.PHYSICAL, 20, 15, 100, 50));
		REGISTRY.put("BARRAGE", new MultiHitMove(Types.NORMAL, MoveType.PHYSICAL, 20, 15, 85, 50, Flags.NO_CONTACT,
				Flags.BALLBASED));
		REGISTRY.put("COMET_PUNCH", new MultiHitMove(Types.NORMAL, MoveType.PHYSICAL, 15, 18, 85, 60));
		REGISTRY.put("DOUBLE_SLAP", new MultiHitMove(Types.NORMAL, MoveType.PHYSICAL, 10, 15, 85, 50));

		//REGISTRY.put("DOUBLE_KICK", new MultiHitMove(Types.FIGHTING, MoveType.PHYSICAL, 30, 30, 100, 2)); //Hits twice
		REGISTRY.put("DOUBLE_KICK", new Move(Types.FIGHTING, MoveType.PHYSICAL, 30, 30, 100, 60){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				if(willHit(this, attacker, defender, true)){
					int damage = 0;
					for(int x = 0; x < 2; x++){
						damage += getDamage(attacker, this, defender);
					}
					Pokebot.sendMessage(channel, attacker.mention()+" attacked "+defender.mention()
							+" 2 times for a total of "+damage+"HP of damage!");
					defender.HP = Math.max(0, defender.HP-damage);
				} else {
					Messages.miss(channel, attacker);
				}
				return BeforeResult.STOP;
			}
		});

		REGISTRY.put("ACID_ARMOR", new StatusChange(Types.POISON, 20, Stats.DEFENSE, 2, Flags.UNTARGETABLE));
		REGISTRY.put("AGILITY", new StatusChange(Types.PSYCHIC, 30, Stats.SPEED, 2, Flags.UNTARGETABLE));
		REGISTRY.put("AMNESIA", new StatusChange(Types.PSYCHIC, 20, Stats.SPECIAL_DEFENSE, 2, Flags.UNTARGETABLE));
		REGISTRY.put("AUTOTOMIZE", new StatusChange(Types.STEEL, 15, 50, Stats.SPEED, 2, Flags.UNTARGETABLE));//TODO lowers weight
		REGISTRY.put("BARRIER", new StatusChange(Types.PSYCHIC, 20, Stats.DEFENSE, 2, Flags.UNTARGETABLE));
		REGISTRY.put("BABY_DOLL_EYES", new StatusChange(Types.FAIRY, 30, 100, 25, Stats.ATTACK, -1, Battle_Priority.P1));
		REGISTRY.put("SWORDS_DANCE", new StatusChange(Types.NORMAL, 20, Stats.ATTACK, 2, Flags.UNTARGETABLE));
		REGISTRY.put("BELLY_DRUM", new StatusChange(Types.NORMAL, 10, 150, Stats.ATTACK, 12, Flags.UNTARGETABLE){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				if(attacker.HP <= Math.floor(attacker.getMaxHP()/2F)){
					Messages.fail(channel, attacker);
					return BeforeResult.STOP;
				}
				attacker.HP -= (int) Math.floor(attacker.getMaxHP()/2F);
				return super.runBefore(channel, attacker, defender);
			}
		});

		REGISTRY.put("ACID", new StatChangeDamageMove(Types.POISON, MoveType.SPECIAL, 30, 40, 100, 10, Stats.SPECIAL_DEFENSE,
				-1, StatChangeDamageMove.Who.DEFENDER));
		REGISTRY.put("ACID_SPRAY", new StatChangeDamageMove(Types.POISON, MoveType.SPECIAL, 20, 40, 100, 100, 100, Stats
				.SPECIAL_DEFENSE, -2, StatChangeDamageMove.Who.DEFENDER, Flags.BALLBASED));
		REGISTRY.put("AURORA_BEAM", new StatChangeDamageMove(Types.ICE, MoveType.SPECIAL, 20, 65, 100, 75, 10, Stats.ATTACK,
				-1, StatChangeDamageMove.Who.DEFENDER));

		REGISTRY.put("AIR_SLASH", new FlinchDamageMove(Types.FLYING, MoveType.SPECIAL, 15, 75, 95, 90, 30));
		REGISTRY.put("ASTONISH", new FlinchDamageMove(Types.GHOST, MoveType.PHYSICAL, 15, 30, 100, 45, 30));
		REGISTRY.put("BITE", new FlinchDamageMove(Types.DARK, MoveType.PHYSICAL, 25, 60, 100, 75, 30));
		REGISTRY.put("BONE_CLUB", new FlinchDamageMove(Types.GROUND, MoveType.PHYSICAL, 20, 65, 85, 75, 10, Flags.NO_CONTACT));

		REGISTRY.put("BLAZE_KICK", new AilmentDamageMove(Types.FIRE, MoveType.PHYSICAL, 10, 85, 90, 90, 10, MoveUtils::burn));

		REGISTRY.put("FIRE_PUNCH", new AilmentDamageMove(Types.FIRE, MoveType.PHYSICAL, 15, 75, 100, 80, 10, MoveUtils::burn));
		REGISTRY.put("ICE_PUNCH", new AilmentDamageMove(Types.ICE, MoveType.PHYSICAL, 15, 75, 100, 80, 10, MoveUtils::freeze));
		REGISTRY.put("THUNDER_PUNCH", new AilmentDamageMove(Types.ELECTRIC, MoveType.PHYSICAL, 15, 75, 100, 80, 10,
				MoveUtils::paralyze));
		//TODO CONFUSION REGISTRY.put("SIGNAL_BEAM", new AilmentDamageMove(Types.BUG, MoveType.SPECIAL, 15, 75, 100, 80, 10, ));
		REGISTRY.put("RELIC_SONG", new AilmentDamageMove(Types.NORMAL, MoveType.SPECIAL, 10, 75, 100, 80, 10, MoveUtils::sleep));

		REGISTRY.put("KARATE_CHOP", new DamageMove(Types.FIGHTING, MoveType.PHYSICAL, 25, 50, 100));
		REGISTRY.put("POISON_TAIL", new AilmentDamageMove(Types.POISON, MoveType.PHYSICAL, 25, 50, 100, 55, 10, MoveUtils::poison));

		//One of the signature moves of Reshiram, boosts fusion bolt
		REGISTRY.put("BLUE_FLARE", new AilmentDamageMove(Types.FIRE, MoveType.SPECIAL, 5, 130, 85, 140, 20, MoveUtils::burn));
		//One of the signature moves of Zekrom, boosts fusion flare
		REGISTRY.put("BOLT_STRIKE", new AilmentDamageMove(Types.ELECTRIC, MoveType.PHYSICAL, 5, 130, 85, 95, 20, MoveUtils::paralyze));

		//Lugia's signature move, only benefit is higher critical
		REGISTRY.put("AEROBLAST", new DamageMove(Types.FLYING, MoveType.SPECIAL, 5, 100, 95));
		REGISTRY.put("AIR_CUTTER", new DamageMove(Types.FLYING, MoveType.SPECIAL, 25, 60, 95));
		REGISTRY.put("ATTACK_ORDER", new DamageMove(Types.BUG, MoveType.PHYSICAL, 15, 90, 100, 100, Flags.NO_CONTACT));
		REGISTRY.put("AQUA_JET", new DamageMove(Types.WATER, MoveType.PHYSICAL, 20, 40, 100, 60, Battle_Priority.P1));
		REGISTRY.put("CUT", new DamageMove(Types.NORMAL, MoveType.PHYSICAL, 30, 50, 95));
		REGISTRY.put("FAIRY_WIND", new DamageMove(Types.FAIRY, MoveType.SPECIAL, 30, 40, 100));
		REGISTRY.put("MEGA_KICK", new DamageMove(Types.NORMAL, MoveType.PHYSICAL, 5, 120, 75));
		REGISTRY.put("MEGA_PUNCH", new DamageMove(Types.NORMAL, MoveType.PHYSICAL, 20, 80, 85));
		REGISTRY.put("PAY_DAY", new DamageMove(Types.NORMAL, MoveType.PHYSICAL, 20, 80, 85));
		REGISTRY.put("SLAM", new DamageMove(Types.NORMAL, MoveType.PHYSICAL, 20, 80, 75));
		REGISTRY.put("WATER_GUN", new DamageMove(Types.WATER, MoveType.SPECIAL, 25, 40, 100));
		REGISTRY.put("WING_ATTACK", new DamageMove(Types.FLYING, MoveType.SPECIAL, 35, 60, 100));
		REGISTRY.put("VICE_GRIP", new DamageMove(Types.NORMAL, MoveType.PHYSICAL, 30, 55, 100));
		REGISTRY.put("VINE_WHIP", new DamageMove(Types.GRASS, MoveType.PHYSICAL, 25, 45, 100));

		REGISTRY.put("POUND", new DamageMove(Types.NORMAL, MoveType.PHYSICAL, 35, 40, 100));
		REGISTRY.put("SCRATCH", new DamageMove(Types.NORMAL, MoveType.PHYSICAL, 35, 40, 100));
		//Affects fly and other moves like that, dealing double damage. +10 cost because of that
		REGISTRY.put("GUST", new DamageMove(Types.FLYING, MoveType.SPECIAL, 35, 40, 100, 50));

		REGISTRY.put("AERIAL_ACE", new DamageMove(Types.FLYING, MoveType.PHYSICAL, 20, 60, -1, 80, Flags.ALWAYS_HIT));
		REGISTRY.put("AURA_SPHERE", new DamageMove(Types.FIGHTING, MoveType.SPECIAL, 20, 80, -1, 100,
				Flags.BALLBASED, Flags.ALWAYS_HIT));

		REGISTRY.put("BLAST_BURN", new HarshRechargeMove(Types.FIRE, MoveType.SPECIAL, 4, 150, 90));

		REGISTRY.put("BODY_SLAM", new AilmentMinimizeMove(Types.NORMAL, MoveType.PHYSICAL, 15, 85, 100, 100, 30, MoveUtils::paralyze));
		REGISTRY.put("STEAMROLLER", new AilmentMinimizeMove(Types.BUG, MoveType.PHYSICAL, 20, 65, 100, 80, 30, MoveUtils::flinch));
		REGISTRY.put("STOMP", new AilmentMinimizeMove(Types.NORMAL, MoveType.PHYSICAL, 20, 65, 100, 80, 30, MoveUtils::flinch));

		REGISTRY.put("ABSORB", new HPStealingMove(Types.GRASS, MoveType.SPECIAL, 25, 20, 100, 40, 50));

		REGISTRY.put("FLY", new SemiInvulChargeMove(Types.FLYING, MoveType.PHYSICAL, 15, 90, 95, 120, "%s flew up high!",
				Flags.FLIGHT, Flags.GUST_VULNURABLE));
		REGISTRY.put("RAZOR_WIND", new ChargeMove(Types.NORMAL, MoveType.SPECIAL, 10, 80, 100, 70, "%s whipped up a whirlwind!"));
		REGISTRY.put("SKY_ATTACK", new ChargeMove(Types.FLYING, MoveType.PHYSICAL, 5, 140, 90, 150, "%s is glowing!"){
			@Override
			public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
				super.runAfter(channel, attacker, defender, damage);
				if(Pokebot.diceRoll(30)) MoveUtils.flinch(channel, defender);
			}
		});

		REGISTRY.put("AFTER_YOU", new FlagMove(Types.NORMAL, 15, -1, 50, null));
		REGISTRY.put("DESTINY_BOND", new FlagMove(Types.GHOST, 5, -1, 100, "%s will take it's foe down with it!",
				Flags.UNTARGETABLE));

		//TODO Blizzard - doubles power in hail, hits everyone, and has a 10% chance of freezing
		//BLIZZARD(Types.ICE, MoveType.SPECIAL, 5, 110, 70, 115, Flags.HAS_BEFORE, Flags.HAS_AFTER),

		//TODO if the user has no item, power is doubled
		REGISTRY.put("ACROBATICS", new Move(Types.FLYING, MoveType.PHYSICAL, 15, 55, 100, 120){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				return BeforeResult.HAS_ADJUSTED_DAMAGE;
			}

			@Override
			protected int getAdjustedDamage(Player attacker, Player defender){
				return getDamage(attacker, this, defender, this.power*2);
			}
		});

		REGISTRY.put("ACUPRESSURE", new Move(Types.NORMAL, MoveType.STATUS, 30, -1, -1, 60){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				if(attacker != defender && defender.has(Effects.VBattle.SUBSTITUTE)){
					Messages.fail(channel, attacker);
					return BeforeResult.STOP;
				}
				ArrayList<Stats> stats = new ArrayList<>(Arrays.asList(Stats.values()));
				stats.remove(Stats.HEALTH);
				stats.removeIf(stat -> defender.modifiers[stat.getIndex()] == 6);
				if(stats.isEmpty()){
					Messages.fail(channel, attacker);
					return BeforeResult.STOP;
				}
				StatHandler.changeStat(channel, defender, stats.get(Pokebot.ran.nextInt(stats.size())), 2);
				return BeforeResult.STOP;
			}
		});

		REGISTRY.put("ANCIENT_POWER", new DamageMove(Types.ROCK, MoveType.SPECIAL, 5, 60, 100, 150){
			@Override
			public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
				if(Pokebot.diceRoll(10)){
					for(int x = 1; x < 6; x++){
						StatHandler.changeStat(channel, attacker, Stats.getStatFromIndex(x), 1);
					}
				}
			}
		});

		REGISTRY.put("AQUA_RING", new Move(Types.WATER, MoveType.STATUS, 20, -1, -1, 150, Flags.UNTARGETABLE){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				//TODO BigRoot increases restoration
				attacker.set(Effects.VBattle.AQUA_RING);
				return BeforeResult.STOP;
			}
		});

		REGISTRY.put("AROMATHERAPY", new Move(Types.GRASS, MoveType.STATUS, 5, -1, -1, 50){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				//These are status moves, battle checking is done for us
				attacker.cureNV();
				for(Player player : attacker.battle.getParticipants()){
					if(player == attacker) continue;
					//TODO Sap Sipper will prevent Aromatherapy from working, instead raising their speed by 1 stage
					player.cureNV();
					player.lastAttacker = attacker; //Counts as "attacking" that pokemon
				}
				Pokebot.sendMessage(channel, "A soothing aroma wafted through the area, curing everyone of all status effects!");
				return BeforeResult.STOP;
			}
		});

		REGISTRY.put("AVALANCHE", new Move(Types.ICE, MoveType.PHYSICAL, 10, 60, 100, 80, Battle_Priority.N4){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				return BeforeResult.HAS_ADJUSTED_DAMAGE;
			}

			@Override
			protected int getAdjustedDamage(Player attacker, Player defender){
				return getDamage(attacker, this, defender); //TODO does double damage if attacker was hit
			}
		});

		REGISTRY.put("GRAVITY", new Move(Types.PSYCHIC, MoveType.STATUS, 5, -1, -1, 100, Flags.UNTARGETABLE){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				attacker.battle.set(Battle.BattleEffects.GRAVITY, 1);
				Messages.attackMessage(channel, attacker, this);
				Pokebot.sendMessage(channel, "Gravity Intensified!");
				return BeforeResult.STOP;
			}
		});

		REGISTRY.put("HEAL_BELL", new Move(Types.NORMAL, MoveType.STATUS, 5, -1, -1, 50){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				//These are status moves, battle checking is done for us
				attacker.cureNV();
				for(Player player : attacker.battle.getParticipants()){
					if(player == attacker) continue;
					player.cureNV();
					player.lastAttacker = attacker; //Counts as "attacking" that pokemon
				}
				Pokebot.sendMessage(channel, "A bell chimed, curing everyone of all status effects!");
				return BeforeResult.STOP;
			}
		});

		REGISTRY.put("JUMP_KICK", new Move(Types.FIGHTING, MoveType.PHYSICAL, 10, 100, 95, 100, Flags.FLIGHT){
				//Has recoil){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				if(willHit(this, attacker, defender, true)){
					return BeforeResult.CONTINUE;
				}
				Pokebot.sendMessage(channel, attacker.mention()
						+" missed and took crash damage instead!");
				attacker.HP = Math.max(0, attacker.HP-(attacker.getMaxHP()/2));
				return BeforeResult.STOP;
			}
		});

		REGISTRY.put("GASTRO_ACID", new Move(Types.POISON, MoveType.STATUS, 10, -1, 100, 150){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				if(willHit(this, attacker, defender, true)){ //Implicit battle check, because it's a status move
					Messages.attackMessage(channel, attacker, this);
					defender.set(Effects.VBattle.ABILITY_BLOCK);
					Pokebot.sendMessage(channel, defender.mention()+" 's ability was suppressed!");
				} else {
					Messages.miss(channel, attacker);
				}
				return BeforeResult.STOP;
			}
		});

		//TODO this can probably be in a OHKO move class, since other moves will be like it
		REGISTRY.put("GUILLOTINE", new Move(Types.NORMAL, MoveType.PHYSICAL, 5, -1, 30, 150){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				if(willHit(this, attacker, defender, true)){
					defender.HP = 0;
					Pokebot.sendMessage(channel, defender.mention()+" was OHKO'd!");
				} else {
					Messages.miss(channel, attacker);
				}
				return BeforeResult.STOP;
			}
		});

		REGISTRY.put("SPLASH", new Move(Types.WATER, MoveType.PHYSICAL, 999, -1, 100, 200,
				Flags.UNTARGETABLE, Flags.FLIGHT){
			@Override
			public BeforeResult runBefore(IChannel channel, Player attacker, Player defender){
				Pokebot.sendMessage(channel, "... But nothing happened");
				return BeforeResult.STOP;
			}
		});

		System.out.println("Done registering moves");
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public static boolean attack(IChannel channel, IAttack attack){
		return attack(channel, attack.attacker, attack.move, attack.defender);
	}

	//Returns ifDefenderFainted
	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public static boolean attack(IChannel channel, Player attacker, MoveSet moveSet, Player defender){
		Move move = moveSet.getMove();
		if(!attacker.inBattle() && move.getMoveType() == MoveType.STATUS){
			Pokebot.sendMessage(channel, "But it doesn't work here!");
			return false;
		}
		if(attacker.has(Effects.NonVolatile.FROZEN)){
			//TODO if the attacker uses Fusion Flare, Flame Wheel, Sacred Fire, Flare Blitz
			//TODO or Scald, it will thaw them and/or the opponent out
			if(Pokebot.diceRoll(20)){
				attacker.cureNV();
				Messages.unfreeze(channel, attacker);
			} else {
				Messages.isFrozen(channel, attacker);
				return false;
			}
		}
		if(attacker.has(Effects.NonVolatile.SLEEP)){
			//TODO if the move is snore or sleep talk, then it will work
			if(attacker.counter-- <= 0){
				Messages.wokeUp(channel, attacker);
			} else {
				Messages.isAsleep(channel, attacker);
				return false;
			}
		}
		Pokebot.sendMessage(channel, attacker.mention()+" used "+move.getDisplayName()+"!");
		BeforeResult cont = move.runBefore(channel, attacker, defender);
		if(cont != BeforeResult.STOP || cont != BeforeResult.IGNORE_PP){
			if(!moveSet.canBeUsed()) cont = BeforeResult.STOP;
		}
		switch(cont){
			//noinspection DefaultNotLastCaseInSwitch
			default:{
				if(cont.willHitAlways() || willHit(move, attacker, defender, !(move.getAccuracy() > 1))){
					//Do battle attack logic
					int damage;
					if(cont.hasAdjustedDamage()){
						damage = move.getAdjustedDamage(attacker, defender);
					} else {
						damage = getDamage(attacker, move, defender);
					}
					defender.HP = Math.max(0, defender.HP-damage);
					Messages.dealtDamage(channel, attacker, damage);
					if(attacker.inBattle()) move.runAfter(channel, attacker, defender, damage);
					//"After Damage"
					/*if(defender.inBattle()){
						defender.getModifiedItem().onAfterDamage(channel, attacker, move, defender, damage);

					}*/
					//attackMessage(channel, attacker, move, defender, damage);
				} else { //we check here again to make sure cont wasn't what made it not run
					Messages.miss(channel, attacker);
				}
				break;
			}
			case RUN_AFTER:{
				move.runAfter(channel, attacker, defender, 0);
				break;
			}
			case STOP:
				break;
		}
		if(attacker.HP == 0 && !attacker.inBattle()){
			//Checking for things like recoil
			Messages.fainted(channel, attacker);
		}
		if(defender.HP == 0){
			Messages.fainted(channel, defender);
			return true;
		}
		Pokebot.sendMessage(channel, defender.mention()+" has "+defender.HP+"HP left!");
		return false;
	}

	//Call AFTER you heal
	public static void heal(IChannel channel, Player attacker, int heal){
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
			Pokebot.sendMessage(channel, attacker.mention()+" was fully healed!");
		} else {
			Pokebot.sendMessage(channel, attacker.mention()+" restored "+heal+"HP!");
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
		defender.HP = Math.max(0, defender.HP-damage);
		Pokebot.sendMessage(channel, defender.mention()+" hurt themselves for "+damage+" damage!");
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	private static boolean runBattleLogic(Player attacker, Player defender){
		return attacker.inBattle() && defender.inBattle() && attacker.battle == defender.battle;
	}

	public static int getDamage(Player attacker, Move move, Player defender){
		return getDamage(attacker, move, defender, move.getPower());
	}

	public static int getDamage(Player attacker, Move move, Player defender, int power){
		double modifier =
				getStab(attacker, move) //STAB
						*Types.getTypeMultiplier(attacker, move, defender) //Effectiveness
						*getOtherModifiers(attacker, move, defender)
						*((Pokebot.ran.nextInt(100-85)+85+1)/100D) //Random chance, it would be 85-99 if there wasn't the +1
				;
		switch(attacker.getModifiedAbility()){
			case ANALYTIC:{
				modifier *= 1.3; //30% increase
				break;
			}
			case BLAZE:{
				if(move.getType(attacker) == Types.FIRE && attacker.HP < Math.floorDiv(attacker.getMaxHP(), 3)){
					modifier *= 1.5;
				}
				break;
			}
			default:
				break;
		}
		double a = ((2*attacker.level)+10D)/250D;
		double b;
		if(move.isSpecial()){
			b = attacker.getSpecialAttackStat();
			b /= defender.getSpecialDefenseStat();
		} else {
			b = attacker.getAttackStat();
			b /= defender.getDefenseStat();
		}
		power = (int) getPowerChange(attacker, move, defender, power);
		double ret = ((a*b*power)+2)*modifier;
		return (int) ret; //implicit math.floor
	}

	public static double getStab(Player attacker, Move move){
		if(attacker.hasType(move.getType(attacker))){
			if(attacker.hasAbility(Abilities.ADAPTABILITY)) return 2;
			return 1.5;
		}
		return 1;
	}

	@SuppressWarnings("UnusedParameters")
	public static double getPowerChange(Player attacker, Move move, Player defender, double power){
		switch(attacker.getModifiedAbility()){
			case AERILATE:{
				power *= 1.3;
				break;
			}
			default:
				break;
		}
		return power;
	}

	//TODO perhaps this isn't necessary?
	public static float getOtherModifiers(Player attacker, Move move, Player defender){
		/* TODO switch(move){
			case GUST:{
				switch(defender.lastMove){
					case FLY:{
						if(defender.lastMoveData == MoveConstants.FLYING) return 2;
						break;
					}
					default:
						break;
				}
				break;
			}
			default:
				break;
		} */
		float ret = 1;
		if(move == REGISTRY.get("GUST") && defender.lastMoveHas(Flags.GUST_VULNURABLE) && defender.lastMoveData != 0) ret *= 2;
		//if(attacker.heldItem.getPoweredUpType() == move.getType(attacker)) ret *= 1.2; //20% boost
		return ret;
	}

	//Dice rolls for a hit, if not factoring in changes to accuracy and evasion, you can safely
	//pass in null for Attacker and Defender
	@SuppressWarnings("BooleanParameter")
	public static boolean willHit(Move move, Player attacker, Player defender, boolean factorChanges){
		if(move == null) return false;
		if(MoveUtils.checkParalysis(attacker)) return false;
		if(defender.inBattle()){
			if(defender.has(Effects.VBattle.SEMI_INVULNERABLE)){
				/* TODO switch(move){
					//Pokémon that use Fly, Bounce, or Sky Drop, or are targeted by Sky Drop fly or are flown up high, and
					// are vulnerable to Gust, Smack Down, Sky Uppercut, Thunder, Twister, and Hurricane
					//If the move Gravity is used, these moves cannot be used and any Pokémon in the air return to the ground with their move cancelled

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
				}*/
				if(move == REGISTRY.get("GUST")&& defender.lastMoveHas(Flags.GUST_VULNURABLE)){
					return true;
				}
				Pokebot.sendMessage(defender.battle.channel, "But there was no target!");
				return false;
			}
			if(defender.has(Effects.VBattle.PROTECTION)){
				switch(move.getName()){
					//TODO Shadow Force and Fient remove protection for the rest of the turn
					//TODO
					default:
						Pokebot.sendMessage(defender.battle.channel, "But "+defender.mention()+" protected itself!");
						return false;
				}
			}
			switch(defender.getModifiedAbility()){
				case BULLETPROOF:{
					if(move.has(Flags.BALLBASED)){
						Pokebot.sendMessage(defender.battle.channel, defender.mention()+" is immune to the attack!");
						return false;
					}
					break;
				}
				default:
					break;
			}
		}

		double accuracy = move.getAccuracy();
		if(factorChanges){
			accuracy *= attacker.getAccuracy()/defender.getEvasion();
		}
		return Pokebot.ran.nextDouble() <= accuracy;
	}

	protected enum BeforeResult{
		CONTINUE,
		HAS_ADJUSTED_DAMAGE(false, true),
		ALWAYS_HIT(true),
		ALWAYS_HIT_ADJUSTED_DAMAGE(true, true),
		//SKIP_DAMAGE,
		RUN_AFTER,
		IGNORE_PP,
		STOP;

		private final boolean hitAlways;
		private final boolean hasAdjustedDamage;

		BeforeResult(){
			this(false);
		}

		BeforeResult(boolean hitAlways){
			this(hitAlways, false);
		}

		BeforeResult(boolean hitAlways, boolean hasAdjustedDamage){
			this.hitAlways = hitAlways;
			this.hasAdjustedDamage = hasAdjustedDamage;
		}

		public boolean willHitAlways(){
			return this.hitAlways;
		}

		public boolean hasAdjustedDamage(){
			return this.hasAdjustedDamage;
		}
	}
}
