package coolway99.discordpokebot;

import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Natures;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
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

public class StatHandler{
	
	//Total points are 1000, the extra 100 comes from levels. You can reduce your level to gain extra points...
	public static final int MAX_TOTAL_POINTS = 1100;
	public static final int MAX_LEVEL = 100;
	public static final int MAX_TOTAL_STAT_POINTS = 780;
	public static final int MAX_SINGLE_STAT_POINTS = 255;
	public static final int MAX_SINGLE_IV_POINTS = 31;
	public static final int MAX_TOTAL_EV_POINTS = 127; //Got by dividing 510 by 4 then rounding
	public static final int MAX_SINGLE_EV_POINTS = 63; //Got by dividing 252 by 4
	
	public static final int[] MAX_SINGLE_SUBSTATS = new int[]{
			MAX_SINGLE_STAT_POINTS,
			MAX_SINGLE_IV_POINTS,
			MAX_SINGLE_EV_POINTS
	};
	
	public static final int[] MAX_TOTAL_SUBSTATS = new int[]{
			MAX_TOTAL_STAT_POINTS,
			31*6,
			MAX_TOTAL_EV_POINTS
	};
	
	
	public static int calcStatValue(int statpoints, int ivpoints, int evpoints, int level,
			Stats type, byte modifier, Effects.NonVolatile effect, Natures nature){
		double firstStage =  Math.floor(((2*statpoints+ivpoints+evpoints)*level)/100D);
		double ret = Math.max((type == Stats.HEALTH ?
				(firstStage + level + 10)
				: ((firstStage+5) * nature.getStatMultiplier(type)))
				* getModifierChange(modifier)
				, 1D); //Setting a hard limit here for the lowest a stat can go is 1, to prevent errors
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
		return (int) ret;
	}
	
	public static int getTotalPoints(Player player){
		return getStatPoints(player)
				+ player.level
				+ getMovePoints(player)
				+ player.getAbility().getCost();
		//TODO include other point-taking things like moves
	}
	
	public static int getCombinedPoints(Player player, SubStats sub){
		int total = 0;
		for(int x = 0; x < player.stats.length; x++){
			total += player.stats[x][sub.getIndex()];
		}
		return total;
	}
	
	public static int getStatPoints(Player player){
		int total = 0;
		for(int x = 0; x < player.stats.length; x++){
			for(int y = 0; y < player.stats[0].length; y++){
				total += player.stats[x][y];
			}
		}
		return total;
	}
	
	public static int getMovePoints(Player player){
		int total = 0;
		for(int x = 0; x < player.numOfAttacks; x++){
			total += player.moves[x].getCost();
		}
		return total;
	}
	
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
					+" points left for this category");
			return;
		}
		if(getTotalPoints(player) + change > MAX_TOTAL_POINTS){
			Pokebot.sendMessage(channel, "You don't have enough points overall to do this!");
			return;
		}
		player.stats[s.getIndex()][subS.getIndex()] = newStat;
	}
	
	public static double getModifierChange(byte mod){
		if(mod == 0 || mod > 6 || mod < -6) return 1D;
		double res = (Math.abs(mod+2D)/2D);
		if(mod < 0) res = 1D/res;
		return res;
	}
	
	//This is used for accuracy and evasion
	public static double getHitModifierChange(byte mod){
		if(mod == 0 || mod > 6 || mod < -6) return 1D;
		double res = (Math.abs(mod+3D)/3D);
		if(mod < 0) res = 1D/res;
		return res;
	}
	
	public static void raiseStat(IChannel channel, Player player, Stats stat, boolean sharply){
		player.modifiers[stat.getIndex()] = (byte) Math.min(
				player.modifiers[stat.getIndex()]+(sharply ? 2 : 1),
				6);
		Pokebot.sendMessage(channel, player.user.mention()+"'s "+stat.toString()
		+" increased"+(sharply ? " sharply" : "")+'!');
	}
	
	public static void lowerStat(IChannel channel, Player player, Stats stat, boolean harshly){
		player.modifiers[stat.getIndex()] = (byte) Math.max(
				player.modifiers[stat.getIndex()]-(harshly ? 2 : 1),
				-6);
		Pokebot.sendMessage(channel, player.user.mention()+"'s "+stat.toString()
		+" decreased"+(harshly ? " harshly" : "")+'!');
	}
}
