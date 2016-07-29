package coolway99.discordpokebot.battle;

import coolway99.discordpokebot.Player;

import java.util.*;

/**
 * @author Coolway
 * @since 2016-07-26
 */
@SuppressWarnings("InstanceofInterfaces")
public class BattleMap implements SortedMap<Player, IAttack>, Comparator<IAttack>{

	private final ArrayList<Player> players;
	private final ArrayList<IAttack> attacks;


	public BattleMap(){
		this.players = new ArrayList<>();
		this.attacks = new ArrayList<>();
	}

	@Override
	public Comparator<? super Player> comparator(){
		return null;
	}

	@Override
	public SortedMap<Player, IAttack> subMap(Player fromKey, Player toKey){
		return null;
	}

	@Override
	public SortedMap<Player, IAttack> headMap(Player toKey){
		return null;
	}

	@Override
	public SortedMap<Player, IAttack> tailMap(Player fromKey){
		return null;
	}

	@Override
	public Player firstKey(){
		return this.players.get(0);
	}

	@Override
	public Player lastKey(){
		return this.players.get(this.players.size()-1);
	}

	@Override
	public int size(){
		return this.players.size();
	}

	@Override
	public boolean isEmpty(){
		return this.players.isEmpty();
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	@Override
	public boolean containsKey(Object key){
		return this.players.contains(key);
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	@Override
	public boolean containsValue(Object value){
		return this.attacks.contains(value);
	}

	@Override
	public IAttack get(Object key){
		if(!(key instanceof Player)) return null;
		int i = this.players.indexOf(key);
		if(i < 0) return null;
		return this.attacks.get(i);
	}

	public Iterator<IAttack> iterator(){
		return new Iterator<IAttack>(){

			private int i = 0;

			@Override
			public boolean hasNext(){
				return this.i < BattleMap.this.attacks.size();
			}

			@Override
			public IAttack next(){
				return BattleMap.this.attacks.get(this.i++);
			}
		};
	}

	@Override
	public IAttack put(Player key, IAttack value){
		if(this.isEmpty()){
			this.players.add(key);
			this.attacks.add(value);
			return null;
		}
		//Using .contains then .indexOf gives O(2n) where we can simply check if indexOf < 0
		int i = this.players.indexOf(key);
		if(i >= 0){
			//If this key exists, then lets just not do anything. This is because we shouldn't have an existing
			// key... >.>
			return null;
		}
		//At this point, the key does not exist before
		this.attacks.add(value);
		this.attacks.sort(this);
		this.players.add(this.attacks.indexOf(value), key);
		return null;
	}

	@SuppressWarnings("ChainOfInstanceofChecks")
	@Override
	public IAttack remove(Object key){
		if(key instanceof Player){
			int i = this.players.indexOf(key);
			if(i < 0) return null;
			this.players.remove(i);
			return this.attacks.remove(i);
		}
		if(key instanceof IAttack){
			int i = this.attacks.indexOf(key);
			if(i < 0) return null;
			this.players.remove(i);
			return this.attacks.remove(i);
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends Player, ? extends IAttack> m){}

	@Override
	public void clear(){
		this.attacks.clear();
		this.players.clear();
	}

	@Override
	public Set<Player> keySet(){
		return new HashSet<>(this.players);
	}

	@Override
	public Collection<IAttack> values(){
		return this.attacks;
	}

	@Override
	public Set<Entry<Player, IAttack>> entrySet(){
		return null;
	}

	@Override
	public int compare(IAttack o1, IAttack o2){
		int ret;
		if(o2.move == null || o1.move == null){
			ret = 0;
		} else {
			ret = o2.move.getPriority()-o1.move.getPriority();
		}
		if(ret == 0){
			ret = o2.attacker.getSpeedStat()-o1.attacker.getSpeedStat();
		}
		//if(this.has(Battle.BattleEffects.TRICK_ROOM)) ret *= -1; This doesn't have a battle context
		return ret;
	}
}
