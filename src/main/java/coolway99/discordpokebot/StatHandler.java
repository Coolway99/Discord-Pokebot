package coolway99.discordpokebot;

import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.states.Natures;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

//The lowest pokemon have stats of 180, while the highest (legendaries) are 780
//This gives a range of 600 stat points
//Each stat can range from 0-255
//Burning a pokemon cuts attack by 1/2th
//Paralysis cuts speed by 1/4th
//Perhaps 1000 is a good cutoff point for max stats...
//The strongest moves will be nerfed via stat points, for example, perhaps Roar of Time will be like 100 points
//IV's will each be one stat point, with each "EV level" one stat point
//EV's raise the "Stat Points" values (it really only happens completely like that at level 100, but idgaf)
//You can lower your level to grind out that extra little bit of points

/**
 * The class that handles anything to do with stats or points. It has several helper functions for dealing with stats or points
 *
 * @author Coolway99
 */
@SuppressWarnings("SpellCheckingInspection")
public class StatHandler{
	
	//Total points are 1000, the extra 100 comes from levels. You can reduce your level to gain extra points...
	/**
	 * The maximum total overall points for player
	 */
	public static final int MAX_TOTAL_POINTS = 1100;
	/**
	 * The maximum level for a player
	 */
	public static final int MAX_LEVEL = 100;
	/**
	 * The maximum base stat points a player can have
	 */
	public static final int MAX_TOTAL_STAT_POINTS = 780;
	/**
	 * The maximum points a player can have in any single base stat at once
	 */
	public static final int MAX_SINGLE_STAT_POINTS = 255;
	/**
	 * The maximum number of IV points a player can have in any single stat at once
	 */
	public static final int MAX_SINGLE_IV_POINTS = 31;
	/**
	 * The maximum total points the player can be into EV's
	 */
	public static final int MAX_TOTAL_EV_POINTS = 127; //Got by dividing 510 by 4 then rounding
	/**
	 * The maximum number of points the player can put into a single EV
	 */
	public static final int MAX_SINGLE_EV_POINTS = 63; //Got by dividing 252 by 4

	/**
	 * An array for convinence
	 */
	public static final int[] MAX_SINGLE_SUBSTATS = new int[]{
			MAX_SINGLE_STAT_POINTS,
			MAX_SINGLE_IV_POINTS,
			MAX_SINGLE_EV_POINTS
	};

	/**
	 * Another array for convience
	 */
	private static final int[] MAX_TOTAL_SUBSTATS = new int[]{
			MAX_TOTAL_STAT_POINTS,
			31*6,
			MAX_TOTAL_EV_POINTS
	};

	/**
	 * This takes in everything it needs to know, and calculates a Stat value based on it
	 * @param statpoints The base stat of a player
	 * @param ivpoints The iv for this stat
	 * @param evpoints The ev for this stat
	 * @param level The level of the player
	 * @param type The type of stat. This is used for checking {@link Natures Nature},
	 * {@link Effects.NonVolatile Status Conditions}, and eventually abilities
	 * @param modifier The {@link Player#modifiers modifer} for that stat.
	 * @param effect The {@link Effects.NonVolatile Status Effect} that the player is currently under
	 * @param nature The nature of the player
	 * @return An int representing the raw stat value. 1 is the lowest it can be
	 *
	 * @see Player#getMaxHP()
	 * @see Player#getAttackStat()
	 * @see Player#getDefenseStat()
	 * @see Player#getSpecialAttackStat()
	 * @see Player#getSpecialDefenseStat()
	 * @see Player#getSpeedStat()
	 */
	public static int calcStatValue(int statpoints, int ivpoints, int evpoints, int level,
			Stats type, byte modifier, Effects.NonVolatile effect, Natures nature){
		//First stage
		//double ret =  Math.floor(((2*statpoints+ivpoints+evpoints)*level)/100D);
		//Really shouldn't be making changes to code in documentation, but it makes it 10x more readable
		double ret = (2*statpoints)+ivpoints+evpoints;
		ret *= level/100D;
		ret = Math.floor(ret);
		//Second Stage
		if(type == Stats.HEALTH){
			ret += level + 10;
		} else {
			ret = (ret+5)*nature.getStatMultiplier(type);
		}
		ret *= getModifierChange(modifier);
		ret = Math.max(ret, 1D); //Setting a hard limit here for the lowest a stat can go is 1, to prevent errors
		switch(type){
			case ATTACK:{
				if(effect == Effects.NonVolatile.BURN) ret /= 2;
				break;
			}
			case SPEED:{
				if(effect == Effects.NonVolatile.PARALYSIS) ret /= 4;
				break;
			}
			default:
				break;
		}
		return (int) ret; //Implicit Math.floor
	}

