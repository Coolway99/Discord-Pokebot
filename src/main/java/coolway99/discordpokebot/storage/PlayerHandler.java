package coolway99.discordpokebot.storage;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.battle.BattleManager;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Iterator;

public class PlayerHandler{

	public static final byte MAX_SLOTS = 6;
	
	private static final HashMap<String, Player> playerMap = new HashMap<>();
	private static final HashMap<String, MainFile> mainFileMap = new HashMap<>();
	
	public static Player getPlayer(IUser user){
		if(playerMap.containsKey(user.getID())) return playerMap.get(user.getID());
		//TODO use main file for last slot
		return getPlayer(user, getMainFile(user).lastSlot);
	}

	private static Player getPlayer(IUser user, byte slot){
		if(playerMap.containsKey(user.getID())){
			if(playerMap.get(user.getID()).slot == slot) return playerMap.get(user.getID());
			//ELSE
			removePlayer(user);
		}
		playerMap.put(user.getID(), new Player(user, slot));
		return playerMap.get(user.getID());
	}

	//TODO This shouldn't be returning strings
	public static String switchSlot(IUser author, byte slot){
		String ID = author.getID();
		if(playerMap.containsKey(ID)){
			Player player = playerMap.get(ID);
			if(player.inBattle()) return "You are in a battle!";
			playerMap.remove(ID).saveData();
		}
		getPlayer(author, slot);
		return "Switched slot to "+(slot+1);
	}

	public static void removePlayer(IUser user){
		if(!playerMap.containsKey(user.getID())) return;
		playerMap.remove(user.getID()).saveData();
	}

	public static MainFile getMainFile(IUser user){
		mainFileMap.computeIfAbsent(user.getID(), s -> new MainFile(user));
		return mainFileMap.get(user.getID());
	}

	public static void saveAll(){
		Iterator<Player> i = playerMap.values().iterator();
		while(i.hasNext()){
			Player player = i.next();
			player.saveData();
			//If the player is not in a battle, and the player does not have a battle pending
			if(!(player.inBattle() || BattleManager.hasBattlePending(player.user))){
				//System.out.println("Removing player");
				i.remove();
			}
		}
		Iterator<MainFile> j = mainFileMap.values().iterator();
		while(j.hasNext()){
			MainFile main = j.next();
			main.saveData();
			j.remove();
		}
	}
}
