package coolway99.discordpokebot;

import coolway99.discordpokebot.types.Types;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public enum Moves{
	//ENUM_NAME("Textual Name", Type, isSpecial", PP, Power, Accuracy (either int or double)
	NULL("", Types.NULL, false, 0, 0, 0), //TODO perhaps make this Struggle
	POUND("pound", Types.NORMAL, false, 35, 40, 100),
	//TODO Karate Chop
	DOUBLE_SLAP("double_slap", Types.NORMAL, false, 10, 15, 85), //TODO
	COMET_PUNCH("comet_punch", Types.NORMAL, false, 15, 18, 85, true, false),
	MEGA_PUNCH("mega_punch", Types.NORMAL, false, 20, 80, 85),
	PAY_DAY("pay_day", Types.NORMAL, false, 20, 80, 85),
	FIRE_PUNCH("fire_punch", Types.FIRE, false, 15, 75, 100, false, true), //TODO
	//TODO Ice Punch
	//TODO Thunder Punch
	SCRATCH("scratch", Types.NORMAL, false, 35, 40, 100),
	VICE_GRIP("vice_grip", Types.NORMAL, false, 30, 55, 100),
	GUILLOTINE("guillotine", Types.NORMAL, false, 5, -1, 30, true, false),
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
			case COMET_PUNCH:{
				//TODO when items are in, each attack can be blocked
				//TODO protect and the stuff
				if(willHit(this, attacker, defender, true)){
					byte timesHit = 1;
					int damage = getDamage(attacker, this, defender);
					if(Pokebot.ran.nextDouble() < (33.3/100D)){
						timesHit++;
						damage += getDamage(attacker, this, defender);
						if(Pokebot.ran.nextDouble() < (33.3/100D)){
							timesHit++;
							damage += getDamage(attacker, this, defender);
							if(Pokebot.ran.nextDouble() < (16.7/100D)){
								timesHit++;
								damage += getDamage(attacker, this, defender);
								if(Pokebot.ran.nextDouble() < (16.7/100D)){
									timesHit++;
									damage += getDamage(attacker, this, defender);
								}
							}
						}
					}
					Pokebot.sendMessage(channel, attacker.getUser().mention()+" attacked "+defender.getUser().mention()
							+" "+timesHit+" times for a total of "+damage+"HP of damage!");
					defender.HP = Math.max(0, defender.HP - damage);
					//Fainting will take place in the turn area
				} else {
					Pokebot.sendMessage(channel, "But "+attacker.getUser().mention()+" missed!");
				}
				return false;
			}
			case GUILLOTINE:{
				//Can only safely pass in null if last arg is false
				if(willHit(this, null, null, false)){
					Pokebot.sendMessage(channel, attacker.getUser().mention()+" one-hit KO'd "+defender.getUser().mention());
					defender.HP = 0; //Guillotine is a one-hit KO if it hits, and it's accuracy is based on the level of the pokemon
				} else {
					Pokebot.sendMessage(channel, "But "+attacker.getUser().mention()+" missed!");
				}
				return false;
			}
			case SPLASH:{
				Pokebot.sendMessage(channel, attacker.getUser().mention()+" used Splash!... but nothing happened.");
				return false;
			}
			default:
				break;
		}
		return true;
	}
	
	public void runAfter(IChannel channel, Player attacker, Player defender, int damage){
		switch(this){
			
			default:
				break;
		}
	}
	
	public static void attack(IChannel channel, Player attacker, Moves move, Player defender){
		if(move.hasBefore() && !move.runBefore(channel, attacker, defender)) return;
		//Do battle attack logic
		int damage = getDamage(attacker, move, defender);
		defender.HP = Math.max(0, defender.HP - damage);
		if(move.hasAfter()) move.runAfter(channel, attacker, defender, damage);
		Pokebot.sendMessage(channel, attacker.getUser().mention()
				+" attacked "+defender.getUser().mention()+" with "+move.getName()
				+" for "+damage+" damage!");
		if(defender.HP == 0){
			Pokebot.sendMessage(channel, defender.getUser().mention()+" has fainted!");
		} else {
			Pokebot.sendMessage(channel, defender.getUser().mention()+" has "+defender.HP+"HP left!");
		}
		
	}
	
	public static int getDamage(Player attacker, Moves move, Player defender){
		return (int) //We just drop the remainder, no rounding 
				(((((((attacker.level/5D) + 2) //The D on the 5 makes this entire calculation a double
				*(move.isSpecial() ? attacker.getSpecialAttackStat() :
					attacker.getAttackStat()) //TODO - be affected by stat changes
				*move.getPower())
				/(move.isSpecial() ? defender.getSpecialDefenseStat() :
					defender.getDefenseStat()) //TODO - be affected by stat changes
				/50) //This is a constant
				+2)
				*Types.getAttackMultiplier(attacker, move, defender))
				*((Pokebot.ran.nextInt(255-217)+217)/255D));
		//return 0;
	}
	
	//Dice rolls for a hit, if not factoring in changes to accuracy and evasion, you can safely
	//pass in null for Attacker and Defender
	public static boolean willHit(Moves move, Player attacker, Player defender, boolean factorChanges){
		//TODO factor in evasion
		return (Pokebot.ran.nextDouble() < move.getAccuracy());
	}	
}
