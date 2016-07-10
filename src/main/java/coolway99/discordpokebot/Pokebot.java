package coolway99.discordpokebot;

import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.storage.PlayerHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

public class Pokebot{
	
	public static final String COMMAND_PREFIX = "==";
	public static final long SAVE_DELAY = minutesToMiliseconds(1);
	
	public static IDiscordClient client;
	public static final Scanner in = new Scanner(System.in);
	public static final Random ran = new Random();
	public static final Timer timer = new Timer("Pokebot Timer Thread", true);
	//TODO this creates a bottleneck of only one message at a time
	private static IChannel batchMessagesForBattle = null;
	private static StringBuilder builder = null;
	
	public static void main(String... args) throws Exception{
		if(args.length < 1){
			System.out.println("The app needs a token to log in");
			client = getClient(in.nextLine());
		} else {
			client = getClient(args[0]);
		}
		System.out.println("Logging in");
		//client = getClient(TOKEN);
		client.getDispatcher().registerListener(new BotReadyHandler());
		timer.schedule(new TimerTask(){
			@Override
			public void run(){
				PlayerHandler.saveAll();
			}
		}, SAVE_DELAY, SAVE_DELAY);
	}
	
	public static IDiscordClient getClient(String token) throws Exception{
		return new ClientBuilder().withToken(token).login();
	}
	
	public static File getSaveFile(IUser user){
		return new File("/botdata/userpokemon/"+user.getID());
	}
	
	public static void sendMessage(IChannel channel, String message){
		try{
			channel.sendMessage(message);
		}catch(DiscordException | HTTP429Exception | MissingPermissionsException e){
			e.printStackTrace();
		}
	}
	
	public static void startBatchMessages(Battle battle){
		batchMessagesForBattle = battle.channel;
		builder = new StringBuilder();
	}
	
	public static void sendBatchableMessage(IChannel channel, String message){
		if(batchMessagesForBattle != null && channel.getID().equals(batchMessagesForBattle.getID())){
			builder.append(message);
			builder.append('\n');
			return;
		}
		Pokebot.sendMessage(channel, message);
	}
	
	
	public static void endBatchMessages(){
		IChannel channel = batchMessagesForBattle;
		batchMessagesForBattle = null;
		sendMessage(channel, builder.toString());
		builder = null;
	}
	
	public static long minutesToMiliseconds(int minutes){
		return secondsToMiliseconds(minutes * 60L);
	}
	
	public static long secondsToMiliseconds(long seconds){
		return seconds * 1000L;
	}	
}