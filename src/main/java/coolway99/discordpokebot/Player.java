package coolway99.discordpokebot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import coolway99.discordpokebot.StatHandler.Stats;
import coolway99.discordpokebot.StatHandler.SubStats;
import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.types.Types;
import sx.blah.discord.handle.obj.IUser;

//TODO
//The variables all contain the default states
public class Player{
	
	public IUser user;
	public Types primary = Types.NORMAL;
	public Types secondary = Types.NULL;
	public int HP = 0;
	public int level = StatHandler.MAX_LEVEL;
	//Physical Stats
	/**
	 * An array containing all the stats of the player, they go in this order:
	 * health
	 * attack
	 * special attack
	 * defense
	 * special defense
	 * speed
	 * 
	 * additionally, there are also 3 subStats for each:
	 * base
	 * IV
	 * EV
	 */
	public int[][] stats = new int[6][3];
	
	/**
	 * An array holding the modifiers for each stat
	 */
	public byte[] modifiers = new byte[8];
	/**
	 * The nature of the player
	 */
	public Natures nature = Natures.values()[Pokebot.ran.nextInt(Natures.values().length)];
	/**
	 * What effect is the player currently under?
	 */
	public Effects effect = Effects.NORMAL;
	
	public int numOfAttacks = 0;
	//This array is manually done out as to make sure they are "null" type moves, to prevent errors
	public Moves[] moves = new Moves[]{Moves.NULL, Moves.NULL, Moves.NULL, Moves.NULL};
	public int[] PP = new int[4];
	
	public Battle battle = null;
	public boolean isSemiInvunerable = false; //Set by moves, is not used outside of a battle
	public Moves lastMove = Moves.NULL; //Isn't set outside of a battle
	public int lastMovedata = 0; //Can be used by moves for whatever they want, only used in battles
	public Player lastTarget = null; //Only set in-battle. Null if there wasn't a target
	public Player lastAttacker = null; //Only set in-battle. Null if there wasn't an attacker
	
	public Player(IUser user){
		this.user = user;
		this.loadData();
		this.HP = this.getMaxHP();
		for(int x = 0; x < this.numOfAttacks; x++){
			this.PP[x] = this.moves[x].getPP();
		}
	}
	
	//the user object is public final anyways
	/*public IUser getUser(){
		return this.user;
	}*/
	
	public boolean hasSecondaryType(){
		return this.secondary != Types.NULL;
	}
	
	public boolean inBattle(){
		return this.battle != null;
	}
	
	public int getMaxHP(){
		return StatHandler.calcStatValue(this.stats[Stats.HEALTH.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.HEALTH.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.HEALTH.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.HEALTH,
				this.modifiers[Stats.HEALTH.getIndex()],
				this.effect,
				this.nature);
	}
	
	public int getAttackStat(){
		return StatHandler.calcStatValue(this.stats[Stats.ATTACK.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.ATTACK.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.ATTACK.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.ATTACK,
				this.modifiers[Stats.ATTACK.getIndex()],
				this.effect,
				this.nature);
	}
	
	public int getSpecialAttackStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.SPECIAL_ATTACK,
				this.modifiers[Stats.SPECIAL_ATTACK.getIndex()],
				this.effect,
				this.nature);
	}
	
	public int getDefenseStat(){
		return StatHandler.calcStatValue(this.stats[Stats.DEFENSE.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.DEFENSE.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.DEFENSE.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.DEFENSE,
				this.modifiers[Stats.DEFENSE.getIndex()],
				this.effect,
				this.nature);
	}
	
	public int getSpecialDefenseStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.SPECIAL_DEFENSE,
				this.modifiers[Stats.SPECIAL_DEFENSE.getIndex()],
				this.effect,
				this.nature);
	}
	
	public int getSpeedStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPEED.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.SPEED.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.SPEED.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.SPEED,
				this.modifiers[Stats.SPEED.getIndex()],
				this.effect,
				this.nature);
	}
	
	public double getAccuracy(){
		return StatHandler.getHitModifierChange(this.modifiers[Stats.ACCURACY.getIndex()]);
	}
	
	public double getEvasion(){
		return StatHandler.getModifierChange(this.modifiers[Stats.EVASION.getIndex()]);
	}
	
	public int getStatFromIndex(int x){
		switch(x){
			case 0:
				return this.getMaxHP();
			case 1:
				return this.getAttackStat();
			case 2:
				return this.getSpecialAttackStat();
			case 3:
				return this.getDefenseStat();
			case 4:
				return this.getSpecialDefenseStat();
			case 5:
				return this.getSpeedStat();
			default:
				return 0;
		}
	}
	
	public void loadData(){
		//If the file is "incomplete", which should only result when the save format is updated
		//with more info, then this will error out and close the file, with the default values
		//being intact for the values not found
		System.out.println("Loading");
		File file = Pokebot.getSaveFile(this.user);
		if(!file.exists()) return; //Use defaults
		try(Scanner in = new Scanner(file)){
			this.primary = Types.valueOf(in.nextLine());
			this.secondary = Types.valueOf(in.nextLine());
			
			for(int y = 0; y < this.stats.length; y++){
				for(int x = 0; x < this.stats[0].length; x++){
					this.stats[y][x] = in.nextInt();
				}
			}
			
			this.numOfAttacks = in.nextInt();
			in.nextLine(); //nextInt tends to leave over the \n, it seems
			for(int x = 0; x < this.moves.length; x++){
				this.moves[x] = Moves.valueOf(in.nextLine());
			}
			this.level = in.nextInt();
			in.nextLine();
			this.nature = Natures.valueOf(in.nextLine());
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(NoSuchElementException e){
			System.err.println("We read from an incomplete or invalid file");
		}
	}
	
	public boolean hasMove(Moves move){
		for(int x = 0; x < this.moves.length; x++){
			if(this.moves[x].equals(move)) return true;
		}
		return false;
	}
	
	public void saveData(){
		System.out.println("Beginning to save");
		File file = Pokebot.getSaveFile(this.user);
		if(!file.exists() && file.getParentFile() != null){
			file.getParentFile().mkdirs();
		}
		try(PrintStream out = new PrintStream(file)){
			out.println(this.primary.toString());
			out.println(this.secondary.toString());
			
			for(int y = 0; y < this.stats.length; y++){
				for(int x = 0; x < this.stats[0].length; x++){
					out.println(this.stats[y][x]);
				}
			}
			
			out.println(this.numOfAttacks);
			for(int x = 0; x < this.moves.length; x++){
				out.println(this.moves[x].toString());
			}
			out.println(this.level);
			out.println(this.nature);
			out.flush();
			out.close();
			System.out.println(Pokebot.getSaveFile(this.user).getAbsolutePath());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
