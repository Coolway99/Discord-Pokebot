package coolway99.discordpokebot.moves.rewrite;

import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.jsonUtils.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.TreeMap;

//Static objects should face inwards to Pokebot
//Non-static objects should face outwards to the javascript api
public class NewMoves{
	public static final NewMoves INSTANCE = new NewMoves();
	public static final TreeMap<String, MoveWrapper> REGISTRY = new TreeMap<>(String::compareToIgnoreCase);

	private NewMoves(){
		//Nothing is actually done in here, just making the constructor private.
	}

	public static void registerMoves(){
		//TODO Put some trickery here
		//TODO Move the engine setup
		try{
			//noinspection ConstantConditions
			Pokebot.engine.eval(new FileReader(Pokebot.getResource("scripting/engine.js")));
		} catch(ScriptException | FileNotFoundException e){
			e.printStackTrace();
		}
	}

	public void registerMove(ScriptObjectMirror moveObject){
		MoveWrapper move = new MoveWrapper(new JSONObject(moveObject));
		if(move.getName() == null) return;
		REGISTRY.put(move.getName(), move);
	}

	public void registerMoves(ScriptObjectMirror moveObject){
		ScriptObjectMirror[] moves = moveObject.to(ScriptObjectMirror[].class);
		for(ScriptObjectMirror move : moves){
			this.registerMove(move);
		}
	}
}