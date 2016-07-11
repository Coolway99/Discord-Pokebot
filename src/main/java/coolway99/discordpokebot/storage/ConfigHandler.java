package coolway99.discordpokebot.storage;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintStream;
import java.util.Scanner;

public class ConfigHandler{
	
	public static final String BOTDIR = "/botdata";
	public static final String CONFIGPATH = BOTDIR+"/config";
	public final String SAVEDIR;
	public final String OWNERID;
	//DEFAULTS
	private static final String dSAVEDIR = BOTDIR+"/userpokemon/";
	private static final String dOWNERID = "";
	
	
	public ConfigHandler(){
		//DEFAULTS
		String SAVEDIR = dSAVEDIR;
		String OWNERID = dOWNERID;
		File file = new File(CONFIGPATH);
		
		while(true){ //Use a while loop here, so we can exit it anytime
			if(!file.exists()){
				createDefaultConfig();
				break;
			}
			try(Scanner in = new Scanner(file)){
			if(!in.hasNextLine()) break;
			SAVEDIR = in.nextLine();
			if(!in.hasNextLine()) break;
			OWNERID = in.nextLine();
			break;
			}catch(Exception e){
				e.printStackTrace();
				break;
			}
		}
		this.SAVEDIR = SAVEDIR;
		this.OWNERID = OWNERID;
	}
	
	private static void createDefaultConfig(){
		File file = new File(CONFIGPATH);
		file.getParentFile().mkdirs();
		try(PrintStream out = new PrintStream(file)){
			out.println(dSAVEDIR);
			out.println(dOWNERID);
			System.out.println("Generated default config at "+file.getAbsolutePath());
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("\nThere was an error creating a default config file");
		}
	}
}
