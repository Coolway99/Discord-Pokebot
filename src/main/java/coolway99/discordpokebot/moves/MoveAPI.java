package coolway99.discordpokebot.moves;

import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.jsonUtils.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.TreeMap;

//Static objects should face inwards to Pokebot
//Non-static objects should face outwards to the javascript api
public class MoveAPI{
	public static final TreeMap<String, MoveWrapper> REGISTRY = new TreeMap<>(String::compareToIgnoreCase);

	public static void registerMoves(){
		//TODO Put some trickery here
		//TODO Move the engine setup
		try{
			//noinspection ConstantConditions
			Pokebot.engine.eval(new FileReader(Pokebot.getResource("scripting/engine.js")));
		} catch(ScriptException | FileNotFoundException e){
			e.printStackTrace();
		}

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

	public static void registerMove(ScriptObjectMirror moveObject){
		MoveWrapper move = new MoveWrapper(new JSONObject(moveObject));
		if(move.getName() == null) return;
		REGISTRY.put(move.getID(), move);
	}

	public static void registerMoves(ScriptObjectMirror moveObject){
		ScriptObjectMirror[] moves = moveObject.to(ScriptObjectMirror[].class);
		for(ScriptObjectMirror move : moves){
			registerMove(move);
		}
	}
}