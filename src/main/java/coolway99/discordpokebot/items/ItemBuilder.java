package coolway99.discordpokebot.items;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.Types;
import sx.blah.discord.handle.obj.IChannel;

public class ItemBuilder{

	private Item.ItemTypes itemType = Item.ItemTypes.ITEM;
	private final int cost;
	private String name;
	private String displayName;

	private int flingPower = 0;
	private int naturalGiftPower = 0;
	private Types naturalGiftType = Types.NULL;

	private Item.MoveMethod onMove = (a, b, moveSet, d, e) -> moveSet;
	private Item.DamageMethod afterDamage = (a, b, c, d, e, f) -> {};


	//public static ItemBuilder newItem(int cost, String name){
	//	return newItem(cost, name, name.replaceAll("_", " "));
	//}

	public static ItemBuilder newItem(int cost, String name, String displayName){
		return new ItemBuilder(cost, name, displayName);
	}

	private ItemBuilder(int cost, String name, String displayName){
		this.cost = cost;
		this.name = name;
		this.displayName = displayName;
	}

	public ItemBuilder setItemType(Item.ItemTypes itemType){
		this.itemType = itemType;
		return this;
	}

	public ItemBuilder fling(int basePower){
		this.flingPower = basePower;
		return this;
	}

	public ItemBuilder naturalGift(int basePower, Types type){
		this.naturalGiftPower = basePower;
		this.naturalGiftType = type;
		return this;
	}

	public ItemBuilder onMove(Item.MoveMethod onMove){
		this.onMove = onMove;
		return this;
	}

	public ItemBuilder onAfterDamage(Item.DamageMethod afterDamage){
		this.afterDamage = afterDamage;
		return this;
	}

	public ItemBuilder isHealingBerry(Stats stat, int naturalGiftPower, Types naturalGiftType/*, boolean appendBerryName*/){
		/*if(appendBerryName){
			this.name += "_BERRY";
			this.displayName += " Berry";
		}*/
		this.naturalGift(naturalGiftPower, naturalGiftType);
		this.onAfterDamage(new HealingBerry(stat));
		this.setItemType(Item.ItemTypes.BERRY);
		return this;
	}

	public Item register(){
		Item item = new Item(this.itemType, this.cost, this.name, this.displayName,
				this.flingPower, this.naturalGiftPower, this.naturalGiftType,
				this.onMove, this.afterDamage);
		Item.REGISTRY.put(this.name, item);
		return item;
	}

	private static class HealingBerry implements Item.DamageMethod{

		public final Stats negative;

		private HealingBerry(Stats negative){
			this.negative = negative;
		}

		@Override
		public void run(IChannel channel, Player attacker, Move move, Player defender, int damage, Item item){
			if(attacker.HP < attacker.getMaxHP()/2 && !attacker.has(Effects.Volatile.HEAL_BLOCK) && item.canConsume(attacker)){
				int heal = attacker.getMaxHP()/8;
				attacker.HP = Math.min(heal, attacker.getMaxHP());
				Pokebot.sendMessage(channel, attacker.mention()+" recovered "+heal+" using their "+item.getDisplayName()+"!");
				//TODO if(this.negative == attacker.nature.decrease) Move.confuse(attacker);
			}
		}
	}
}
