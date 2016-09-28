package coolway99.discordpokebot.storage;

import coolway99.discordpokebot.Pokebot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

@SuppressWarnings("UnqualifiedStaticUsage")
public class ConfigHandler{
	
	public static final String CONFPATH = "pokebot.conf";
	public final String BOTNAME;
	public final String COMMAND_PREFIX;
	public final String SAVEDIR;
	public final String OPSPATH;
	public final String OWNERID;
	public final String TOKEN;
	public final boolean WEBENABLED;
	public final int PORT;
	public final String CLIENT_ID;
	public final String CLIENT_SECRET;
	public final String REDIRECT_URL;
	
	private final Properties prop;
	
	//CONSTANTS FOR INTERNAL USE
	private static final String NAMEKEY = "BOTNAME";
	private static final String NAMEDEFAULT = "Pokebot";//"Pokébot";
	private static final String COMMANDKEY = "COMMAND_PREFIX";
	private static final String COMMANDDEFAULT = "==";
	private static final String OWNERIDKEY = "BOT_OWNER_ID";
	private static final String OWNERIDDEFAULT = "";
	private static final String OPSPATHKEY = "OPS_PATH";
	private static final String OPSPATHDEFAULT = "/botdata/ops.conf";
	private static final String SAVEDIRKEY = "USER_POKEMON_DIR";
	private static final String SAVEDIRDEFAULT = "/botdata/userpokemon";
	private static final String TOKENKEY = "TOKEN";
	private static final String TOKENDEFAULT = "";
	private static final String WEBENABLEDKEY = "WEB_ENABLED";
	private static final String WEBENABLEDEFAULT= "false";
	private static final String PORTKEY = "WEB_PORT";
	private static final String PORTDEFAULT = "9009";
	private static final String CLIENTIDKEY = "CLIENT_ID";
	private static final String CLIENTIDDEFAULT = "";
	private static final String CLIENTSECRETKEY = "CLIENT_SECRET";
	private static final String CLIENTSECRETDEFAULT = "";
	private static final String REDIRECTURLKEY = "REDIRECT_URL";
	private static final String REDIRECTURLDEFAULT = "";

	public ConfigHandler(){
		Properties config = new Properties();
		File file = new File(CONFPATH);
		if(file.exists()){
			try(FileInputStream in = new FileInputStream(file)){
				config.load(in);
			}catch(IOException e){
				Pokebot.LOGGER.error("There was an error reading the config file...", e);
			}
		}
		this.BOTNAME = config.getProperty(NAMEKEY, NAMEDEFAULT);
		this.COMMAND_PREFIX = config.getProperty(COMMANDKEY, COMMANDDEFAULT);
		this.OWNERID = config.getProperty(OWNERIDKEY, OWNERIDDEFAULT);
		this.SAVEDIR = config.getProperty(SAVEDIRKEY, SAVEDIRDEFAULT);
		this.OPSPATH = config.getProperty(OPSPATHKEY, OPSPATHDEFAULT);
		this.TOKEN = config.getProperty(TOKENKEY, TOKENDEFAULT);
		this.WEBENABLED = Boolean.parseBoolean(config.getProperty(WEBENABLEDKEY, WEBENABLEDEFAULT));
		if(this.WEBENABLED){
			int port = -1;
			try{
				port = Integer.parseInt(config.getProperty(PORTKEY, PORTDEFAULT));
				if(port < 0 || port > 65535 /*PORT MAX NUMBER*/){
					throw new NumberFormatException();
				}
			} catch(NumberFormatException e){
				Pokebot.LOGGER.error("Error with config, not a valid port number. Expected 0-65535, got {}.", port);
				System.exit(1);
			}
			this.PORT = port;
			this.CLIENT_ID = config.getProperty(CLIENTIDKEY);
			if(this.CLIENT_ID == null){
				Pokebot.LOGGER.error("CLIENT_ID NOT SPECIFIED, NEEDED FOR WEB FUNCTIONALITY");
				System.exit(1);
			}
			this.CLIENT_SECRET = config.getProperty(CLIENTSECRETKEY);
			if(this.CLIENT_SECRET == null){
				Pokebot.LOGGER.error("CLIENT_SECRET NOT SPECIFIED, NEEDED FOR WEB FUNCTIONALITY");
				System.exit(1);
			}
			this.REDIRECT_URL = config.getProperty(REDIRECTURLKEY);
		} else {
			this.PORT = 0;
			this.CLIENT_ID = CLIENTIDDEFAULT;
			this.CLIENT_SECRET = CLIENTSECRETDEFAULT;
			this.REDIRECT_URL = REDIRECTURLDEFAULT;
		}
		this.prop = config;
		
		if(!file.exists()){
			Pokebot.LOGGER.info("Detected first time run, saving config and stopping");
			this.saveDefaultConfig();
			Pokebot.LOGGER.info("Saved config file to "+file.getAbsolutePath());
			System.exit(0);
		}
	}
	
	private void saveDefaultConfig(){
		Pokebot.LOGGER.debug("Creating default config file...");
		this.prop.setProperty(NAMEKEY, NAMEDEFAULT);
		this.prop.setProperty(COMMANDKEY, COMMANDDEFAULT);
		this.prop.setProperty(OWNERIDKEY, OWNERIDDEFAULT);
		this.prop.setProperty(SAVEDIRKEY, SAVEDIRDEFAULT);
		this.prop.setProperty(OPSPATHKEY, OPSPATHDEFAULT);
		this.prop.setProperty(TOKENKEY, TOKENDEFAULT);
		this.prop.setProperty(WEBENABLEDKEY, WEBENABLEDEFAULT);
		this.prop.setProperty(PORTKEY, PORTDEFAULT);
		this.prop.setProperty(CLIENTIDKEY, CLIENTIDDEFAULT);
		this.prop.setProperty(CLIENTSECRETKEY, CLIENTSECRETDEFAULT);
		this.prop.setProperty(REDIRECTURLKEY, REDIRECTURLDEFAULT);
		File file = new File(CONFPATH);
		if(!file.exists() && file.getParentFile() != null) file.getParentFile().mkdirs();
		try(OutputStream out = new FileOutputStream(file)){
			this.prop.store(out, "Full documentation can be found at TODO" //TODO
					+ "\nComment out lines with #"
					+ "\nCommented out lines will return to default");
		}catch(IOException e){
			Pokebot.LOGGER.error("There was an error writing the config file...", e);
		}
	}
}
