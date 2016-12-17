package coolway99.discordpokebot.moves.rewrite;

import com.google.gson.annotations.Expose;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.jsonUtils.JSONObject;
import coolway99.discordpokebot.moves.Battle_Priority;
import coolway99.discordpokebot.moves.MoveType;
import coolway99.discordpokebot.states.Types;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;

//A wrapper to Moves from JSON
public class MoveWrapper{

	private final Types type;
	private final int power;
	private final MoveType category;
	private final int PP;
	private final int accuracy;
	private final Battle_Priority priority;
	private final int cost;
	private final EnumSet<MoveFlags> flags;

	@Nullable
	private final ScriptObjectMirror onBeforeFunction;
	private final ScriptObjectMirror onAttackFunction;
	@Nullable
	private final ScriptObjectMirror onSecondaryFunction;

	private final String name;
	private final String displayName;

	@SuppressWarnings("FeatureEnvy")
	public MoveWrapper(JSONObject move){
		this.type = (Types) move.getObject("type");
		this.power = move.getInt("power", 0);
		this.category = (MoveType) move.getObject("category");
		this.PP = move.getInt("pp");
		this.accuracy = move.getInt("accuracy");
		this.priority = Battle_Priority.getPriority(move.getString("priority"));
		this.cost = move.getInt("cost");
		this.flags = EnumSet.copyOf(Arrays.asList((MoveFlags[]) move.getObject("flags")));

		this.onBeforeFunction = (ScriptObjectMirror) move.getRoot().get("onBeforeAttack");
		this.onAttackFunction = (ScriptObjectMirror) move.getRoot().get("onAttack");
		this.onSecondaryFunction = (ScriptObjectMirror) move.getRoot().get("OnSecondary");

		this.name = move.getString("id");
		String displayName = move.getString("name");
		if(displayName == null){
			this.displayName = this.name;
		} else {
			this.displayName = displayName;
		}
	}

	public Types getType(){
		return this.type;
	}

	public int getPower(){
		return this.power;
	}

	public MoveType getCategory(){
		return this.category;
	}

	public int getPP(){
		return this.PP;
	}

	public int getAccuracy(){
		return this.accuracy;
	}

	public Battle_Priority getPriority(){
		return this.priority;
	}

	public int getCost(){
		return this.getCost();
	}

	public EnumSet<MoveFlags> getFlags(){
		return this.flags;
	}

	/**
	 * OnBeforeAttack, ran before the attack is done.
	 * @param attacker The one running the attack
	 * @param defender The one defending from the attack
	 * @return If to continue or not
	 * Can be null or a function
	 */
	public boolean onBeforeAttack(Player attacker, Player defender){
		if(this.onBeforeFunction == null) return true;
		return (boolean) this.onBeforeFunction.call(NewMoves.API, attacker, defender);
	}

	/**
	 * The main method for moves. This must always be defined
	 * @param attacker
	 * @param defender
	 */
	public void onAttack(Player attacker, Player defender){
		this.onAttackFunction.call(NewMoves.API, attacker, defender);
	}

	/**
	 * OnSecondary, ran after attacking
	 * @param attacker The one running the attack
	 * @param defender The one defending from the attack
	 * If null or false, then it is skipped, otherwise it must be a function
	 */
	public void onSecondary(Player attacker, Player defender, int damageDealt){
		if(this.onSecondaryFunction == null) return;
		if(!this.onSecondaryFunction.isFunction()) return;
		this.onSecondaryFunction.call(NewMoves.API, attacker, defender, damageDealt);
	}

	public String getName(){
		return this.displayName;
	}

	public String getDisplayName(){
		return this.displayName;
	}
}
