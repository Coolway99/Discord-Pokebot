package coolway99.discordpokebot.battles;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.storage.PlayerHandler;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class BattleManager{

	public static final int BATTLE_TIMEOUT = 10; //In Minutes
	
	public static final ArrayList<Battle> runningBattles = new ArrayList<>();
	public static final HashMap<IUser, Battle> unstartedBattles = new HashMap<>();

	public static boolean hasBattlePending(IUser user){
		return unstartedBattles.containsKey(user);
	}

	public static void createBattle(IChannel channel, String battleName, int turnTime, IUser host, List<IUser> invites){
		if(hasBattlePending(host)){
			Pokebot.sendMessage(channel, "You already have a battle pending!");
			return;
		}
		if(PlayerHandler.getPlayer(host).inBattle()){
			Pokebot.sendMessage(channel, "You're already in a battle!");
			return;
		}
		//We know there isn't any battles for them
		ArrayList<Player> players = new ArrayList<>();
		players.add(PlayerHandler.getPlayer(host));
		Battle battle;
		switch(battleName){
			case "SINGLEBATTLE":{
				battle = new SingleBattle(channel, turnTime, players);
				break;
			}
			default:{
				battle = null;
				break;
			}
		}
		if(battle == null){
			Pokebot.sendMessage(channel, host.mention()+", that is not a valid battle type!"); //TODO perhaps add types
			return;
		}
		unstartedBattles.put(host, battle);
		battleInviteMessage(channel, battle, host, invites);
		battle.timer = Pokebot.timer.schedule(() -> {
			unstartedBattles.remove(host);
			Pokebot.sendMessage(channel, "The battle hosted by "+host.mention()+" has expired!");
		}, BATTLE_TIMEOUT, TimeUnit.MINUTES);
	}

	public static void onJoinBattle(IChannel channel, IUser host, IUser user){
		Battle battle = unstartedBattles.get(host);
		if(battle == null){
			if(PlayerHandler.getPlayer(user).inBattle()){
				Pokebot.sendMessage(channel, user.mention()+" the battle hosted by that person has already started!");
				return;
			}
			Pokebot.sendMessage(channel, user.mention()+" there is no battle hosted by that person!");
			return;
		}
		if(!battle.channel.equals(channel)){
			Pokebot.sendMessage(channel, " you must do this in the same channel the battle is being hosted in!");
			return;
		}
		Player player = PlayerHandler.getPlayer(user);
		battle.joinBattle(player);
	}

	public static void onStartBattle(IChannel channel, IUser host){
		Battle battle = unstartedBattles.get(host);
		if(battle == null){
			Pokebot.sendMessage(channel, "You don't have a battle pending!");
			return;
		}
		if(!battle.channel.equals(channel)){
			Pokebot.sendMessage(channel, "This isn't the same channel where you have the battle pending!");
			return;
		}
		if(battle.getNumberOfPlayers() < battle.getMinPlayers()){
			Pokebot.sendMessage(channel, "You don't have enough participants to start!");
			return;
		}
		unstartedBattles.remove(battle);
		runningBattles.add(battle);
		battle.setupBattle();
	}

	//Ideally only ever called from the command
	public static void onLeaveBattle(Player player){
		if(player.battle == null) return;
		Pokebot.sendMessage(player.battle.channel, player.mention()+" left the battle!");
		player.battle.onLeaveBattle(player);
	}

	public static void onCancelBattle(IChannel channel, IUser user){
		Battle battle = unstartedBattles.remove(user);
		if(battle == null){
			Pokebot.sendMessage(channel, user.mention()+", you don't have a battle pending!");
			return;
		}
		battle.timer.cancel(true);
		battle.participants.forEach(BattleManager::onExitBattle);
		Pokebot.sendMessage(channel, user.mention()+" canceled their pending battle!");
	}

	public static void onExitBattle(Player player){
		player.HP = player.getMaxHP();
		player.removeAllEffects();
		player.lastAttacker = null;
		player.lastMove = null;
		player.lastMoveData = 0;
		for(MoveSet move : player.moves){
			if(move != null) move.resetPP();
		}
		player.battle = null;
	}

	private static void battleInviteMessage(IChannel channel, Battle battle, IUser host, List<IUser> users){
		if(users == null || users.isEmpty()) return;
		StringBuilder builder = new StringBuilder("Calling ");
		if(users.size() > 1){
			Iterator<IUser> i = users.iterator();
			while(i.hasNext()){
				IUser user = i.next();
				if(!i.hasNext()) builder.append(" and ");
				builder.append(user.mention());
				builder.append(i.hasNext() ? ", " : ". ");
			}
		} else {
			builder.append(users.get(0).mention());
			builder.append(". ");
		}
		builder.append(host.mention());
		builder.append(" has invited you ");
		if(users.size() > 1) builder.append("all ");
		builder.append("to battle in a ").append(battle.getBattleType()).append(", last one standing wins!\n");
		builder.append("To join, type ");
		builder.append(Pokebot.config.COMMAND_PREFIX);
		builder.append("joinbattle ");
		builder.append(host.mention());
		builder.append('\n');
		builder.append(host.mention());
		builder.append(" type ");
		builder.append(Pokebot.config.COMMAND_PREFIX);
		builder.append("startbattle to begin.\n");
		builder.append("If the battle is not started in ");
		builder.append(BATTLE_TIMEOUT);
		builder.append(" minutes, then this request will automatically expire.");
		Pokebot.sendMessage(channel, builder.toString());
	}

	public static void nukeBattles(){
		for(Battle battle : runningBattles){
			List<Player> players = battle.getParticipants();
			for(Player player : players){
				player.battle = null;
			}
			Pokebot.sendMessage(battle.channel, "Battle nuked, "+Pokebot.config.BOTNAME+" going offline");
		}
		for(Battle battle : unstartedBattles.values()){
			List<Player> players = battle.getParticipants();
			for(Player player : players){
				player.battle = null;
			}
			Pokebot.sendMessage(battle.channel, "Battle nuked, "+Pokebot.config.BOTNAME+" going offline");
		}
		runningBattles.clear();
		unstartedBattles.clear();
	}
}
