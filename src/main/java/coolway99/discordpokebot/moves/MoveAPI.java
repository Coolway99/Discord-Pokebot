package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.jsonUtils.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.TreeMap;

public class MoveAPI{

	private static final TreeMap<String, MoveWrapper> REGISTRY = new TreeMap<>(String::compareToIgnoreCase);

	@Nullable
	@Contract(pure = true)
	public static MoveWrapper getMove(@NotNull String[] args){
		return getMove(0, args);
	}

	//Used to be more intelligent to people using spaces
	@Nullable
	@Contract(pure = true)
	public static MoveWrapper getMove(int offset, @NotNull String[] args){
		String name = "";
		for(int x = offset; x < args.length; x++){
			name += args[x];
			if(x != args.length-1) name += " ";
		}
		return getMove(name);
	}

	@Nullable
	@Contract(value = "null -> null", pure = true)
	public static MoveWrapper getMove(@NotNull String name){
		MoveWrapper move = REGISTRY.get(name);
		if(move == null) move = REGISTRY.get(name.replaceAll(" ", ""));
		return move;
	}

	public static Collection<MoveWrapper> getAllMoves(){
		return REGISTRY.values();
	}

	public static void setUpMoves(){
		System.out.println("Registering moves");
		//Registering the moves o3o
		File files = Pokebot.getResource("scripting/moves/");
		if(files == null){
			System.err.println("Error, unable to load any moves");
			return;
		}
		for(File file : files.listFiles()){
			if(file.isDirectory()) continue;
			if(file.isHidden()) continue;
			try{
				Pokebot.engine.eval(new FileReader(file));
			} catch(ScriptException e){
				System.err.println("Error in file "+file.getName());
				System.err.println(e.getMessage());
			} catch(FileNotFoundException e){
				System.err.println("Unable to find file "+file.getName());
			}
		}
	}

	/**
	 * A method for JSON to register moves
	 * @param moveObject The move to register
	 */
	public static void register(ScriptObjectMirror moveObject){
		MoveWrapper move = new MoveWrapper(new JSONObject(moveObject));
		REGISTRY.put(move.getName(), move);
	}

	/**
	 * An overload that accepts an array of moves
	 * @param moves The list of moves to register
	 */
	public static void register(ScriptObjectMirror[] moves){
		for(ScriptObjectMirror move : moves){
			register(move);
		}
	}
}