package coolway99.discordpokebot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import coolway99.discordpokebot.StatHandler.Stats;
import coolway99.discordpokebot.StatHandler.SubStats;
import coolway99.discordpokebot.types.Types;
import sx.blah.discord.handle.obj.IUser;

//TODO
public class Player{
	
	public IUser user;
	public Types primary = Types.NORMAL;
	public Types secondary = Types.NULL;
	public int HP = 0;
	public int level = 50;
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
	/*
	public int health = 1;
	public int speed = 1;
	public int defense = 1;
	public int sDefense = 1;
	public int attack = 1;
	public int sAttack = 1;
	
	public int healthIV = 1;
	public int speedIV = 1;
	public int defenseIV = 1;
	public int sDefenseIV = 1;
	public int attackIV = 1;
	public int sAttackIV = 1;
	
	public int healthEV = 0;
	public int speedEV = 0;
	public int defenseEV = 0;
	public int sDefenseEV = 0;
	public int attackEV = 0;
	public int sAttackEV = 0;
	*/
	
	public int numOfAttacks = 0;
	//This array is manually done out as to make sure they are "null" type moves, to prevent errors
	public Moves[] moves = new Moves[]{Moves.NULL, Moves.NULL, Moves.NULL, Moves.NULL};
	public int[] PP = new int[4];
	
	public Player(IUser user){
		//Set the default values
		for(int y = 0; y < 6; y++){
			for(int x = 0; x < 2; x++){ //we skip the EV's
				this.stats[y][x] = 1;
			}
		}
		this.user = user;
		this.loadData();
		this.HP = this.getMaxHP();
		for(int x = 0; x < this.numOfAttacks; x++){
			this.PP[x] = this.moves[x].getPP();
		}
	}
	
	public IUser getUser(){
		return this.user;
	}
	
	public boolean hasSecondaryType(){
		return this.secondary != Types.NULL;
	}
	
	public int getMaxHP(){
		return StatHandler.calcStatValue(this.stats[Stats.HEALTH.getIndex()][SubStats.BASE.getIndex()], this.stats[Stats.HEALTH.getIndex()][SubStats.IV.getIndex()], this.stats[Stats.HEALTH.getIndex()][SubStats.EV.getIndex()], this.level, true);
	}
	
	public int getAttackStat(){
		return StatHandler.calcStatValue(this.stats[Stats.ATTACK.getIndex()][SubStats.BASE.getIndex()], this.stats[Stats.ATTACK.getIndex()][SubStats.IV.getIndex()], this.stats[Stats.ATTACK.getIndex()][SubStats.EV.getIndex()], this.level, false);
	}
	
