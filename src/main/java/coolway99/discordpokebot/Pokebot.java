package coolway99.discordpokebot;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.misc.GameList;
import coolway99.discordpokebot.storage.ConfigHandler;
import coolway99.discordpokebot.storage.PlayerHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class Pokebot{
	//TODO make the rest of these configs
	public static final long SAVE_DELAY = minutesToMiliseconds(1);
	public static final long MESSAGE_DELAY = 250;//secondsToMiliseconds(1);
	public static final long GAME_DELAY = minutesToMiliseconds(1);
	
	public static IDiscordClient client;
	public static final ConfigHandler config = new ConfigHandler();
	public static final Scanner in = new Scanner(System.in);
	public static final Random ran = new Random();
	public static final Timer timer = new Timer("Pokebot Timer Thread", true);
	public static final HashMap<IChannel, StringBuilder> buffers = new HashMap<>();
	
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
		timer.scheduleAtFixedRate(new MessageTimer(), MESSAGE_DELAY, MESSAGE_DELAY);
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				Pokebot.client.changeStatus(Status.game(Pokebot.getRandomGame()));
			}
		}, secondsToMiliseconds(5), GAME_DELAY);
	}
	
	public static IDiscordClient getClient(String token) throws Exception{
		return new ClientBuilder().withToken(token).login();
	}
	
	public static File getSaveFile(IUser user){
		return new File("/botdata/userpokemon/"+user.getID());
	}
	
	public static void sendMessage(IChannel channel, String message){
		/*try{
			channel.sendMessage(message);
		}catch(DiscordException | HTTP429Exception | MissingPermissionsException e){
			e.printStackTrace();
		}*/
		if(!buffers.containsKey(channel)){
			buffers.put(channel, (new StringBuilder(message)));
		} else {
			buffers.get(channel).append('\n').append(message);
		}
	}
	
	public static void sendPrivateMessage(IUser user, String message){
		try{
			sendMessage(client.getOrCreatePMChannel(user), message);
		}catch(RateLimitException e){
			System.err.println("Unable to send PM, hit rate limit");
		} catch(DiscordException e) {
			e.printStackTrace();
			System.err.println("\nUnable to send PM");
		}
	}
	
	@SuppressWarnings("unused")
	public static void startBatchMessages(Battle battle){}
	
	//TODO perhaps make "Batchable" messages use a builder inside each battle.
	//I don't think this is needed anyways, it's a single timer thread so
	//if the Battle thread takes long enough that the messages are sent, then
	//the message thread won't run until after the battle is done processing
	public static void sendBatchableMessage(IChannel channel, String message){
		sendMessage(channel, message);
	}
	
	public static void endBatchMessages(){}
	//I wanna try just sending raw messages for now
	/*
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
	}*/
	
	public static long minutesToMiliseconds(int minutes){
		return secondsToMiliseconds(minutes * 60L);
	}
	
	public static long secondsToMiliseconds(long seconds){
		return seconds * 1000L;
	}
	
	public static String getRandomGame(){
		GameList[] vals = GameList.values();
		return vals[Pokebot.ran.nextInt(vals.length)].getName();
	}
	
	public static class MessageTimer extends TimerTask{
		@Override
		public void run(){
			Iterator<IChannel> i = buffers.keySet().iterator();
			while(i.hasNext()){
				IChannel channel = i.next();
				StringBuilder builder = buffers.get(channel);
				if(builder.length() > 0){
					try {
						channel.sendMessage(builder.toString()); //If it fails at this point, then the next message tick will try again
						i.remove();
					}catch(RateLimitException e){
						System.err.println("We are being rate limited in channel "
								+channel.getGuild().getID()+'/'+channel.getID());
					}catch(MissingPermissionsException e){
						System.err.println("We do not have permission to send messages in channel "
								+channel.getGuild().getID()+'/'+channel.getID());
					}catch(DiscordException e) {
						e.printStackTrace();
						System.err.println("\nWe were unable to send messages in channel "
								+channel.getGuild().getID()+'/'+channel.getID());
					}
				}
			}
		}
	}
}