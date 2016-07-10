package coolway99.discordpokebot;

import coolway99.discordpokebot.StatHandler.Stats;
import coolway99.discordpokebot.types.Types;
import sx.blah.discord.handle.obj.IChannel;

public enum Moves{
	//ENUM_NAME("Textual Name", Type, isSpecial", PP, Power, Accuracy (either int or double)
	NULL("", Types.NULL, false, 0, 0, 0), //TODO perhaps make this Struggle
	POUND("pound", Types.NORMAL, false, 35, 40, 100),
	KARATE_CHOP("karate chop", Types.FIGHTING, false, 25, 50, 100),
	DOUBLE_SLAP("double slap", Types.NORMAL, false, 10, 15, 85, true, false),
	COMET_PUNCH("comet punch", Types.NORMAL, false, 15, 18, 85, true, false),
	MEGA_PUNCH("mega punch", Types.NORMAL, false, 20, 80, 85),
	PAY_DAY("pay day", Types.NORMAL, false, 20, 80, 85),
	FIRE_PUNCH("fire punch", Types.FIRE, false, 15, 75, 100, false, true),
	ICE_PUNCH("ice punch", Types.ICE, false, 15, 75, 100, false, true),
	THUNDER_PUNCH("thunder punch", Types.ELECTRIC, false, 15, 75, 100, false, true),
	SCRATCH("scratch", Types.NORMAL, false, 35, 40, 100),
	VICE_GRIP("vice grip", Types.NORMAL, false, 30, 55, 100),
	GUILLOTINE("guillotine", Types.NORMAL, false, 5, -1, 30, true, false),
	RAZOR_WIND("razor wind", Types.NORMAL, true, 10, 80, 100), //TODO It's a multiturn-attack
	SWORDS_DANCE("swords dance", Types.NORMAL, false, 20, -1, -1, true, false), //Status attack
	CUT("cut", Types.NORMAL, false, 30, 50, 95),
	GUST("gust", Types.FLYING, true, 35, 40, 100), //TODO affects fly
	WING_ATTACK("wing attack", Types.FLYING, true, 35, 60, 100),
	//WHIRLWIND does not apply
	FLY("fly", Types.FLYING, false, 15, 90, 95), //TODO Multiturn
	BIND("bind", Types.NORMAL, false, 20, 15, 85), //TODO Multiturn
	SLAM("slam", Types.NORMAL, false, 20, 80, 75),
	VINE_WHIP("vine whip", Types.GRASS, false, 25, 45, 100),
	STOMP("stomp", Types.NORMAL, false, 20, 65, 100, true, false),
	SPLASH("splash", Types.WATER, false, 999, 0, 100, true, false), //Fine, you people win
	;
	
	private final String name;
	private final Types type;
	private final int power;
	private final boolean isSpecial;
	private final int PP; //The default PP of the move
	private final double accuracy; //From 0 to 1
	private final boolean hasBeforeEffect; //Will code be ran BEFORE attacking?
	private final boolean hasAfterEffect; //Will code be ran AFTER attacking?
	
	private Moves(String name, Types type, boolean isSpecial, int PP, int power, int accuracy){
		this(name, type, isSpecial, PP, power, accuracy, false, false);
	}

	private Moves(String name, Types type, boolean isSpecial, int PP, int power,
			double accuracy, boolean hasBefore, boolean hasAfter){
		if(name == null){
			name = "";
			type = Types.NULL;
			power = 0;
			isSpecial = false;
			PP = 0;
			accuracy = 0;
			hasAfter = false;
			hasBefore = false;
		}
		this.name = name;
		this.type = type;
		this.power = power;
		this.isSpecial = isSpecial;
		this.PP = PP;
		this.accuracy = accuracy;
		this.hasBeforeEffect = hasBefore;
		this.hasAfterEffect = hasAfter;
	}
	
	private Moves(String name, Types type, boolean isSpecial, int PP, int power,
			int accuracy, boolean hasBefore, boolean hasAfter){
		this(name, type, isSpecial, PP, power, accuracy/100D, hasBefore, hasAfter);
	}
	
	public boolean isSpecial(){
		return this.isSpecial;
	}
	
