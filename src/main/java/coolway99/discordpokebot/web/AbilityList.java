package coolway99.discordpokebot.web;

import coolway99.discordpokebot.abilities.OldAbilities;
import org.watertemplate.Template;

import java.util.Arrays;

public final class AbilityList extends Template{

	private static String render = null;

	private AbilityList(){
		this.addCollection("abilities", Arrays.asList(OldAbilities.values()), (ability, map) -> {
			map.add("name", ability.toString().replace("_", " "));
			map.add("value", ability.toString());
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
