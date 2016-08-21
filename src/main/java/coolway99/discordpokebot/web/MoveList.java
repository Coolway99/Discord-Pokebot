package coolway99.discordpokebot.web;

import coolway99.discordpokebot.states.Moves;
import coolway99.discordpokebot.states.Types;
import org.watertemplate.Template;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * This was a copy/paste of TypeList
 */
public class MoveList extends Template{

	private static String render = null;

	private MoveList(){
		this.addCollection("moves", Arrays.asList(Moves.values()), (move, map) -> {
			if(move == Moves.NULL){
				map.add("name", "NONE");
				map.add("cost", "0");
				return;
			}
			map.add("name", move.toString().replace("_", " "));
			map.add("cost", Integer.toString(move.getCost()));
		});
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
