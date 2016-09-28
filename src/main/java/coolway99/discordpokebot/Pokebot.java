package coolway99.discordpokebot;

import coolway99.discordpokebot.misc.GameList;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.storage.ConfigHandler;
import coolway99.discordpokebot.storage.PlayerHandler;
import coolway99.discordpokebot.web.WebInterface;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is the main class. It holds the entry point as well as a few convenience methods
 * @author Coolway99
 */
public class Pokebot{
	/**
	 * The current version of Pokebot
	 */
	public static final String VERSION = "1.2.0";

	//TODO make the rest of these configs
	/**
	 * The delay between autosaving, in minutes
	 * @see PlayerHandler#saveAll()
	 */
	private static final byte SAVE_DELAY = 1;
	/**
	 * The delay between sending messages, in miliseconds
	 * @see #sendMessage(IChannel, String)
	 */
	private static final short MESSAGE_DELAY = 250;
	/**
	 * The delay between switching the "Currently Playing" game
	 * @see GameList
	 * @see #getRandomGame()
	 */
	private static final byte GAME_DELAY = 1;

	/**
	 * This represents the client on discord, and is the main interaction with it.
	 * It can also be thought of as "our user"
	 */
	public static IDiscordClient client;
	/**
	 * A main external configuration file, this allows the owner to change critical info about the bot
	 */
	public static final ConfigHandler config = new ConfigHandler();
	/**
	 * A global random, used for generating random numbers
	 */
	public static final Random ran = new Random();
	/**
	 * A global timer thread, this processes things like updating saving players or sending messages
	 */
	public static final ScheduledExecutorService timer = Executors.newScheduledThreadPool(4);

	/**
	 * This HashMap contains a list of all {@link StringBuilder StringBuilders} used in message sending
	 * @see #sendAllMessages()
	 * @see #sendMessage(IChannel, String)
	 * @see #locks
	 */
	private static final ConcurrentHashMap<IChannel, StringBuilder> buffers = new ConcurrentHashMap<>();
	/**
	 * This HashMap contains a list of all {@link ReentrantLock Locks} used in message sending
	 * <br /><br />
	 * The hope with channel-locking is that we don't send messages at the same time we are adding new ones.
	 * Channel objects, while not equal to each other directly, are equal if they belong to the same channel
	 * @see #sendMessage(IChannel, String)
	 * @see #sendAllMessages()
	 * @see #buffers
	 */
	private static final ConcurrentHashMap<IChannel, ReentrantLock> locks = new ConcurrentHashMap<>();

	public static void main(String... args) throws Exception{
		System.out.println("Pokebot version "+VERSION);
		if(config.TOKEN.isEmpty()){
			System.out.println("Error: No token found in pokebot.conf file");
			System.exit(0);
		}
		if(config.WEBENABLED){
			System.out.println("Web interface enabled on port "+config.PORT);
			WebInterface.initWebInterface(config.REDIRECT_URL, config.PORT);
		}
		client = new ClientBuilder().withToken(config.TOKEN).login();
		System.out.println("Logging in");
		client.getDispatcher().registerListener(new BotReadyHandler(Thread.currentThread()));
		timer.scheduleAtFixedRate(PlayerHandler::saveAll, SAVE_DELAY, SAVE_DELAY, TimeUnit.MINUTES);
		timer.scheduleAtFixedRate(Pokebot::sendAllMessages, MESSAGE_DELAY, MESSAGE_DELAY,
				TimeUnit.MILLISECONDS);
		timer.scheduleAtFixedRate(() -> Pokebot.client.changeStatus(Status.game(Pokebot.getRandomGame()))
				, GAME_DELAY, GAME_DELAY, TimeUnit.MINUTES);
		//Now that the main thread is done doing it's business and the bot is busy logging in...
		Move.registerMoves();
	}

	/**
	 * This helper method gets the save file path from the current configuration, the user's ID, and the slot number
	 * @param user The user to get the save file for
	 * @param slot The slot to get the file for
	 * @return A file leading to &lt;{@link ConfigHandler#SAVEDIR}&gt;/&lt;{@link IUser#getID()}&gt;/&lt;slot&gt;
	 */
	public static File getSaveFile(IUser user, byte slot){
		return new File(config.SAVEDIR+'/'+user.getID()+"/"+slot);
	}