	public int getPower(){
		return this.power;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Types getType(){
		return this.type;
	}
	
	public int getPP(){
		return this.PP;
	}
	
	//TODO
	@SuppressWarnings("static-method")
	public int getCost(){
		return 0;
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
			case DOUBLE_SLAP:
			case COMET_PUNCH:{
				//TODO when items are in, each attack can be blocked
				//TODO protect and the stuff
				if(willHit(this, attacker, defender, true)){
					int timesHit = getTimesHit(5, 100D, 33.3D, 33.3D, 16.7D, 16.7D);
					int damage = getDamage(attacker, this, defender)*timesHit;
					Pokebot.sendBatchableMessage(channel, attacker.getUser().mention()+" attacked "+defender.getUser().mention()
							+" "+timesHit+" times for a total of "+damage+"HP of damage!");
					defender.HP = Math.max(0, defender.HP - damage);
				} else {
					missMessage(channel, attacker);
				}
				return false;
			}
			case GUILLOTINE:{
				if(willHit(this, attacker, defender, false)){
					Pokebot.sendBatchableMessage(channel, attacker.getUser().mention()+" one-hit KO'd "+defender.getUser().mention());
					defender.HP = 0; //Guillotine is a one-hit KO if it hits, and it's accuracy is based on the level of the pokemon
				} else {
					missMessage(channel, attacker);
				}
				return false;
			}
			case SWORDS_DANCE:{
				if(!defender.inBattle()){
					Pokebot.sendBatchableMessage(channel, "But it doesn't work here!");
				}
				StatHandler.raiseStat(channel, defender, Stats.ATTACK, true);
				return false;
			}
			case SPLASH:{
				Pokebot.sendBatchableMessage(channel, attacker.getUser().mention()+" used Splash!... but nothing happened.");
				return false;
			}
			default:
				break;
		}
		return true;
	}
	
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		switch(this){
			case FIRE_PUNCH:{
				if(defender.inBattle() && diceRoll(10)){
					if(!isType(defender, Types.FIRE)) defender.effect = Effects.BURN;
					burnMessage(channel, defender, isType(defender, Types.FIRE));
				}
				break;
			}
			case ICE_PUNCH:{
				if(defender.inBattle() && diceRoll(10)){
					if(!isType(defender, Types.ICE)) defender.effect = Effects.FROZEN;
					freezeMessage(channel, defender, isType(defender, Types.ICE));
				}
				break;
			}
			case THUNDER_PUNCH:{
				if(defender.inBattle() && diceRoll(10)){
					if(!isType(defender, Types.ELECTRIC)) defender.effect = Effects.PARALYSIS;
					paralyzedMessage(channel, defender, isType(defender, Types.ELECTRIC));
				}
				break;
			}
			default:
				break;
		}
	}
	
	public static boolean attack(IChannel channel, Player attacker, Moves move, Player defender){
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
		Pokebot.sendBatchableMessage(channel, defender.getUser().mention()+" has "+defender.HP+"HP left!");
		return false;
	}
	
	public static boolean attack(IChannel channel, IAttack attack){
		return attack(channel, attack.attacker, attack.move, attack.defender);
	}
	
	private static void attackMessage(IChannel channel, Player attacker, Moves move, Player defender, int damage){
		Pokebot.sendBatchableMessage(channel, attacker.getUser().mention()
				+" attacked "+defender.getUser().mention()+" with "+move.getName()
				+" for "+damage+" damage!");
	}
	
	private static void faintMessage(IChannel channel, Player defender){
		Pokebot.sendBatchableMessage(channel, defender.getUser().mention()+" has fainted!");
	}
	
	private static void missMessage(IChannel channel, Player attacker){
		Pokebot.sendBatchableMessage(channel, "But "+attacker.getUser().mention()+" missed!");
	}
	
	private static void burnMessage(IChannel channel, Player defender, boolean isImmune){
		effectMessage(channel, defender, isImmune, "burns", "burned");
	}
	
	private static void freezeMessage(IChannel channel, Player defender, boolean isImmune){
		effectMessage(channel, defender, isImmune, "freezing", "frozen");
	}
	
	private static void paralyzedMessage(IChannel channel, Player defender, boolean isImmune){
		effectMessage(channel, defender, isImmune, "paralysis", "paralyzed");
	}
	
	private static void effectMessage(IChannel channel, Player defender, boolean isImmune, String immune, String afflicted){
		if(isImmune){
			Pokebot.sendBatchableMessage(channel, defender.getUser().mention()+"'s type is immune to "+immune+"!");
		} else {
			Pokebot.sendBatchableMessage(channel, defender.getUser().mention()+" was "+afflicted+"!");
		}
	}
	
	public static int getDamage(Player attacker, Moves move, Player defender){
		return (int) //We just drop the remainder, no rounding 
				(((((((attacker.level/5D) + 2) //The D on the 5 makes this entire calculation a double
				*(move.isSpecial() ? attacker.getSpecialAttackStat() :
					attacker.getAttackStat())
				*move.getPower())
				/(move.isSpecial() ? defender.getSpecialDefenseStat() :
					defender.getDefenseStat())
				/50) //This is a constant
				+2)
				*Types.getAttackMultiplier(attacker, move, defender))
				*((Pokebot.ran.nextInt(255-217)+217)/255D));
	}
	
	//Dice rolls for a hit, if not factoring in changes to accuracy and evasion, you can safely
	//pass in null for Attacker and Defender
	public static boolean willHit(Moves move, Player attacker, Player defender, boolean factorChanges){
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
