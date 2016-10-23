package coolway99.discordpokebot;

import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.states.Natures;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
import coolway99.discordpokebot.states.Types;
import coolway99.discordpokebot.storage.PlayerHandler;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * The class for "players". This class can be thought of as a wrapper around IUser, adding on everything needed to make
 * that IUser a pokemon.
 * <br /><br />
 * Most variables that can are initalized with their default value. That way, {@link #loadData() loading data} will contain
 * the defaults for anything unitialized, helps with new and outdated pokemon
 *
 * @author Coolway99
 */
//TODO stuff
//The variables all contain the default states
public class Player{
	/**
	 * The "slot" of the player. Slots are a final unique identifer that lets each user have different configuratons
	 * @see PlayerHandler#switchSlot(IUser, byte)
	 */
	public final byte slot;
	/**
	 * The user that this player belongs too. Used for keeping track of who's who.
	 * The main use of IUser is {@link IUser#getID()}, which is how this bot keeps track
	 */
	public final IUser user;

	/**
	 * The primary type of this player, cannot be null or {@link Types#NULL}.
	 * This is {@link Types#NORMAL} by default
	 */
	public Types primary = Types.NORMAL;
	/**
	 * The secondary type of this player, cannot be null but can be {@link Types#NULL}.
	 * This is {@link Types#NULL} by default
	 */
	public Types secondary = Types.NULL;
	/**
	 * The current HP of the player. This is the current health of the player, and if it drops <= 0 then the player faints
	 * This is initalized to 0 by default
	 * @see #getMaxHP()
	 */
	public int HP = 0;
	/**
	 * The current level of the player. This is set to the max by default
	 * @see StatHandler#MAX_LEVEL
	 */
	public int level = StatHandler.MAX_LEVEL;

	//Physical Stats
	/**
	 * An array containing all the raw stats of the player, they go in this order:<br />
	 * Health<br />
	 * Attack<br />
	 * Special Attack<br />
	 * Defense<br />
	 * Special Defense<br />
	 * Speed<br />
	 * <br />
	 * Additionally, there are also 3 subStats for each:<br />
	 * Base<br />
	 * IV<br />
	 * EV<br />
	 *
	 * @see StatHandler
	 * @see Stats
	 * @see SubStats
	 */
	public final int[][] stats = new int[6][3];
	
	/**
	 * An array holding the modifiers for each stat. Health is slot 0, but isn't ever used<br />
	 * The modifiers go in this order:<br />
	 * Health (unused)<br />
	 * Attack<br />
	 * Special Attack<br />
	 * Defense<br />
	 * Special Defense<br />
	 * Speed<br />
	 * Accuracy<br />
	 * Evasion
	 *
	 * @see StatHandler#getModifierChange(byte)
	 * @see Stats
	 */
	public final byte[] modifiers = new byte[8];
	/**
	 * The nature of the player. This can affect Attack through Speed by 90%, 100%, or 110%
	 * @see Natures
	 * @see Stats
	 * @see Natures#getStatMultiplier(Stats)
	 */
	public Natures nature = Natures.values()[Pokebot.ran.nextInt(Natures.values().length)];
	/**
	 * The ability of the player. This can be "modified" in the middle of battle, but this is the original value
	 * @see Abilities
	 * @see #getModifiedAbility()
	 */
	private Abilities ability = Abilities.MC_NORMAL_PANTS;
	/**
	 * What nonvolatile effect is the player currently under? This is the variable for Status(Nonvolatile) Effects
	 * @see Effects.NonVolatile
	 */
	private Effects.NonVolatile nvEffect = Effects.NonVolatile.NORMAL;
	/**
	 * What volatile effects does the player have? This is the variable for Volatile Effects
	 * @see Effects.Volatile
	 */
	private final EnumSet<Effects.Volatile> vEffects;
	/**
	 * What battle-effects does the player have? This is the variable for Volatile Battle Effects
	 * @see Effects.VBattle
	 */
	private final EnumSet<Effects.VBattle> battleEffects;

	/**
	 * How many attacks does this player have? This is automatically kept track of. Should not be greater than 4, ever
	 */
	public int numOfAttacks = 0;

	/**
	 * Contains the list of Moves the player has, as well as their PP.
	 * @see Move
	 * @see MoveSet
	 */
	public final MoveSet[] moves;

	/**
	 * What {@link Battle} is this player currently in? Set to null outside of battle
	 */
	public Battle battle = null;
	/**
	 * What was the last move the player used inside of battle? null if invalid or outside of battle
	 */
	public MoveSet lastMove = null; //Isn't set outside of a battle
	/**
	 * What was the last move data for the last move used? Used for multiturn attacks. 0 if nothing or outside of battle.
	 * If this is not zero then the move is auto-queued again, without using any PP
	 */
	public int lastMoveData = 0; //Can be used by moves for whatever they want, only used in battles
	/**
	 * The last player this player targeted inside of battle. Null if there was no target
	 */
	public Player lastTarget = null; //Only set in-battle. Null if there wasn't a target
	/**
	 * The last player to attack this player inside of battle. Null if there wasn't a valid attacker
	 */
	public Player lastAttacker = null; //Only set in-battle. Null if there wasn't an attacker
	/**
	 * A counter used for Toxic, Sleep, and Freeze {@link Effects.NonVolatile} effects
	 */
	public int counter = 0; //Used for Toxic, Sleep and Freeze

	/**
	 * Assumes slot is 0, redirects to {@link #Player(IUser, byte)}
	 * @param user The user to bind this player too
	 */
	public Player(IUser user){
		this(user, (byte) 0);
	}

	/**
	 * Creates a new player, binding it to the user. The slot determines what file to read from
	 * @param user The user to bind this player too
	 * @param slot The slot to read from
	 */
	public Player(IUser user, byte slot){
		this.user = user;
		this.slot = slot;
		this.moves = new MoveSet[4];
		this.loadData();
		this.HP = this.getMaxHP();
		this.vEffects = EnumSet.noneOf(Effects.Volatile.class);
		this.battleEffects = EnumSet.noneOf(Effects.VBattle.class);

		PlayerHandler.getMainFile(this.user).lastSlot = this.slot;
	}

	/**
	 * Returns true if we have a second type ({@link #secondary} != {@link Types#NULL}
	 * @return Do we have a second type defined?
	 */
	public boolean hasSecondaryType(){
		return this.secondary != Types.NULL;
	}

	/**
	 * Are we in a battle ({@link #battle} != null)
	 * @return Are in we in a battle?
	 */
	public boolean inBattle(){
		return this.battle != null;
	}

	/**
	 * Sets a {@link Effects.NonVolatile} on this player
	 * @param nvEffect The effect to set on this player
	 */
	public void set(Effects.NonVolatile nvEffect){
		this.nvEffect = nvEffect;
	}

	/**
	 * Do we have the given {@link Effects.NonVolatile} effect?
	 * @param nvEffect The effect to compare
	 * @return Does {@link #nvEffect this.nvEffect} == the given nvEffect ?
	 */
	public boolean has(Effects.NonVolatile nvEffect){
		return this.nvEffect == nvEffect;
	}

	/**
	 * Gets the {@link Effects.NonVolatile} effect currently on the player
	 * @return The Status effect the player is currently under
	 */
	public Effects.NonVolatile getNV(){
		return this.nvEffect;
	}
	
	/*public void remove(){
		this.nvEffect = Effects.NonVolatile.NORMAL;
	}*/

	/**
	 * Cures the player of the {@link Effects.NonVolatile NonVolatile(Status)} effect
	 */
	public void cureNV(){
		this.nvEffect = Effects.NonVolatile.NORMAL;
	}

	/**
	 * Sets a {@link Effects.Volatile} on this player
	 * @param vEffect The effect to set on this player
	 */
	public void set(Effects.Volatile vEffect){
		this.vEffects.add(vEffect);
	}

	/**
	 * Do we have the given {@link Effects.Volatile} effect?
	 * @param vEffect The effect to compare
	 * @return Do we have the Volatile effect?
	 */
	public boolean has(Effects.Volatile vEffect){
		return this.vEffects.contains(vEffect);
	}

	/**
	 * Get the list of {@link Effects.Volatile} effects
	 * @return A {@link EnumSet} of {@link Effects.Volatile} containing all current Volatile Effects
	 */
	public EnumSet<Effects.Volatile> getV(){
		return this.vEffects;
	}

	/**
	 * Remove the volatile effect from the list. NOP if we don't have it
	 * @param vEffect The {@link Effects.Volatile} to remove
	 */
	public void remove(Effects.Volatile vEffect){
		this.vEffects.remove(vEffect);
	}

	/**
	 * Adds a volatile Battle effect to the list. NOP if we already have it
	 * @param battleEffect The {@link Effects.VBattle} to put on the player
	 */
	public void set(Effects.VBattle battleEffect){
		this.battleEffects.add(battleEffect);
	}

	/**
	 * Do we have the given {@link Effects.VBattle} effect?
	 * @param battleEffect The effect to compare
	 * @return Do we have the VBattle effect?
	 */
	public boolean has(Effects.VBattle battleEffect){
		return this.battleEffects.contains(battleEffect);
	}

	/**
	 * Gets the list of {@link Effects.VBattle} effects
	 * @return The list of Volatile Battle effects
	 */
	public EnumSet<Effects.VBattle> getVB(){
		return this.battleEffects;
	}

	/**
	 * Removes the {@link Effects.VBattle} from the list. NOP if the player doesn't have it
	 * @param battleEffect The effect to remove
	 */
	public void remove(Effects.VBattle battleEffect){
		this.battleEffects.remove(battleEffect);
	}

	/**
	 * Removes all {@link Effects} from both lists and the Status Condition
	 */
	public void removeAllEffects(){
		this.cureNV();
		this.vEffects.clear();
		this.battleEffects.clear();
	}


	/**
	 * Gets the unmodified ability for the player
	 * @return The unmodified ability
	 */
	public Abilities getAbility(){
		return this.ability;
	}

	/**
	 * Gets the modified ability for the player.
	 * Currently it's only modified if the player has {@link Effects.VBattle.ABILITY_BLOCK}
	 * @return The modified ability for the player
	 */
	public Abilities getModifiedAbility(){
		if(this.has(Effects.VBattle.ABILITY_BLOCK)) return Abilities.MC_NORMAL_PANTS;
		return this.ability;
	}

	/**
	 * Do we have the given ability? Checks against the Modified Ability
	 * @param ability The ability to check against
	 * @return Does our modified ability match the ability?
	 */
	public boolean hasAbility(Abilities ability){
		return ability == this.getModifiedAbility();
	}

	/**
	 * Sets the unmodified ability for the player
	 * @param ability The ability to set it to
	 */
	public void setAbility(Abilities ability){
		this.ability = ability;
	}

	/**
	 * Gets the maximum HP for the player. Helper method for {@link StatHandler#calcStatValue(int, int, int, int, Stats, byte, Effects.NonVolatile, Natures)}
	 * @return The MAX HP stat for the player
	 */
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

	/**
	 * Gets the modified attack stat for the player. Helper method for {@link StatHandler#calcStatValue(int, int, int, int, Stats, byte, Effects.NonVolatile, Natures)}
	 * @return The current attack stat for the player
	 */
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

	/**
	 * Gets the modified special attack stat for the player. Helper method for
	 * {@link StatHandler#calcStatValue(int, int, int, int, Stats, byte, Effects.NonVolatile, Natures)}
	 * @return The current special attack stat for the player
	 */
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

	/**
	 * Gets the modified defense stat for the player. Helper method for
	 * {@link StatHandler#calcStatValue(int, int, int, int, Stats, byte, Effects.NonVolatile, Natures)}
	 * @return The current defense stat for the player
	 */
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

	/**
	 * Gets the modified special defense stat for the player. Helper method for
	 * {@link StatHandler#calcStatValue(int, int, int, int, Stats, byte, Effects.NonVolatile, Natures)}
	 * @return The current special defense stat for the player
	 */
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

	/**
	 * Gets the modified speed stat for the player. Helper method for
	 * {@link StatHandler#calcStatValue(int, int, int, int, Stats, byte, Effects.NonVolatile, Natures)}
	 * @return The current speed stat for the player
	 */
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

	/**
	 * Gets the modified accuracy for the player.
	 * @return The accuracy of the player
	 * @see StatHandler#getHitModifierChange(byte)
	 */
	public double getAccuracy(){
		return StatHandler.getHitModifierChange(this.modifiers[Stats.ACCURACY.getIndex()]);
	}

	/**
	 * Gets the modified evasion for the player
	 * @return The evasion of the player
	 * @see StatHandler#getModifierChange(byte)
	 */
	//TODO is this right? Shouldn't it be get HIT ModifierChange
	public double getEvasion(){
		return StatHandler.getModifierChange(this.modifiers[Stats.EVASION.getIndex()]);
	}

	/**
	 * Gets a stat from the given index
	 * @param x The index ranging from 0-5
	 * @return The stat for the index, or 0 if undefined
	 */
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

	/**
	 * Loads the data associated with the player and slot.
	 */
	private void loadData(){
		//If the file is "incomplete", which should only result when the save format is updated
		//with more info, then this will error out and close the file, with the default values
		//being intact for the values not found
		File file = Pokebot.getSaveFile(this.user, this.slot);
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
				Move move = Move.REGISTRY.get(in.nextLine());
				if(move == null){
					this.moves[x] = null;
					continue;
				}
				this.moves[x] = new MoveSet(move);
			}
			this.level = in.nextInt();
			in.nextLine();
			this.nature = Natures.valueOf(in.nextLine());
			this.ability = Abilities.valueOf(in.nextLine());
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(NoSuchElementException | NullPointerException e){
			System.err.println("We read from an incomplete or invalid file");
		}
	}

	/**
	 * Does the player have this move?
	 * @param move The move to check against
	 * @return True if the player already has the move
	 */
	//TODO move this up
	public boolean hasMove(Move move){
		for(MoveSet set : this.moves){
			if(set == null) continue;
			if(set.getMove() == move) return true;
		}
		return false;
	}

	/**
	 * Shortcut for {@link IUser#mention()}
	 * @return A string to mention the user for this player
	 */
	//TODO move this up
	public String mention(){
		return this.user.mention();
	}

	/**
	 * Saves the stored in this player
	 */
	public void saveData(){
		System.out.println("Beginning to save");
		File file = Pokebot.getSaveFile(this.user, this.slot);
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
			for(MoveSet set : this.moves){
				if(set == null){
					out.println("null");
					continue;
				}
				out.println(set.getMove().getName());
			}
			out.println(this.level);
			out.println(this.nature);
			out.println(this.ability);
			out.flush();
			out.close();
			System.out.println(Pokebot.getSaveFile(this.user, this.slot).getAbsolutePath());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * This player is equal to another if their user ids match
	 * @param obj The object to compare
	 * @return Are they equal?
	 */
	//TODO this isn't true anymore because of slots
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Player){
			return this.user.getID().equals(((Player) obj).user.getID());
		}
		return false;
	}
}