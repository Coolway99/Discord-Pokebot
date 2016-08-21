package coolway99.discordpokebot.web;

import coolway99.discordpokebot.states.Types;
import org.watertemplate.Template;

import java.util.ArrayList;
import java.util.Arrays;

public class TypeList extends Template{

	private static String render = null;

	private TypeList(){
		ArrayList<Types> typesList = new ArrayList<>();
		for(Types type : Types.values()){
			if(type == Types.NULL) continue;
			typesList.add(type);
		}
		this.addCollection("Ptypes", typesList, (type, map) -> map.add("name", type.toString()));
		this.addCollection("Stypes", Arrays.asList(Types.values()), (type, map) -> {
			String s;
			if(type == Types.NULL){
				s = "NONE";
			} else {
				s = type.toString();
			}
			map.add("name", s);
		});
	}

	public static String getTypeList(){
		//This doesn't have to be thread-safe. It should produce the same result every time regardless, so if two threads
		//hit it at once, worst-case they both end up rendering the same thing
		if(render == null){
			render = new TypeList().render();
		}
		return render;
	}


	@Override
	protected String getFilePath(){
		return "typeList.html";
	}
}
