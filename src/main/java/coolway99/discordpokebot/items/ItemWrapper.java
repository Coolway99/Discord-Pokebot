package coolway99.discordpokebot.items;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.jsonUtils.JSONObject;
import coolway99.discordpokebot.moves.DamageCalculator;
import coolway99.discordpokebot.moves.MoveWrapper;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.obj.IChannel;

//A wrapper to Items from JSON
public class ItemWrapper{

	public final boolean isBerry;

	private final int cost;

	@Nullable
	private final ScriptObjectMirror onFlingFunction;
	@Nullable
	private final ScriptObjectMirror onNaturalGiftFunction;
	@Nullable
	private final ScriptObjectMirror onDamageModifierFunction;
	@Nullable
	private final ScriptObjectMirror onHit;
	@Nullable
	private final ScriptObjectMirror onFaintFunction;
	@Nullable
	private ScriptObjectMirror onBeforeEffectFunction;
	@Nullable
	private ScriptObjectMirror onAfterEffectFunction;

	@NotNull
	private final String name;
	@NotNull
	private final String description;

	@SuppressWarnings("FeatureEnvy")
	public ItemWrapper(JSONObject item){
		this.isBerry = item.getBoolean("isBerry", false);
		this.cost = item.getInt("cost", 0);

		this.onFlingFunction = item.getFunction("onFling");
		this.onNaturalGiftFunction = item.getFunction("onNaturalGift");
		this.onDamageModifierFunction = item.getFunction("onDamageModifier");
		this.onHit = item.getFunction("onHit");
		this.onFaintFunction = item.getFunction("onFaint");

		this.name = item.getString("name", "Default Item");
		this.description = item.getString("description", "There is no description yet for this item.");
	}

	@Contract(pure = true)
	public int getCost(){
		return this.cost;
	}

	@Nullable
	public DamageCalculator onFling(@NotNull IChannel channel, @NotNull DamageCalculator damageCalc){
		if(this.onFlingFunction == null) return null;
		return (DamageCalculator) this.onFlingFunction.call(this, channel, damageCalc);
	}

	public void onDamageModifier(@NotNull IChannel channel, @NotNull DamageCalculator damageCalc){
		if(this.onDamageModifierFunction == null) return;
		this.onDamageModifierFunction.call(this, channel, damageCalc);
	}

	/**
	 * When the defender holding this item gets hit. This is the defender-side on onAttack
	 * @param channel The channel this takes place in
	 * @param damageCalc The previously-done damage calculation
	 */
	public void onHit(@NotNull IChannel channel, @NotNull Player attacker, @NotNull MoveWrapper move, @NotNull Player defender){
		if(this.onHit == null) return;
		this.onHit.call(this, channel, attacker, move, defender);
	}

	public void onBeforeEffect(IChannel channel, Player player){
		if(this.onBeforeEffectFunction == null) return;
		this.onBeforeEffectFunction.call(this, channel, player);
	}

	public void onAfterEffect(IChannel channel, Player player){
		if(this.onAfterEffectFunction == null) return;
		this.onAfterEffectFunction.call(this, channel, player);
	}

	public void onFaint(@NotNull IChannel channel, @NotNull Player player){
		if(this.onFaintFunction == null) return;
		this.onFaintFunction.call(this, channel, player);
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

	@Override
	@NotNull
	@Contract(pure = true)
	public String toString(){
		return this.getName();
	}
}
