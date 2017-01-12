package coolway99.discordpokebot;

import coolway99.discordpokebot.abilities.AbilityAPI;
import coolway99.discordpokebot.abilities.AbilityWrapper;
import coolway99.discordpokebot.battles.Battle;
import coolway99.discordpokebot.items.ItemAPI;
import coolway99.discordpokebot.items.ItemWrapper;
import coolway99.discordpokebot.moves.MoveFlags;
import coolway99.discordpokebot.moves.MoveWrapper;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.moves.MoveAPI;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Natures;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
import coolway99.discordpokebot.states.Types;
import coolway99.discordpokebot.storage.PlayerHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;
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

	public final byte slot;
	
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
	 * An array holding the modifiers for each stat. Health is slot 0, but isn't ever used
	 */
	public final byte[] modifiers = new byte[8];
	/**
	 * The nature of the player
	 */
	public Natures nature = Natures.values()[Pokebot.ran.nextInt(Natures.values().length)];
	/**
	 * The ability of the player, null means no ability
	 */
	@NotNull
	private AbilityWrapper ability = AbilityAPI.getDefaultAbility();
	/**
	 * The default item of the player
	 */
	@NotNull
	private ItemWrapper item = ItemAPI.getDefaultItem();
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

	//public final Move[] moves = new Move[]{Move.NULL, Move.NULL, Move.NULL, Move.NULL};
	public final MoveSet[] moves;
	//public Item heldItem = null;

	public Battle battle = null;
	public MoveSet lastMove = null; //Isn't set outside of a battle
	public int lastMoveData = 0; //Can be used by moves for whatever they want, only used in battles
	public Player lastTarget = null; //Only set in-battle. Null if there wasn't a target
	public Player lastAttacker = null; //Only set in-battle. Null if there wasn't an attacker
	@NotNull
	public ItemWrapper modifiedItem = ItemAPI.getDefaultItem(); //This is instantiated during battle-time

	public int counter = 0; //Used for Toxic, Sleep and Freeze

	public Player(IUser user){
		this(user, (byte) 0);
	}
	
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

	public boolean hasType(Types type){
		return this.primary == type || this.secondary == type;
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

	@NotNull
	public AbilityWrapper getAbility(){
		return this.ability;
	}

	//TODO perhaps have the battle keep track of this
	@NotNull
	public AbilityWrapper getModifiedAbility(){
		if(this.has(Effects.VBattle.ABILITY_BLOCK)) return AbilityAPI.getDefaultAbility();
		return this.ability;
	}

	public boolean hasAbility(AbilityWrapper ability){
		return ability == this.getModifiedAbility();
	}

	public void setAbility(@NotNull AbilityWrapper ability){
		this.ability = ability;
	}

	@NotNull
	public ItemWrapper getItem(){
		return this.item;
	}

	@NotNull
	public ItemWrapper getModifiedItem(){
		return this.modifiedItem;
	}

	@Contract(value = "null -> false", pure = true)
	public boolean hasItem(ItemWrapper item){
		return item == this.modifiedItem;
	}

	public void setItem(@NotNull ItemWrapper item){
		this.item = item;
	}

	public void usedItem(){
		this.modifiedItem = ItemAPI.getDefaultItem();
	}

	/*public Item getItem(){
		return this.heldItem;
	}

	/**
	 * A helper method to consume the item
	 * @return The item that was consumed, or null if there was no item to consume
	 *//*
	public Item consumeItem(){
		Item item = this.modifiedItem;
		this.modifiedItem = null;
		return item;
	}

	public Item getModifiedItem(){
		return this.modifiedItem;
	}

	public boolean hasItem(Item item){
		return this.getModifiedItem() == item; //This can be done because "items" are singleton
	}

	public void setItem(Item item){
		this.heldItem = item;
	}*/

	public int getMaxHP(){
		return this.getStat(Stats.HEALTH);
	}

	public int getAttackStat(){
		return this.getStat(Stats.ATTACK);
	}

	public int getSpecialAttackStat(){
		return this.getStat(Stats.SPECIAL_ATTACK);
	}

	public int getDefenseStat(){
		return this.getStat(Stats.DEFENSE);
	}

	public int getSpecialDefenseStat(){
		return this.getStat(Stats.SPECIAL_DEFENSE);
	}

	public int getSpeedStat(){
		return this.getStat(Stats.SPEED);
	}

	public double getAccuracy(){
		return StatHandler.getHitModifierChange(this.modifiers[Stats.ACCURACY.getIndex()]);
	}

	public double getEvasion(){
		return StatHandler.getModifierChange(this.modifiers[Stats.EVASION.getIndex()]);
	}

	public int getStat(Stats stat){
		return StatHandler.calcStatValue(
				this.stats[stat.getIndex()][SubStats.BASE.getIndex()],
				this.stats[stat.getIndex()][SubStats.IV.getIndex()],
				this.stats[stat.getIndex()][SubStats.EV.getIndex()],
				this.level,
				stat,
				this.modifiers[stat.getIndex()],
				this.nvEffect,
				this.nature);
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

	public void modifyStat(IChannel channel, Stats stat, int amount){
		StatHandler.changeStat(channel, this, stat, amount);
	}

	@Contract(pure = true)
	public int damage(int amount){
		int before = this.HP;
		if(amount <= 0) return 0;
		if(this.HP == this.getMaxHP() && this.HP - amount <= 0){
			//TODO OHKO prevention
		}
		this.HP -= amount;
		if(this.HP <= 0){
			this.HP = 0;
			this.set(Effects.NonVolatile.FAINTED);
		}
		return before - this.HP;
	}

	@Contract(pure = true)
	public int damage(@NotNull IChannel channel, int amount){
		if(amount <= 0){
			Pokebot.sendMessage(channel, this.mention()+" didn't take any damage...");
			return 0;
		}
		int max = this.getMaxHP();
		int before = this.HP;
		int dam = this.damage(amount);
		if(before == max && this.HP == 0){
			Messages.oneHitKO(channel, this);
		} else {
			Messages.tookDamage(channel, this, amount);
		}
		if(this.HP == 0){
			Messages.fainted(channel, this);
			this.set(Effects.NonVolatile.FAINTED);
		}
		return dam;
	}

	@Contract(pure = true)
	public int damage(float amount){
		if(amount <= 0) return 0;
		//Implicit Math.floor
		return this.damage((int) (this.getMaxHP()* amount));
	}

	@Contract(pure = true)
	public int damage(@NotNull IChannel channel, float amount){
		if(amount <= 0) return this.damage(channel, 0);
		//Implicit Math.floor
		return this.damage(channel, (int) (this.getMaxHP()* amount));
	}

	public boolean hasMove(MoveWrapper move){
		for(MoveSet set : this.moves){
			if(set == null) continue;
			if(set.getMove() == move) return true;
		}
		return false;
	}

	public boolean doesLastMoveHave(MoveFlags flag){
		return this.lastMove != null && this.lastMove.getMove().has(flag);
	}

	public String mention(){
		return this.user.mention();
	}

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
				MoveWrapper move = MoveAPI.getMove(in.nextLine());
				if(move == null){
					this.moves[x] = null;
					continue;
				}
				this.moves[x] = new MoveSet(move);
			}
			this.compressMoves();
			this.level = in.nextInt();
			in.nextLine();
			this.nature = Natures.valueOf(in.nextLine());
			this.ability = AbilityAPI.getAbility(in.nextLine());
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(NoSuchElementException | NullPointerException e){
			System.err.println("We read from an incomplete or invalid file");
		}
	}

	public void saveData(){
		System.out.println("Beginning to save");
		File file = Pokebot.getSaveFile(this.user, this.slot);
		if(!file.exists() && file.getParentFile() != null){
			file.getParentFile().mkdirs();
		}
		try(PrintStream out = new PrintStream(file)){
			out.println(this.primary);
			out.println(this.secondary);
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

	public void compressMoves(){
		for(int x = this.moves.length-1; x > 0; x--){
			if(this.moves[x-1] == null || this.moves[x-1].getMove() == null){
				this.moves[x-1] = this.moves[x];
				this.moves[x] = null;
			}
		}
		this.numOfAttacks = 0;
		for(MoveSet move : this.moves){
			//noinspection VariableNotUsedInsideIf
			if(move != null) this.numOfAttacks++;
		}
	}

	/*
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Player){
			return this.user.getID().equals(((Player) obj).user.getID());
		}
		return false;
	}*/
}