	public int getSpecialAttackStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.BASE.getIndex()], this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.IV.getIndex()], this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.EV.getIndex()], this.level, false);
	}
	
	public int getDefenseStat(){
		return StatHandler.calcStatValue(this.stats[Stats.DEFENSE.getIndex()][SubStats.BASE.getIndex()], this.stats[Stats.DEFENSE.getIndex()][SubStats.IV.getIndex()], this.stats[Stats.DEFENSE.getIndex()][SubStats.EV.getIndex()], this.level, false);
	}
	
	public int getSpecialDefenseStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.BASE.getIndex()], this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.IV.getIndex()], this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.EV.getIndex()], this.level, false);
	}
	
	public int getSpeedStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPEED.getIndex()][SubStats.BASE.getIndex()], this.stats[Stats.SPEED.getIndex()][SubStats.IV.getIndex()], this.stats[Stats.SPEED.getIndex()][SubStats.EV.getIndex()], this.level, false);
	}
	
	public void loadData(){
		System.out.println("Loading");
		File file = Pokebot.getSaveFile(this.user);
		if(!file.exists()) return; //Use defaults
		try(Scanner in = new Scanner(new FileInputStream(file))){
			this.primary = Types.valueOf(in.nextLine());
			this.secondary = Types.valueOf(in.nextLine());
			
			this.stats[Stats.HEALTH.getIndex()][SubStats.BASE.getIndex()] = in.nextInt();
			this.stats[Stats.ATTACK.getIndex()][SubStats.BASE.getIndex()] = in.nextInt();
			this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.BASE.getIndex()] = in.nextInt();
			this.stats[Stats.DEFENSE.getIndex()][SubStats.BASE.getIndex()] = in.nextInt();
			this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.BASE.getIndex()] = in.nextInt();
			this.stats[Stats.SPEED.getIndex()][SubStats.BASE.getIndex()] = in.nextInt();
			
			this.stats[Stats.HEALTH.getIndex()][SubStats.IV.getIndex()] = in.nextInt();
			this.stats[Stats.ATTACK.getIndex()][SubStats.IV.getIndex()] = in.nextInt();
			this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.IV.getIndex()] = in.nextInt();
			this.stats[Stats.DEFENSE.getIndex()][SubStats.IV.getIndex()] = in.nextInt();
			this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.IV.getIndex()] = in.nextInt();
			this.stats[Stats.SPEED.getIndex()][SubStats.IV.getIndex()] = in.nextInt();
			
			this.stats[Stats.HEALTH.getIndex()][SubStats.EV.getIndex()] = in.nextInt();
			this.stats[Stats.ATTACK.getIndex()][SubStats.EV.getIndex()] = in.nextInt();
			this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.EV.getIndex()] = in.nextInt();
			this.stats[Stats.DEFENSE.getIndex()][SubStats.EV.getIndex()] = in.nextInt();
			this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.EV.getIndex()] = in.nextInt();
			this.stats[Stats.SPEED.getIndex()][SubStats.EV.getIndex()] = in.nextInt();
			
			this.numOfAttacks = in.nextInt();
			in.nextLine(); //nextInt tends to leave over the \n, it seems
			for(int x = 0; x < this.moves.length; x++){
				this.moves[x] = Moves.valueOf(in.nextLine());
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
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
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		try(PrintStream out = new PrintStream(file)){
			out.println(this.primary.toString());
			out.println(this.secondary.toString());
			
			out.println(this.stats[Stats.HEALTH.getIndex()][SubStats.BASE.getIndex()]);
			out.println(this.stats[Stats.ATTACK.getIndex()][SubStats.BASE.getIndex()]);
			out.println(this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.BASE.getIndex()]);
			out.println(this.stats[Stats.DEFENSE.getIndex()][SubStats.BASE.getIndex()]);
			out.println(this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.BASE.getIndex()]);
			out.println(this.stats[Stats.SPEED.getIndex()][SubStats.BASE.getIndex()]);
			
			out.println(this.stats[Stats.HEALTH.getIndex()][SubStats.IV.getIndex()]);
			out.println(this.stats[Stats.ATTACK.getIndex()][SubStats.IV.getIndex()]);
			out.println(this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.IV.getIndex()]);
			out.println(this.stats[Stats.DEFENSE.getIndex()][SubStats.IV.getIndex()]);
			out.println(this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.IV.getIndex()]);
			out.println(this.stats[Stats.SPEED.getIndex()][SubStats.IV.getIndex()]);
			
			out.println(this.stats[Stats.HEALTH.getIndex()][SubStats.EV.getIndex()]);
			out.println(this.stats[Stats.ATTACK.getIndex()][SubStats.EV.getIndex()]);
			out.println(this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.EV.getIndex()]);
			out.println(this.stats[Stats.DEFENSE.getIndex()][SubStats.EV.getIndex()]);
			out.println(this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.EV.getIndex()]);
			out.println(this.stats[Stats.SPEED.getIndex()][SubStats.EV.getIndex()]);
			
			out.println(this.numOfAttacks);
			for(int x = 0; x < this.moves.length; x++){
				out.println(this.moves[x].toString());
			}
			out.flush();
			out.close();
			System.out.println(Pokebot.getSaveFile(this.user).getAbsolutePath());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
