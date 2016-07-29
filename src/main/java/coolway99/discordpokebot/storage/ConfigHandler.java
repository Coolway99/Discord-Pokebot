package coolway99.discordpokebot.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigHandler{	
	
	public static final String CONFPATH = "pokebot.conf"; //WITHOUT THE "." IT CRASHES
	public final String BOTNAME;
	public final String COMMAND_PREFIX;
	public final String SAVEDIR;
	public final String OPSPATH;
	public final String OWNERID;
	
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
	
	public ConfigHandler(){
		Properties config = new Properties();
		File file = new File(CONFPATH);
		if(file.exists()){
			try(FileInputStream in = new FileInputStream(file)){
				config.load(in);
			}catch(IOException e){
				e.printStackTrace();
				System.out.println("\nThere was an error reading the config file...");
			}
		}
		this.BOTNAME = config.getProperty(NAMEKEY, NAMEDEFAULT);
		this.COMMAND_PREFIX = config.getProperty(COMMANDKEY, COMMANDDEFAULT);
		this.OWNERID = config.getProperty(OWNERIDKEY, OWNERIDDEFAULT);
		this.SAVEDIR = config.getProperty(SAVEDIRKEY, SAVEDIRDEFAULT);
		this.OPSPATH = config.getProperty(OPSPATHKEY, OPSPATHDEFAULT);
		
		this.prop = config;
		
		if(!file.exists()){
			System.out.println("Detected first time run, saving config and stopping");
			this.saveDefaultConfig();
			System.out.println("Saved config file to "+file.getAbsolutePath());
			System.exit(0);
		}
	}
	
	private void saveDefaultConfig(){
		this.prop.setProperty(NAMEKEY, NAMEDEFAULT);
		this.prop.setProperty(COMMANDKEY, COMMANDDEFAULT);
		this.prop.setProperty(OWNERIDKEY, OWNERIDDEFAULT);
		this.prop.setProperty(SAVEDIRKEY, SAVEDIRDEFAULT);
		this.prop.setProperty(OPSPATHKEY, OPSPATHDEFAULT);
		File file = new File(CONFPATH);
		if(!file.exists() && file.getParentFile() != null) file.getParentFile().mkdirs();
		try(OutputStream out = new FileOutputStream(file)){
			this.prop.store(out, "Full documentation can be found at TODO"
					+ "\nComment out lines with #"
					+ "\nCommented out lines will return to default");
		}catch(IOException e){
			e.printStackTrace();
			System.err.println("\nThere was an error writing the config file...");
		}
	}
}
