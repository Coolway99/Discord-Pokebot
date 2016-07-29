package coolway99.discordpokebot;

import java.io.File;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
	//public static final long SAVE_DELAY = minutesToMiliseconds(1);
	private static final byte SAVE_DELAY = 1; //In minutes
	private static final short MESSAGE_DELAY = 250;//secondsToMiliseconds(1);
	private static final byte GAME_DELAY = 1;//minutesToMiliseconds(1);

	public static IDiscordClient client;
	public static final ConfigHandler config = new ConfigHandler();
	public static final Scanner in = new Scanner(System.in);
	public static final Random ran = new Random();
	//public static final Timer timer = new Timer("Pokebot Timer Thread", true);
	public static final ScheduledExecutorService timer =  Executors.newScheduledThreadPool(4);
	private static final ConcurrentHashMap<IChannel, StringBuilder> buffers = new ConcurrentHashMap<>();
	//The hope with channel-locking is that we don't send messages the at the same time we are adding new ones
	//Channel objects, while not equal to each other directly, are equal if they belong to the same channel
	private static final ConcurrentHashMap<IChannel, ReentrantLock> locks = new ConcurrentHashMap<>();

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
		timer.scheduleAtFixedRate(PlayerHandler::saveAll
				, SAVE_DELAY, SAVE_DELAY, TimeUnit.MINUTES);
		timer.scheduleAtFixedRate(new MessageTimer(), MESSAGE_DELAY, MESSAGE_DELAY, TimeUnit.MILLISECONDS);
		timer.scheduleAtFixedRate(() -> Pokebot.client.changeStatus(Status.game(Pokebot.getRandomGame()))
		, GAME_DELAY, GAME_DELAY, TimeUnit.MINUTES);
	}

	private static IDiscordClient getClient(String token) throws Exception{
		return new ClientBuilder().withToken(token).login();
	}

	public static File getSaveFile(IUser user){
		return new File(config.SAVEDIR+'/'+user.getID());
	}

	public static void sendMessage(IChannel channel, String message){
		locks.putIfAbsent(channel, new ReentrantLock());
		locks.get(channel).lock();
		try{
			if(!buffers.containsKey(channel)){
				buffers.put(channel, new StringBuilder(message));
			} else {
				buffers.get(channel).append('\n').append(message);
			}
		} finally {
			locks.get(channel).unlock();
		}
	}

	public static void sendPrivateMessage(IUser user, String message){
		try{
			sendMessage(client.getOrCreatePMChannel(user), message);
		} catch (RateLimitException e){
			System.err.println("Unable to send PM, hit rate limit");
		} catch (DiscordException e){
			e.printStackTrace();
			System.err.println("\nUnable to send PM");
		}
	}

	public static String getRandomGame(){
		GameList[] vals = GameList.values();
		return vals[Pokebot.ran.nextInt(vals.length)].getName();
	}
	
	public static class MessageTimer implements Runnable{
		@Override
		public void run(){
			//I'm lazy
			synchronized(Pokebot.buffers){
				syncRun();
			}
		}
		
		private static void syncRun(){
			Iterator<IChannel> i = buffers.keySet().iterator();
			while(i.hasNext()){
				IChannel channel = i.next();
				StringBuilder builder = buffers.get(channel);
				if(builder.length() > 0){
					locks.get(channel).lock();
					try{
						channel.sendMessage(builder.toString()); //If it fails at this point, then the next message tick will try again
						i.remove();
					}catch(RateLimitException e){
						System.err.println("We are being rate limited in channel "
								+channel.getGuild().getID()+'/'+channel.getID());
					}catch(MissingPermissionsException e){
						System.err.println("We do not have permission to send messages in channel "
								+channel.getGuild().getID()+'/'+channel.getID());
					}catch(DiscordException e){
						e.printStackTrace();
						System.err.println("\nWe were unable to send messages in channel "
								+channel.getGuild().getID()+'/'+channel.getID());
					} finally {
						locks.get(channel).unlock();
					}
				}
			}
		}
	}
}