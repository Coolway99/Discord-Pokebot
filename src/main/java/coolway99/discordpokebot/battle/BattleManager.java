package coolway99.discordpokebot.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import coolway99.discordpokebot.Effects;
import coolway99.discordpokebot.Moves;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.storage.PlayerHandler;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class BattleManager{
	
	public static final int BATTLE_TIMEOUT = 10; //In Minutes
	
	public static final ArrayList<Battle> battles = new ArrayList<>();
	public static final HashMap<IUser, PreBattle> preBattles = new HashMap<>(2); 
	private static final ArrayList<IUser> inPreBattle = new ArrayList<>();
	
	public static boolean hasBattlePending(IUser user){
		return inPreBattle.contains(user);
	}
	
	public static void createBattle(IChannel channel, IUser user, List<IUser> invites, int turnTime){
		if(hasBattlePending(user)){
			Pokebot.sendMessage(channel, "You already have a battle pending!");
			return;
		}
		battleInviteMessage(channel, user, invites);
		PreBattle pre = new PreBattle(channel, PlayerHandler.getPlayer(user), turnTime);
		preBattles.put(user, pre);
		inPreBattle.add(user);
	}
	
	public static void onJoinBattle(IChannel channel, IUser user, IUser host){
		PreBattle pre = preBattles.get(host);
		if(pre == null){
			Pokebot.sendMessage(channel, user.mention()+" there is no battle pending for that person!");
			return;
		}
		if(!pre.channel.getID().equals(channel.getID())){
			Pokebot.sendMessage(channel, " you must do this in the same channel the battle is being hosted in!");
			return;
		}
		List<Player> list = preBattles.get(host).participants;
		Player player = PlayerHandler.getPlayer(user);
		if(!list.contains(player)){
			list.add(player);
			inPreBattle.add(user);
		}
		Pokebot.sendMessage(channel, user.mention()+" joined the battle hosted by "+host.mention());
	}
	
	public static void onStartBattle(IChannel channel, IUser host){
		PreBattle pre = preBattles.get(host);
		if(pre == null){
			Pokebot.sendMessage(channel, "You don't have a battle pending!");
			return;
		}
		if(!pre.channel.getID().equals(channel.getID())){
			Pokebot.sendMessage(channel, "This isn't the same channel where you have the battle pending!");
			return;
		}
		if(pre.participants.size() < 2){
			Pokebot.sendMessage(channel, "You don't have enough participants to start!");
			return;
		}
		//There is a battle pending, and it is the same channel, let's start the battle
		pre.onBattleStarting();
		Battle battle = new Battle(pre.channel, pre.turnTimer, pre.participants);
		battles.add(battle);
		preBattles.remove(host);
		Pokebot.sendMessage(channel, "Begin!");
	}
	
	//Idealy only ever called from the command
	//TODO
	/*
	public static void onLeaveBattle(Player player){
		if(player.battle == null) return;
		player.battle.playerFainted(player);
	}*/
	
	public static void onExitBattle(Player player){
		player.HP = player.getMaxHP();
		player.effect = Effects.NORMAL;
		player.isSemiInvunerable = false;
		player.lastAttacker = null;
		player.lastMove = Moves.NULL;
		player.lastMovedata = 0;
		for(int x = 0; x < player.numOfAttacks; x++){
			player.PP[x] = player.moves[x].getPP();
		}
		player.battle = null;
		inPreBattle.remove(player);
	}

	public static void onBattleWon(Battle battle, Player player){
		Pokebot.sendBatchableMessage(battle.channel, player.getUser().mention()+" won the battle!");
		battles.remove(battle);
		onExitBattle(player);
	}
	
	public static void battleInviteMessage(IChannel channel, IUser host, List<IUser> users){
		if(users == null || users.isEmpty()) return;
		StringBuilder builder = new StringBuilder("Calling ");
		if(users.size() > 1){
			Iterator<IUser> i = users.iterator();
			while(i.hasNext()){
				IUser user = i.next();
				if(!i.hasNext()) builder.append(" and ");
				builder.append(user.mention());
				builder.append((i.hasNext() ? ", " : ". "));
			}
		} else {
			builder.append(users.get(0).mention());
			builder.append(". ");
		}
		builder.append(host.mention());
		builder.append(" has invited you ");
		if(users.size() > 1) builder.append("all ");
		builder.append("to battle in a free for all, last one standing wins!\n");
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
		for(Battle battle : battles){
			List<Player> players = battle.getParticipants();
			for(Player player : players){
				player.battle = null;
			}
			Pokebot.sendMessage(battle.channel, "Battle nuked, "+Pokebot.config.BOTNAME+" going offline");
		}
		battles.clear();
		inPreBattle.clear();
	}
}
