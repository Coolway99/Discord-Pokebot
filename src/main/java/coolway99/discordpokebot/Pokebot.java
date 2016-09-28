package coolway99.discordpokebot;

import coolway99.discordpokebot.misc.GameList;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.storage.ConfigHandler;
import coolway99.discordpokebot.storage.PlayerHandler;
import coolway99.discordpokebot.web.WebInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class Pokebot{
	public static final String VERSION = "1.2.0";
	//TODO make the rest of these configs
	private static final byte SAVE_DELAY = 1; //In minutes
	private static final short MESSAGE_DELAY = 250;//secondsToMiliseconds(1);
	private static final byte GAME_DELAY = 1;//minutesToMiliseconds(1);
	public static final Logger LOGGER = LoggerFactory.getLogger("Pokebot");

	public static IDiscordClient client;
	public static final ConfigHandler config = new ConfigHandler();
	public static final Random ran = new Random();
	//public static final Timer timer = new Timer("Pokebot Timer Thread", true);
	public static final ScheduledExecutorService timer = Executors.newScheduledThreadPool(4);
	private static final ConcurrentHashMap<IChannel, StringBuilder> buffers = new ConcurrentHashMap<>();
	//The hope with channel-locking is that we don't send messages the at the same time we are adding new ones
	//Channel objects, while not equal to each other directly, are equal if they belong to the same channel
	private static final ConcurrentHashMap<IChannel, ReentrantLock> locks = new ConcurrentHashMap<>();

	public static void main(String... args) throws Exception{
		LOGGER.info("Pokebot version {}", VERSION);
		if(config.TOKEN.isEmpty()){
			LOGGER.error("Error: No token found in pokebot.conf file");
			System.exit(-1);
		}
		if(config.WEBENABLED){
			LOGGER.info("Web interface enabled on port {}", config.PORT);
			WebInterface.initWebInterface(config.REDIRECT_URL, config.PORT);
		}
		client = new ClientBuilder().withToken(config.TOKEN).login();
		LOGGER.info("Logging in");
		LOGGER.debug("Creating new BotReadyHandler and dispatching it");
		client.getDispatcher().registerListener(new BotReadyHandler(Thread.currentThread()));
		LOGGER.debug("Scheduling Player Save Handler");
		timer.scheduleAtFixedRate(PlayerHandler::saveAll, SAVE_DELAY, SAVE_DELAY, TimeUnit.MINUTES);
		LOGGER.debug("Scheduling message sender");
		timer.scheduleAtFixedRate(Pokebot::sendAllMessages, MESSAGE_DELAY, MESSAGE_DELAY,
				TimeUnit.MILLISECONDS);
		LOGGER.debug("Scheduling game changer");
		timer.scheduleAtFixedRate(() -> Pokebot.client.changeStatus(Status.game(Pokebot.getRandomGame()))
				, GAME_DELAY, GAME_DELAY, TimeUnit.MINUTES);
		//Now that the main thread is done doing it's business and the bot is busy logging in...
		Move.registerMoves();
		LOGGER.debug("Finished bot setup");
	}

	public static File getSaveFile(IUser user, byte slot){
		return new File(config.SAVEDIR+'/'+user.getID()+"/"+slot);
	}

	public static File getMainFile(IUser user){
		return new File(config.SAVEDIR+'/'+user.getID()+"/main");
	}

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

	public static void sendPrivateMessage(IUser user, String message){
		try{
			sendMessage(client.getOrCreatePMChannel(user), message);
		} catch(RateLimitException e){
			LOGGER.error("Unable to send PM, hit rate limit");
		} catch(DiscordException e){
			LOGGER.error("Unable to send PM\n", e);
		}
	}

	public static String getRandomGame(){
		LOGGER.debug("Getting a random game");
		GameList[] vals = GameList.values();
		return vals[ran.nextInt(vals.length)].getName();
	}

	public static void sendAllMessages(){
		LOGGER.debug("Sending all messages");
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
							LOGGER.error("{}, 2000+ characters, not a single line break", builder);
							requestHelper(channel, "Exceeded max characters per message");
							i.remove();
						} else {
							String remainder = builder.substring(k+1);
							builder.setLength(k);
							requestHelper(channel, '\u200B'+builder.toString()); //We add a 0-width character to the
							// front, prevents triggering other bot's commands
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

	public static void requestHelper(final IChannel channel, final String message){
		RequestBuffer.request(() -> {
			try{
				channel.sendMessage(message);
			}catch(RateLimitException e){
				LOGGER.info("We are being rate limited in channel {}/{}", channel.getGuild().getID(), channel.getID());
				throw e;
			}catch(MissingPermissionsException e){
				LOGGER.error("We do not have permission to send messages in channel {}/{}", channel.getGuild().getID(), channel.getID());
			}catch(DiscordException e){
				LOGGER.error("We were unable to send messages in channel {}/{}", channel.getGuild().getID(), channel.getID(), e);
			}
		});
	}
}
