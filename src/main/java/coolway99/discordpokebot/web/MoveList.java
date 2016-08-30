package coolway99.discordpokebot.web;

import coolway99.discordpokebot.moves.Move;
import org.watertemplate.Template;

import java.util.Arrays;

/*
 * This was a copy/paste of TypeList
 */
public final class MoveList extends Template{

	private static final String noMove = "0|NONE";

	private static String render = null;

	private MoveList(){
		this.addCollection("moves", Move.REGISTRY.values(), (move, map) -> {
			/*map.add("name", "NONE");
			map.add("value", "NONE");
			map.add("cost", "0");*/
			map.add("name", move.getDisplayName());
			map.add("value", move.getName());
			map.add("cost", Integer.toString(move.getCost()));
		});
		this.add("noMove", MoveList.noMove);
	}

	public static String getMoveList(){
		//This doesn't have to be thread-safe. It should produce the same result every time regardless, so if two threads
		//hit it at once, worst-case they both end up rendering the same thing
		if(render == null){
			render = new MoveList().render();
		}
		return render;
	}


	@Override
	protected String getFilePath(){
		return "moveList.html";
	}
}
