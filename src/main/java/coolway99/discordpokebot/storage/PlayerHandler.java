package coolway99.discordpokebot.storage;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.battle.BattleManager;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Iterator;

public class PlayerHandler{
	
	private static final HashMap<String, Player> playerMap = new HashMap<>();
	
	public static Player getPlayer(IUser user){
		System.out.println("Accessing map");
		if(!playerMap.containsKey(user.getID())){
			System.out.println("Creating new entry"); 
			Player player = new Player(user);
			playerMap.put(user.getID(), player);
			}
		System.out.println(user.getID());
		return playerMap.get(user.getID());
	}
	
	public static void removePlayer(IUser user){
		if(!playerMap.containsKey(user.getID())) return;
		playerMap.remove(user.getID()).saveData();
	}
	
	public static void saveAll(){
		Iterator<Player> i = playerMap.values().iterator();
		while(i.hasNext()){
			Player player = i.next();
			player.saveData();
			//If play is not in a battle, and the player does not have a battle pending
			if(!(player.inBattle() || BattleManager.hasBattlePending(player.user))){
				System.out.println("Removing player");
				i.remove();
			}
		}
	}
}
