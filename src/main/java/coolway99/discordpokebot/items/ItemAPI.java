package coolway99.discordpokebot.items;

import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.jsonUtils.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.TreeMap;

public class ItemAPI{

	private static final TreeMap<String, ItemWrapper> REGISTRY = new TreeMap<>(String::compareToIgnoreCase);

	@NotNull
	@Contract(pure = true)
	public static ItemWrapper getItem(@NotNull String[] args){
		return getItem(0, args);
	}

	@NotNull
	@Contract(pure = true)
	public static ItemWrapper getItem(int offset, @NotNull String[] args){
		String name = "";
		for(int x = offset; x < args.length; x++){
			name += args[x];
			if(x != args.length-1) name += " ";
		}
		return getItem(name);
	}

	@NotNull
	@Contract(pure = true)
	public static ItemWrapper getItem(@NotNull String name){
		ItemWrapper item = REGISTRY.get(name);
		if(item == null) item = REGISTRY.get(name.replaceAll(" ", ""));
		return item;
	}

	@NotNull
	public static Collection<ItemWrapper> getAllItems(){
		return REGISTRY.values();
	}

	public static void setUpItems(){
		System.out.println("Registering items");
		File files = Pokebot.getResource("scripting/items/");
		if(files == null || !files.isDirectory()){
			System.err.println("Error, unable to load any items");
			return;
		}
		File[] itemList = files.listFiles();
		if(itemList == null){
			System.err.println("Error, no items found");
			return;
		}
		for(File file : itemList){
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
		//assert REGISTRY.get("None") != null
		if(REGISTRY.get("None") == null){
			System.err.println("No default item named \"None\", exiting");
			System.exit(12);
		}
	}

	/**
	 * A method for JSON to register moves
	 * @param itemObject The move to register
	 */
	public static void register(ScriptObjectMirror itemObject){
		ItemWrapper move = new ItemWrapper(new JSONObject(itemObject));
		REGISTRY.put(move.getName(), move);
	}

	/**
	 * An overload that accepts an array of moves
	 * @param items The list of moves to register
	 */
	public static void register(ScriptObjectMirror[] items){
		for(ScriptObjectMirror item : items){
			register(item);
		}
	}

	@NotNull
	@Contract(pure = true)
	public static ItemWrapper getDefaultItem(){
		ItemWrapper item = getItem("None");
		return item;
	}
}