package coolway99.discordpokebot.moves.rewrite;

import coolway99.discordpokebot.jsonUtils.JSONObject;
import coolway99.discordpokebot.moves.MoveType;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
import coolway99.discordpokebot.states.Types;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.TreeMap;

//Static objects should face inwards to Pokebot
//Non-static objects should face outwards to the javascript api
public class NewMoves{
	public static final NewMoves API = new NewMoves();
	public static final TreeMap<String, MoveWrapper> REGISTRY = new TreeMap<>(String::compareToIgnoreCase);

	public final Types[] types = Types.values();
	public final MoveType[] moveTypes = MoveType.values();
	public final Stats[] stats = Stats.values();
	public final SubStats[] subStats = SubStats.values();

	private NewMoves(){
		//Nothing is actually done in here, just making the constructor private.
	}

	public static void registerMoves(){
		//TODO Put some trickery here
	}

	public void register(String name, ScriptObjectMirror moveObject){
		JSONObject move = new JSONObject(moveObject);
		REGISTRY.put(name, new MoveWrapper(move));
	}
}