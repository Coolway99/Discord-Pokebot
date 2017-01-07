package coolway99.discordpokebot.abilities;

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

//Static objects should face inwards to Pokebot
//Non-static objects should face outwards to the javascript api
public class AbilityAPI{

	private static final TreeMap<String, AbilityWrapper> REGISTRY = new TreeMap<>(String::compareToIgnoreCase);

	@NotNull
	@Contract(value = "_, null -> fail", pure = true)
	public static AbilityWrapper getAbility(int offset, @NotNull String[] args){
		String name = "";
		for(int x = offset; x < args.length; x++){
			name += args[x];
			if(x != args.length-1) name += " ";
		}
		return getAbility(name);
	}

	@NotNull
	@Contract(pure = true)
	public static AbilityWrapper getAbility(String name){
		AbilityWrapper ability = REGISTRY.get(name.toUpperCase());
		if(ability == null) ability = REGISTRY.get(name.replaceAll(" ", "").toUpperCase());
		if(ability == null) return getDefaultAbility();
		return ability;
	}

	@NotNull
	@Contract(pure = true)
	public static Collection<AbilityWrapper> getAllAbilities(){
		return REGISTRY.values();
	}

	public static void setUpAbilities(){
		System.out.println("Registering abilities");
		File files = Pokebot.getResource("scripting/abilities/");
		if(files == null){
			System.err.println("Error, unable to load any abilities");
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
		if(REGISTRY.get("None".toUpperCase()) == null){
			System.err.println("No default ability named \"None\", exiting");
			System.exit(12);
		}
	}

	public static void register(ScriptObjectMirror abilObject){
		AbilityWrapper ability = new AbilityWrapper(new JSONObject(abilObject));
		REGISTRY.put(ability.getName(), ability);
	}

	public static void register(ScriptObjectMirror[] abilities){
		for(ScriptObjectMirror ability : abilities){
			register(ability);
		}
	}

	@NotNull
	public static AbilityWrapper getDefaultAbility(){
		AbilityWrapper ability = getAbility("None");
		return ability;
	}
}