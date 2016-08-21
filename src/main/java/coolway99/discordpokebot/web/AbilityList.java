package coolway99.discordpokebot.web;

import coolway99.discordpokebot.states.Abilities;
import org.watertemplate.Template;

import java.util.Arrays;

public class AbilityList extends Template{

	private static String render = null;

	private AbilityList(){
		this.addCollection("abilities", Arrays.asList(Abilities.values()), (ability, map) -> {
			map.add("name", ability.toString());
			map.add("cost", Integer.toString(ability.getCost()));
		});
	}

	public static String getAbilityList(){
		if(render == null){
			render = new AbilityList().render();
		}
		return render;
	}

	@Override
	protected String getFilePath(){
		return "abilityList.html";
	}
}
