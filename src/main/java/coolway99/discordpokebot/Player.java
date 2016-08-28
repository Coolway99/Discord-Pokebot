package coolway99.discordpokebot;

import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.states.Natures;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Scanner;

//TODO stuff
//The variables all contain the default states
public class Player{
	
	public final IUser user;
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
	public final int[][] stats = new int[6][3];
	
	/**
	 * An array holding the modifiers for each stat
	 */
	public final byte[] modifiers = new byte[8];
	/**
	 * The nature of the player
	 */
	public Natures nature = Natures.values()[Pokebot.ran.nextInt(Natures.values().length)];
	/**
	 * The ability of the player
	 */
	private Abilities ability = Abilities.MC_NORMAL_PANTS;
	/**
	 * What nonvolatile effect is the player currently under?
	 */
	private Effects.NonVolatile nvEffect = Effects.NonVolatile.NORMAL;
	/**
	 * What volatile effects does the player have?
	 */
	private final EnumSet<Effects.Volatile> vEffects;
	/**
	 * What battle-effects does the player have?
	 */
	private final EnumSet<Effects.VBattle> battleEffects;
	
	public int numOfAttacks = 0;
	//This array is manually done out as to make sure they are "null" type moves, to prevent errors
	public final Move[] moves = new Move[]{Move.NULL, Move.NULL, Move.NULL, Move.NULL};
	public final int[] PP = new int[4];
	
	public Battle battle = null;
	public Move lastMove = Move.NULL; //Isn't set outside of a battle
	public int lastMoveData = 0; //Can be used by moves for whatever they want, only used in battles
	public Player lastTarget = null; //Only set in-battle. Null if there wasn't a target
	public Player lastAttacker = null; //Only set in-battle. Null if there wasn't an attacker
	public int counter = 0; //Used for Toxic, Sleep and Freeze
	
	public Player(IUser user){
		this.user = user;
		this.loadData();
		this.HP = this.getMaxHP();
		for(int x = 0; x < this.numOfAttacks; x++){
			this.PP[x] = this.moves[x].getPP();
		}
		this.vEffects = EnumSet.noneOf(Effects.Volatile.class);
		this.battleEffects = EnumSet.noneOf(Effects.VBattle.class);
	}
	
	public boolean hasSecondaryType(){
		return this.secondary != Types.NULL;
	}
	
	public boolean inBattle(){
		return this.battle != null;
	}
	
	public void set(Effects.NonVolatile nvEffect){
		this.nvEffect = nvEffect;
	}
	
	public boolean has(Effects.NonVolatile nvEffect){
		return this.nvEffect == nvEffect;
	}
	
	public Effects.NonVolatile getNV(){
		return this.nvEffect;
	}
	
	/*public void remove(){
		this.nvEffect = Effects.NonVolatile.NORMAL;
	}*/
	
	public void cureNV(){
		this.nvEffect = Effects.NonVolatile.NORMAL;
	}
	
	public void set(Effects.Volatile vEffect){
		this.vEffects.add(vEffect);
	}
	
	public boolean has(Effects.Volatile vEffect){
		return this.vEffects.contains(vEffect);
	}
	
	public EnumSet<Effects.Volatile> getV(){
		return this.vEffects;
	}
	
	public void remove(Effects.Volatile vEffect){
		this.vEffects.remove(vEffect);
	}
	
	public void set(Effects.VBattle battleEffect){
		this.battleEffects.add(battleEffect);
	}
	
	public boolean has(Effects.VBattle battleEffect){
		return this.battleEffects.contains(battleEffect);
	}
	
	public EnumSet<Effects.VBattle> getVB(){
		return this.battleEffects;
	}
	
	public void remove(Effects.VBattle battleEffect){
		this.battleEffects.remove(battleEffect);
	}
	
	public void removeAllEffects(){
		this.cureNV();
		this.vEffects.clear();
		this.battleEffects.clear();
	}
	
	public Abilities getAbility(){
		return this.ability;
	}
	
	public Abilities getModifiedAbility(){
		if(this.has(Effects.VBattle.ABILITY_BLOCK)) return Abilities.MC_NORMAL_PANTS;
		return this.ability;
	}

	public boolean hasAbility(Abilities ability){
		return ability == this.getModifiedAbility();
	}
	
	public void setAbility(Abilities ability){
		this.ability = ability;
	}

	public int getMaxHP(){
		return StatHandler.calcStatValue(this.stats[Stats.HEALTH.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.HEALTH.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.HEALTH.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.HEALTH,
				this.modifiers[Stats.HEALTH.getIndex()],
				this.nvEffect,
				this.nature);
	}
	
	public int getAttackStat(){
		return StatHandler.calcStatValue(this.stats[Stats.ATTACK.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.ATTACK.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.ATTACK.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.ATTACK,
				this.modifiers[Stats.ATTACK.getIndex()],
				this.nvEffect,
				this.nature);
	}
	
	public int getSpecialAttackStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.SPECIAL_ATTACK.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.SPECIAL_ATTACK,
				this.modifiers[Stats.SPECIAL_ATTACK.getIndex()],
				this.nvEffect,
				this.nature);
	}
	
	public int getDefenseStat(){
		return StatHandler.calcStatValue(this.stats[Stats.DEFENSE.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.DEFENSE.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.DEFENSE.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.DEFENSE,
				this.modifiers[Stats.DEFENSE.getIndex()],
				this.nvEffect,
				this.nature);
	}
	
	public int getSpecialDefenseStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.SPECIAL_DEFENSE.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.SPECIAL_DEFENSE,
				this.modifiers[Stats.SPECIAL_DEFENSE.getIndex()],
				this.nvEffect,
				this.nature);
	}
	
	public int getSpeedStat(){
		return StatHandler.calcStatValue(this.stats[Stats.SPEED.getIndex()][SubStats.BASE.getIndex()],
				this.stats[Stats.SPEED.getIndex()][SubStats.IV.getIndex()],
				this.stats[Stats.SPEED.getIndex()][SubStats.EV.getIndex()],
				this.level,
				Stats.SPEED,
				this.modifiers[Stats.SPEED.getIndex()],
				this.nvEffect,
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
	
	private void loadData(){
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
				this.moves[x] = Move.valueOf(in.nextLine());
			}
			this.level = in.nextInt();
			in.nextLine();
			this.nature = Natures.valueOf(in.nextLine());
			this.ability = Abilities.valueOf(in.nextLine());
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(NoSuchElementException e){
			System.err.println("We read from an incomplete or invalid file");
		}
	}
	
	public boolean hasMove(Move move){
		for(Move hasMove : this.moves){
			if(hasMove == move) return true;
		}
		return false;
	}
	
	public String mention(){
		return this.user.mention();
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
			//If this doesn't guarantee an order, then this will cause issues
			for(int[] stats : this.stats){
				for(int stat : stats){
					out.println(stat);
				}
			}

			out.println(this.numOfAttacks);
			for(Move move : this.moves){
				out.println(move.toString());
			}
			out.println(this.level);
			out.println(this.nature);
			out.println(this.ability);
			out.flush();
			out.close();
			System.out.println(Pokebot.getSaveFile(this.user).getAbsolutePath());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Player){
			return this.user.getID().equals(((Player) obj).user.getID());
		}
		return false;
	}
}