package coolway99.discordpokebot;

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

public class StatHandler{
	
	public static final int MAX_TOTAL_POINTS = 1000;
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
	
	
	public static int calcStatValue(int statpoints, int ivpoints, int evpoints, int level, boolean isHP){
		//TODO Nature
		int firstStage =  (int) Math.floor(((2*statpoints+ivpoints+evpoints)*level)/100D);
		return Math.min((isHP ?
				(firstStage + level + 10) 
				: ((firstStage+5)/*TODO *nature */))
				, 999); //The highest a single stat can go is 999
	}
	
	public static int getTotalPoints(Player player){
		return getCombinedPoints(player, SubStats.BASE)
				+ getCombinedPoints(player, SubStats.IV)
				+ getCombinedPoints(player, SubStats.EV);
		//TODO include other point-taking things like moves
	}
	
	public static int getCombinedPoints(Player player, SubStats sub){
		int total = 0;
		for(int x = 0; x < player.stats.length; x++){
			total += player.stats[x][sub.getIndex()];
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
					+ "\nInvalid stat, avalible stats are health, attack, special_attack,"
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
	
	public static enum Stats{
		HEALTH(0),
		ATTACK(1),
		SPECIAL_ATTACK(2),
		DEFENSE(3),
		SPECIAL_DEFENSE(4),
		SPEED(5);
		
		private final int index;
		
		private Stats(int x){
			this.index = x;
		}
		
		public static Stats getStatFromIndex(int i){
			switch(i){
				case 0:
					return HEALTH;
				case 1:
					return ATTACK;
				case 2:
					return SPECIAL_ATTACK;
				case 3:
					return DEFENSE;
				case 4:
					return SPECIAL_DEFENSE;
				case 5:
					return SPEED;
				default:
					return null;
			}
		}
		
		public static Stats getStatFromString(String s){
			switch(s.toLowerCase()){
				case "health":
				case "h":
				case "hp":{
					return Stats.HEALTH;
				}
				case "a":
				case "attack":{
					return Stats.ATTACK;
				}
				case "special attack":
				case "special_attack":
				case "sattack":
				case "sa":{
					return Stats.SPECIAL_ATTACK;
				}
				case "defense":
				case "d":{
					return Stats.DEFENSE;
				}
				case "special defense":
				case "special_defense":
				case "sdefense":
				case "sd":{
					return Stats.SPECIAL_DEFENSE;
				}
				case "speed":
				case "sp":{
					return Stats.SPEED;
				}
				default:{
					return null;
				}
			}
		}
		
		public int getIndex(){
			return this.index;
		}
	}
	
	public static enum SubStats{
		BASE(0),
		IV(1),
		EV(2);
		
		private final int index;
		
		private SubStats(int x){
			this.index = x;
		}
		
		public int getIndex(){
			return this.index;
		}
		
		public static SubStats getSubStatFromIndex(int i){
			switch(i){
				case 0:
					return BASE;
				case 1:
					return IV;
				case 2:
					return EV;
				default:
					return null;
			}
		}
		
		public static SubStats getSubStatFromString(String s){
			if(s == null) return SubStats.BASE;
			switch(s.toLowerCase()){
				case "d": //Sometimes called "DV"
				case "dv":
				case "i":
				case "iv":{
					return SubStats.IV;
				}
				case "ev":
				case "e":{
					return SubStats.EV;
				}
				default:{
					return SubStats.BASE;
				}
			}
		}
	}
}
