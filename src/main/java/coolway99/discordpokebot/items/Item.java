package coolway99.discordpokebot.items;


import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

import java.util.TreeMap;

/**
 * This class would not be possible without Pokemon Showdown's code.
 * Please go thank them. https://github.com/Zarel/Pokemon-Showdown
 */
public class Item{

	protected static final TreeMap<String, Item> REGISTRY = new TreeMap<>((o1, o2) -> {
		try{
			char c1, c2;
			for(int x = 0; x < o2.length(); x++){
				c1 = o1.charAt(x);
				c2 = o2.charAt(x);
				if(c1 == c2) continue;
				return c1 - c2;
			}
			if(o1.length() > o2.length()) return 1;
			return 0;
		} catch(IndexOutOfBoundsException e){
			return -1;
		}
	});

	private final int cost;
	private final ItemTypes itemType;
	private final String displayName;
	private final String name;

	private final int flingPower;
	private final int naturalGiftPower;
	private final Types naturalGiftType;

	private final MoveMethod onMove;
	private final DamageModMethod onAttack;
	private final DamageMethod onAfterDamage;
	private final TriMethod onPostTurn;

	protected Item(ItemTypes itemType, int cost, String name, String displayName,
				   int flingPower, int naturalGiftPower, Types naturalGiftType,
				   MoveMethod onMove, DamageModMethod onAttack, DamageMethod onAfterDamage, TriMethod onPostTurn){
		this.itemType = itemType;
		this.cost = cost;
		this.name = name;
		this.displayName = displayName;
		this.flingPower = flingPower;
		this.naturalGiftPower = naturalGiftPower;
		this.naturalGiftType = naturalGiftType;
		this.onMove = onMove;
		this.onAttack = onAttack;
		this.onAfterDamage = onAfterDamage;
		this.onPostTurn = onPostTurn;
	}

	public ItemTypes getItemType(){
		return this.itemType;
	}

	public String getName(){
		return this.name;
	}

	public String getDisplayName(){
		return this.displayName;
	}

	public MoveSet onMove(IChannel channel, Player attacker, MoveSet move, Player defender){
		return this.onMove.run(channel, attacker, move, defender, this);
	}

	public int onAttack(IChannel channel, Player attacker, Move move, Player defender, int damage){
		return this.onAttack.run(channel, attacker, move, defender, damage, this);
	}

	public void onAfterDamage(IChannel channel, Player attacker, Move move, Player defender, int damage){
		this.onAfterDamage.run(channel, attacker, move, defender, damage, this);
	}

	public void onPostTurn(IChannel channel, Player player){
		this.onPostTurn.run(channel, player, this);
	}

	public int getFlingPower(){
		return this.flingPower;
	}

	public int getNaturalGiftPower(){
		return this.naturalGiftPower;
	}

	public Types getNaturalGiftType(){
		return this.naturalGiftType;
	}

	public boolean canConsume(Player player){
		if(player.getModifiedItem() == this){
			player.modifiedItem = null;
			return true;
		}
		return false;
	}

	@FunctionalInterface
	public interface MoveMethod{
		MoveSet run(IChannel channel, Player attacker, MoveSet move, Player defender, Item item);
	}

	@FunctionalInterface
	public interface DamageMethod{
		void run(IChannel channel, Player attacker, Move move, Player defender, int damage, Item item);
	}

	@FunctionalInterface
	public interface DamageModMethod{
		int run(IChannel channel, Player attacker, Move move, Player defender, int damage, Item item);
	}

	@FunctionalInterface
	public interface TriMethod{
		void run(IChannel channel, Player player, Item item);
	}

	public static void registerItems(){
		System.out.println("Registering items");
		ItemBuilder.newItem(30, "ABSORB_BULB", "Absorb Bulb")
				.onAfterDamage((channel, attacker, move, defender, damage, item) -> {
					if(move.getType(attacker) == Types.WATER){
						//attacker.itemConsumed = true;
						StatHandler.changeStat(channel, attacker, Stats.SPECIAL_ATTACK, 1);
					}
				})
				.register();
		ItemBuilder.newItem(50, "AGUAV_BERRY", "Aguav Berry")
				.isHealingBerry(Stats.SPECIAL_DEFENSE, 80, Types.DRAGON)
				.register();
		/* TODO REGISTRY.put("AIR_BALLOON", new AbstractFlingableItem(50, 10){
			@Override
			public void onAfterDamage(IChannel channel, Player attacker, Move move, Player defender, int damage){

			}
		});*/
		ItemBuilder.newItem(60, "APICOT_BERRY", "Apicot Berry")
				.isStatusBerry(Stats.SPECIAL_DEFENSE, 100, Types.GROUND)
				.register();
		//I'm not doing items that have no purpose inside of battle/their only purpose is fling
		ItemBuilder.newItem(50, "ASPEAR_BERRY", "Aspear Berry")
				.isCureBerry(Effects.NonVolatile.FROZEN, 80, Types.ICE)
				.register();
		ItemBuilder.newItem(100, "ASSAULT_VEST", "Assault Vest")
				.fling(80)
				.onMove((channel, attacker, move, defender, item) -> {
					if(move.getMove().getMoveType() == Move.MoveType.STATUS) return null;
					return move;
				})
				//TODO mod special defence
				.register();
		//TODO ItemBuilder.newItem(50, "BABIRI_BERRY", "Babiri Berry")
				//.
		//Mega stones are not applicatable
		System.out.println("Done registering items");
	}

	public enum ItemTypes{
		ITEM,
		BERRY
	}
}
	/*NULL(),
	BLACK_BELT(Types.FIGHTING),
	BLACK_GLASSES(Types.DARK),
	CHARCOAL(Types.FIRE),
	DRAGON_FANG(Types.DRAGON),
	HARD_STONE(Types.ROCK),
	MAGNET(Types.ELECTRIC),
	METAL_COAT(Types.STEEL),
	MIRACLE_SEED(Types.GRASS),
	MYSTIC_WATER(Types.WATER),
	//Had to change it's name slightly because java
	@SuppressWarnings("SpellCheckingInspection")
	NEVERMELT_ICE(Types.ICE),
	// Only 10% PINK_BOW(Types.NORMAL),
	POISON_BARB(Types.POISON),
	// Only 12.5% Polkadot Bow(Types.NORMAL),
	SHARP_BEAK(Types.FLYING),
	SILK_SCARF(Types.NORMAL),
	SILVER_POWDER(Types.BUG),
	SOFT_SAND(Types.GROUND),
	SPELL_TAG(Types.GHOST),
	TWISTED_SPOON(Types.PSYCHIC),

	;

	private final Types type;

	Items(){
		this.type = null;
	}

	Items(Types type){
		this.type = type;
	}

	public Types getPoweredUpType(){
		return this.type;
	}*/