	/**
	 * This helper method gets the main file path from the current configuration and the user's id
	 * @param user The user to get the main file for
	 * @return A file leading to &lt;{@link ConfigHandler#SAVEDIR}&gt;/&lt;{@link IUser#getID()}&gt;/main
	 */
	public static File getMainFile(IUser user){
		return new File(config.SAVEDIR+'/'+user.getID()+"/main");
	}

	/**
	 * A helper method to queue messages for to be sent in bulk
	 * @param channel The channel to send the message too
	 * @param message The message to send
	 * @see #sendAllMessages()
	 * @see #sendPrivateMessage(IUser, String)
	 */
	public static void sendMessage(IChannel channel, String message){
		locks.putIfAbsent(channel, new ReentrantLock()); //Thread Safe "create if doesn't exist"
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

	/**
	 * A helper method to send a PM to a user, uses the same system as {@link #sendMessage(IChannel, String)}
	 * @param user The user to send the message too
	 * @param message The message to send to the user
	 */
	public static void sendPrivateMessage(IUser user, String message){
		try{
			sendMessage(client.getOrCreatePMChannel(user), message);
		} catch(RateLimitException e){
			System.err.println("Unable to send PM, hit rate limit");
		} catch(DiscordException e){
			e.printStackTrace();
			System.err.println("\nUnable to send PM");
		}
	}

	/**
	 * Gets a random game from {@link GameList}
	 * @return A string representing a random pokemon game or joke
	 */
	public static String getRandomGame(){
		GameList[] vals = GameList.values();
		return vals[ran.nextInt(vals.length)].getName();
	}

	/**
	 * Sends all the messages in the buffer. This works by first locking down the list of buffers, then locking down each
	 * buffer individually. This makes all the messages for the channel appear as one big message, and helps a lot
	 * with ratelimits
	 */
	public static void sendAllMessages(){
		synchronized(buffers){ //This lock is for the buffers, adding/removing channel buffers
			Iterator<IChannel> i = buffers.keySet().iterator();
			while(i.hasNext()){
				IChannel channel = i.next();
				StringBuilder builder = buffers.get(channel);
				locks.get(channel).lock(); //This lock is for changing channel buffers
				try{
					if(builder.length() > 1999){ //This is the character limit TODO make it not hardcoded
						int k = builder.lastIndexOf("\n", 1999);
						if(k == -1){
							System.err.println(builder);
							System.err.println("2000+ characters, not a single line break");
							requestHelper(channel, "Exceeded max characters per message");
							i.remove();
						} else {
							String remainder = builder.substring(k+1);
							builder.setLength(k);
							//We add a 0-width character to the front, prevents triggering other bot's commands
							requestHelper(channel, '\u200B'+builder.toString());
							builder.setLength(0);
							builder.append(remainder);
						}
					} else if(builder.length() > 0){
						//If it fails at this point, then the next message tick will try again
						requestHelper(channel, '\u200B'+builder.toString());
						i.remove();
					} else {
						i.remove();
					}
				} finally {
					locks.get(channel).unlock();
				}
			}
		}
	}

	/**
	 * A helper for using the request buffer. Can be used to send a message directly
	 * @param channel The channel to send a message too
	 * @param message The message to send
	 */
	public static void requestHelper(final IChannel channel, final String message){
		RequestBuffer.request(() -> {
			try{
				channel.sendMessage(message);
			}catch(RateLimitException e){
				System.err.println("We are being rate limited in channel "
						+channel.getGuild().getID()+'/'+channel.getID());
				throw e;
			}catch(MissingPermissionsException e){
				System.err.println("We do not have permission to send messages in channel "
						+channel.getGuild().getID()+'/'+channel.getID());
			}catch(DiscordException e){
				e.printStackTrace();
				System.err.println("\nWe were unable to send messages in channel "
						+channel.getGuild().getID()+'/'+channel.getID());
			}
		});
	}
}
