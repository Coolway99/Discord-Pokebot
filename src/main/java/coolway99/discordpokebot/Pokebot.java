package coolway99.discordpokebot;

import coolway99.discordpokebot.misc.GameList;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.moves.rewrite.NewMoves;
import coolway99.discordpokebot.storage.ConfigHandler;
import coolway99.discordpokebot.web.WebInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public class Pokebot{
	public static final String VERSION = "dev-2.0.0 Move-Scripting.Pre_-1";
	//TODO make the rest of these configs
	public static final byte SAVE_DELAY = 1; //In minutes
	public static final short MESSAGE_DELAY = 250;//secondsToMiliseconds(1);
	public static final byte GAME_DELAY = 1;//minutesToMiliseconds(1);

	public static IDiscordClient client;
	public static final ConfigHandler config = new ConfigHandler();
	public static final Random ran = new Random();
	public static final ScheduledExecutorService timer = Executors.newScheduledThreadPool(4);
	private static final ConcurrentHashMap<IChannel, StringBuilder> buffers = new ConcurrentHashMap<>();
	//The JS engine, for running showdown's code
	public static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	public static final Invocable js = (Invocable) engine;
	//The hope with channel-locking is that we don't send messages the at the same time we are adding new ones
	//Channel objects, while not equal to each other directly, are equal if they belong to the same channel
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
		client = new ClientBuilder().withToken(config.TOKEN).build();
		System.out.println("Built Client");
		client.getDispatcher().registerListener(new GuildCreateHandler());
		client.getDispatcher().registerListener(new BotReadyHandler(Thread.currentThread()));
		System.out.println("Created Listeners");
		client.login();
		System.out.println("Logging in");
		//Timers moved to BotReadyHandler
		//Now that the main thread is done doing its business and the bot is busy logging in...
		Move.registerMoves();
		NewMoves.registerMoves();
		//Item.registerItems();
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
			System.err.println("Unable to send PM, hit rate limit");
		} catch(DiscordException e){
			e.printStackTrace();
			System.err.println("\nUnable to send PM");
		}
	}

	public static String getRandomGame(){
		GameList[] vals = GameList.values();
		return vals[ran.nextInt(vals.length)].getName();
	}

	public static void sendAllMessages(){
		if(!client.isReady()){
			System.err.println("Skipping send messages, bot isn't ready");
			return;
		}
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

	public static void setupEngine(){

	}

	@Nullable
	public static File getResource(@NotNull String path){
		try{
			URL url = Pokebot.class.getResource("../../"+path);
			if(url == null){
				System.err.println("File not found: "+path);
				System.err.print("Full path: ");
				System.err.println(url);
				return null;
			}
			File file = new File(url.toURI());
			if(file.exists()) return file;
			return null;
		} catch(URISyntaxException e){
			e.printStackTrace();
			System.err.println("File not found: "+path);
		}
		return null;
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public static boolean diceRoll(double chance){
		return ran.nextDouble()*100D <= chance;
	}
}
