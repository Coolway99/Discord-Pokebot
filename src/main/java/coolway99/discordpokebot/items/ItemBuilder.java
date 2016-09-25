package coolway99.discordpokebot.items;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.StatHandler;
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
	private Item.DamageMethod onAfterDamage = (a, b, c, d, e, f) -> {};
	private Item.TriMethod onPostTurn = (a, b, c) -> {};


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

	public ItemBuilder onAfterDamage(Item.DamageMethod onAfterDamage){
		this.onAfterDamage = onAfterDamage;
		return this;
	}

	public ItemBuilder onPostTurn(Item.TriMethod onPostTurn){
		this.onPostTurn = onPostTurn;
		return this;
	}

	public ItemBuilder isBerry(int naturalGiftPower, Types naturalGiftType){
		this.naturalGift(naturalGiftPower, naturalGiftType);
		this.setItemType(Item.ItemTypes.BERRY);
		return this;
	}

	public ItemBuilder isHealingBerry(Stats stat, int naturalGiftPower, Types naturalGiftType/*, boolean appendBerryName*/){
		/*if(appendBerryName){
			this.name += "_BERRY";
			this.displayName += " Berry";
		}*/
		this.isBerry(naturalGiftPower, naturalGiftType);
		this.onAfterDamage(new HealingBerry(stat));
		return this;
	}

	public ItemBuilder isStatusBerry(Stats stat, int naturalGiftPower, Types naturalGiftType){
		this.isBerry(naturalGiftPower, naturalGiftType);
		this.onPostTurn(new StatusBerry(stat));
		return this;
	}

	public ItemBuilder isCureBerry(Effects.NonVolatile effect, int naturalGiftPower, Types naturalGiftType){
		this.isBerry(naturalGiftPower, naturalGiftType);
		this.onPostTurn(new CureBerry(effect));
		return this;
	}

	public Item register(){
		Item item = new Item(this.itemType, this.cost, this.name, this.displayName,
				this.flingPower, this.naturalGiftPower, this.naturalGiftType,
				this.onMove, this.onAfterDamage, this.onPostTurn);
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
			if(defender.HP < defender.getMaxHP()/2 && !defender.has(Effects.Volatile.HEAL_BLOCK) && item.canConsume(defender)){
				int heal = defender.getMaxHP()/8;
				defender.HP = Math.min(heal, defender.getMaxHP());
				Pokebot.sendMessage(channel, defender.mention()+" recovered "+heal+" using their "+item.getDisplayName()+"!");
				//TODO if(this.negative == defender.nature.decrease) Move.confuse(defender);
			}
		}
	}

	private static class StatusBerry implements Item.TriMethod{

		public final Stats raisedStat;

		private StatusBerry(Stats raisedStat){
			this.raisedStat = raisedStat;
		}

		@Override
		public void run(IChannel channel, Player player, Item item){
			if(player.HP < player.getMaxHP()/4 /*TODO GLUTTONY*/ && item.canConsume(player)){
				StatHandler.changeStat(channel, player, this.raisedStat, 1);
			}
		}
	}

	private static class CureBerry implements Item.TriMethod{

		private Effects.NonVolatile effect;

		private CureBerry(Effects.NonVolatile effect){
			this.effect = effect;
		}

		@Override
		public void run(IChannel channel, Player player, Item item){
			if(player.has(this.effect) && item.canConsume(player)){
				player.cureNV();
				Pokebot.sendMessage(channel, player.mention()+" got cured of "+this.effect+" using their "+item.getDisplayName());
			}
		}
	}
}