	/**
	 * Gets the total amount of points used by the player
	 * @param player The player to calculate the points for
	 * @return The points the player is using
	 */
	public static int getTotalPoints(Player player){
		return getStatPoints(player)
				+ player.level
				+ getMovePoints(player)
				+ player.getAbility().getCost();
	}

	/**
	 * Gets the combined points across all {@link Stats} for a given {@link SubStats SubStat}
	 * @param player The player to calculate points for
	 * @param sub The substat to calculate
	 * @return The total points in that substat category
	 */
	public static int getCombinedPoints(Player player, SubStats sub){
		int total = 0;
		for(int x = 0; x < player.stats.length; x++){
			total += player.stats[x][sub.getIndex()];
		}
		return total;
	}

	/**
	 * Gets the combined points across all {@link Stats} and {@link SubStats} for a given player
	 * @param player The player to calculate points for
	 * @return The amount of points the player is using on stats
	 */
	public static int getStatPoints(Player player){
		int total = 0;
		for(int x = 0; x < player.stats.length; x++){
			for(int y = 0; y < player.stats[0].length; y++){
				total += player.stats[x][y];
			}
		}
		return total;
	}

	/**
	 * Gets the combined points across all the player's {@link Move Moves}
	 * @param player The player to calculate points for
	 * @return The amount of points the player is using on moves 
	 */
	public static int getMovePoints(Player player){
		int total = 0;
		for(int x = 0; x < player.numOfAttacks; x++){
			total += player.moves[x].getMove().getCost();
		}
		return total;
	}

	/**
	 * A command used in {@link EventHandler#onMessage(MessageReceivedEvent)} for setting stats
	 * @param channel The channel this is taking place in, used for replies
	 * @param player The player to set stats for
	 * @param stat The name of the stat to set
	 * @param newStat The new value of the stat
	 * @param subStat The optional substat to set it to
	 */
	public static void setStats(IChannel channel, Player player, String stat, int newStat, String subStat){
		if(newStat < 0){
			Pokebot.sendMessage(channel, "The new value must be greater than 0");
			return;
		}
		Stats s = Stats.getStatFromString(stat);
		if(s == null){
			Pokebot.sendMessage(channel, "Usage: setstat <statname> <amount> (optional EV or IV modifier)"
					+ "\nInvalid stat, available stats are health, attack, special_attack,"
					+ "defense, special_defense, and speed, along with EV and IV variants for each");
			return;
		}
		SubStats subS = SubStats.getSubStatFromString(subStat);
		int change = newStat - player.stats[s.getIndex()][subS.getIndex()];
		if(MAX_SINGLE_SUBSTATS[subS.getIndex()] < newStat){
			Pokebot.sendMessage(channel, "Too many points, maximum is "+MAX_SINGLE_SUBSTATS[subS.getIndex()]);
			return;
		}
		if(getCombinedPoints(player, subS) + change > MAX_TOTAL_SUBSTATS[subS.getIndex()]){
			Pokebot.sendMessage(channel, "Not enough points left to do that, you have "
					+(MAX_TOTAL_SUBSTATS[subS.getIndex()]-
							(getCombinedPoints(player, subS)-player.stats[s.getIndex()][subS.getIndex()]))
					+" points left for "+(subS == SubStats.EV ? "EV's" : subS == SubStats.BASE ? "base stats" :
					"ERROR"));
			return;
		}
		if(getTotalPoints(player) + change > MAX_TOTAL_POINTS){
			//Pokebot.sendMessage(channel, "You don't have enough points overall to do this!");
			//return;
			exceedWarning(channel, player);
		}
		player.stats[s.getIndex()][subS.getIndex()] = newStat;
		Pokebot.sendMessage(channel, "Set "+s+" "+subS+" to "+newStat);
	}

	/**
	 * For a given modifier, calulate the percent change done to it
	 * @param mod a given modification ranging from -6 to 6
	 * @return The percent to multiply the stat by
	 */
	public static double getModifierChange(byte mod){
		if(mod == 0 || mod > 6 || mod < -6) return 1D;
		double res = Math.abs(mod+2D)/2D;
		if(mod < 0) res = 1D/res;
		return res;
	}

	/**
	 * For a given modifier on Accuracy and Evasion, calculate the percent change done to it
	 * @param mod A given modification ranging from -6 to 6
	 * @return The percent to multiply the stat by
	 */
	//This is used for accuracy and evasion
	public static double getHitModifierChange(byte mod){
		if(mod == 0 || mod > 6 || mod < -6) return 1D;
		double res = Math.abs(mod+3D)/3D;
		if(mod < 0) res = 1D/res;
		return res;
	}

