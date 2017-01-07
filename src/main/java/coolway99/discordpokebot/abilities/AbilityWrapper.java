package coolway99.discordpokebot.abilities;

import coolway99.discordpokebot.Context;
import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.jsonUtils.JSONObject;
import coolway99.discordpokebot.moves.DamageCalculationHelper;
import coolway99.discordpokebot.moves.MoveWrapper;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//A wrapper to Abilities from JSON
public class AbilityWrapper{

	private final int cost;
	//private final EnumSet<MoveFlags> flags;

	@Nullable
	private final ScriptObjectMirror onBeginFunction;
	@Nullable
	private final ScriptObjectMirror onDamageModifierFunction;
	//@Nullable
	//private final ScriptObjectMirror onBeforeFunction;
	@Nullable
	private final ScriptObjectMirror onAttackFunction;
	@Nullable
	private final ScriptObjectMirror onFaintFunction;
	@Nullable
	private final ScriptObjectMirror onEndFunction;

	private final String name;
	private final String description;

	@SuppressWarnings("FeatureEnvy")
	public AbilityWrapper(JSONObject move){
		this.cost = move.getInt("cost", 0);

		//this.flags = EnumSet.noneOf(MoveFlags.class);
		//this.flags.addAll(Arrays.asList(move.getArray("flags", MoveFlags.class,
		//		new MoveFlags[]{MoveFlags.MAKES_CONTACT, MoveFlags.CAN_BE_MIRRORED})));

		this.onBeginFunction = move.getFunction("onBegin");
		this.onAttackFunction = move.getFunction("onAttack");
		this.onDamageModifierFunction = move.getFunction("onDamageModifier");
		this.onEndFunction = move.getFunction("onEnd");
		this.onFaintFunction = move.getFunction("onFaint");

		this.name = move.getString("name", "Default Ability");
		this.description = move.getString("description", "There is no description yet for this ability.");
	}

	@Contract(pure = true)
	public int getCost(){
		return this.cost;
	}

	//public EnumSet<MoveFlags> getFlags(){
	//	return this.flags;
	//}

	/**
	 * Ran when we enter battle
	 * @param context The context we're doing this in
	 * @param player The player who has this ability
	 */
	public void onBegin(@NotNull Context context, @NotNull Player player){
		if(this.onBeginFunction == null) return;
		this.onBeginFunction.call(this, context, player);
	}

	/**
	 * Called as an adjustment (multiplying) how much damage the defender takes. It's only a modifier
	 * @param damageCalc The pending calculation
	 * @param attacker The one dealing the attack
	 * @param move The move being used
	 * @param defender The one defending from the move
	 */
	public void onDamageModifier(@NotNull DamageCalculationHelper damageCalc, Player attacker, MoveWrapper move, Player defender){
		if(this.onDamageModifierFunction == null) return;
		this.onDamageModifierFunction.call(this, damageCalc, attacker, move, defender);
	}

/*	/**
	 * OnBeforeAttack, ran before the attack is done.
	 * @param attacker The one running the attack
	 * @param defender The one defending from the attack
	 * @return If to continue or not
	 * Can be null or a function
	 *//*
	@Contract(pure = true)
	public boolean onBeforeAttack(Context context, Player attacker, Player defender){
		if(this.onBeforeFunction == null) return true;
		Boolean b = (Boolean) this.onBeforeFunction.call(this, context, attacker, defender);
		if(b == null) return true;
		return b;
	}*/

	/**
	 * On attack, this is ran before moves
	 * @param context The context this takes place in
	 * @param attacker The attacker, aka the one performing the attack
	 * @param defender The defender, aka the one receiving the attack
	 */
	public void onAttack(@NotNull Context context, Player attacker, MoveWrapper move, Player defender){
		if(this.onAttackFunction == null) return;
		this.onAttackFunction.call(this, context, attacker, move, defender);
	}

	public void onFaint(@NotNull Context context, @NotNull Player player){
		if(this.onFaintFunction == null) return;
		this.onFaintFunction.call(this, context, player);
	}

	/**
	 * OnEnd, ran after this player leaves the battle (I.E. switching out) or the ability is canceled somehow
	 * @param player The player who "owns" this ability
	 */
	public void onEnd(@NotNull Context context, @NotNull Player player){
		if(this.onEndFunction == null) return;
		this.onEndFunction.call(this, context, player);
	}

	@NotNull
	@Contract(pure = true)
	public String getName(){
		return this.name;
	}

	@NotNull
	@Contract(pure = true)
	public String getDescription(){
		return this.description;
	}

//	public boolean has(MoveFlags flag){
//		return this.getFlags().contains(flag);
//	}

	@Override
	public String toString(){
		return this.getName();
	}
}
