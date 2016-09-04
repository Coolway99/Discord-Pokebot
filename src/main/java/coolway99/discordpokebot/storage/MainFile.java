package coolway99.discordpokebot.storage;

import coolway99.discordpokebot.Pokebot;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

public class MainFile{

	private final IUser user;

	public byte lastSlot = 0;

	public MainFile(IUser user){
		this.user = user;
		this.loadData();
	}

	public void loadData(){
		File file = Pokebot.getMainFile(this.user);
		if(!file.exists()) return;
		try(Scanner scanner = new Scanner(file)){
			this.lastSlot = scanner.nextByte();
		} catch(Exception e){
			e.printStackTrace();
			System.err.println("\nUnable to load main file for "+this.user.getID());
		}
	}

	public void saveData(){
		File file = Pokebot.getMainFile(this.user);
		if(!file.exists()){
			if(!file.getParentFile().exists() && !file.getParentFile().mkdirs()){
				System.err.println("There was an error creating a directory structure for "+this.user.getID());
				return;
			}
		}
		try(PrintStream out = new PrintStream(file)){
			out.println(this.lastSlot);
		} catch(Exception e){
			e.printStackTrace();
			System.err.println("\nUnable to save main file for "+this.user.getID());
		}
	}
}