	/**
	 * Adds a Modifier to the stat
	 * @param channel The channel to send replies too
	 * @param player The player to modify
	 * @param stat The stat to modify
	 * @param amount The amount to modify it by
	 */
	public static void changeStat(IChannel channel, Player player, Stats stat, int amount){
		if(amount == 0){
			Pokebot.sendMessage(channel, player.mention()+"'s "+stat+" remained unchanged!");
			return;
		}
		//Todo if player has a certain ability then amount *= -1;
		int newAmount = player.modifiers[stat.getIndex()];
		if((newAmount < -6 && amount < 0) || (newAmount > 6 && amount > 0)){
			Pokebot.sendMessage(channel, "But "+player.mention()+"'s "+stat+" can't go any "
					+(amount < 0 ? "lower" : "higher")+"!");
			return;
		}
		newAmount += amount;
		if(amount < 0){
			newAmount = Math.max(-6, newAmount);
		} else {
			newAmount = Math.min(6, newAmount);
		}
		player.modifiers[stat.getIndex()] = (byte) newAmount;
		StringBuilder b = new StringBuilder(player.mention()).append("'s ").append(stat).append(" was ");
		switch(amount){
			case 1:
				b.append("raised!");
				break;
			case -1:
				b.append("lowered!");
				break;
			case 2:
				b.append("raised sharply!");
				break;
			case -2:
				b.append("lowered harshly");
				break;
			case 12:
				b.append("was maxed out");
				break;
			case -12:
				b.append("was minimized");
				break;
			default:{
				if(amount < 0){
					b.append("lowered severely!");
				} else {
					b.append("raised greatly!");
				}
				break;
			}
		}
		Pokebot.sendMessage(channel, b.toString());
	}

	/**
	 * Asks if the change in points would exceed the point maximum
	 * @param player The player to get total points for
	 * @param oldAmount The old amount of points, can be 0 and newAmount is the change in points
	 * @param newAmount The new amount of points
	 * @return Would the given change exceed {@link StatHandler#MAX_TOTAL_POINTS} or not
	 */
	public static boolean wouldExceedTotalPoints(Player player, int oldAmount, int newAmount){
		return wouldExceed(getTotalPoints(player), oldAmount, newAmount, MAX_TOTAL_POINTS);
	}

	/**
	 * Would the swapping of the old move with the new move cause the player to exceed the maximum points?
	 * @param player The player to get the total points for
	 * @param oldMove The old move to switch with
	 * @param newMove The new move to switch with
	 * @return Would it exceed {@link StatHandler#MAX_TOTAL_POINTS}
	 */
	public static boolean wouldExceedTotalPoints(Player player, Move oldMove, Move newMove){
		return wouldExceedTotalPoints(player, oldMove.getCost(), newMove.getCost());
	}

	/**
	 * Would the swapping of the old ability with the new ability cause the player to exceed the maximum points?
	 * @param player The player to get the total points for
	 * @param oldAbility The old ability to switch with
	 * @param newAbility The new ability to switch with
	 * @return Would it exceed {@link StatHandler#MAX_TOTAL_POINTS}
	 */
	public static boolean wouldExceedTotalPoints(Player player, Abilities oldAbility, Abilities newAbility){
		return wouldExceedTotalPoints(player, oldAbility.getCost(), newAbility.getCost());
	}

	/**
	 * Calls {@link #wouldExceedTotalPoints(Player, Abilities, Abilities)} using {@link Player#getAbility()} for oldAbility
	 * @param player The player to use
	 * @param newAbility The new ability
	 * @return Would it exceed {@link StatHandler#MAX_TOTAL_POINTS}
	 */
	public static boolean wouldExceedTotalPoints(Player player, Abilities newAbility){
		return wouldExceedTotalPoints(player, player.getAbility(), newAbility);
	}

	/**
	 * Would the number calculated be greater than max
	 * @param current The current number
	 * @param oldAmount The old amount of an object
	 * @param newAmount The new amount of an object
	 * @param max The maximum not to exceed
	 * @return Would current-oldAmount+newAmount be greater than max?
	 */
	public static boolean wouldExceed(int current, int oldAmount, int newAmount, int max){
		return current-oldAmount+newAmount > max;
	}

	/**
	 * Gives the player a warning if they have exceed the maximum total point value
	 * @param channel The channel to send the warning in
	 * @param player The player to address it too
	 */
	public static void exceedWarning(IChannel channel, Player player){
		Pokebot.sendMessage(channel, player.mention()+" warning! You have exceeded the maximum total points. You will" +
				" not be able to attack or battle until this is resolved");
	}
}
