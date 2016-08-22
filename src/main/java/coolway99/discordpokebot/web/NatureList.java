package coolway99.discordpokebot.web;

import coolway99.discordpokebot.states.Natures;
import org.watertemplate.Template;

import java.util.Arrays;

public final class NatureList extends Template{

	private static String render = null;

	private NatureList(){
		super();
		this.addCollection("natures", Arrays.asList(Natures.values()), (nature, map) -> {
			map.add("value", getNatureIdentifier(nature));
			map.add("name", nature.getExpandedText().replaceAll("_", " "));
		});
	}

	public static String getNatureIdentifier(Natures nature){
		String value = nature.toString();
		if(nature.hasEffect()){
			String identifier = "";
			identifier += nature.increase.getIndex();
			identifier += nature.decrease.getIndex();
			identifier += '|';
			value = identifier + value;
		} else {
			value = "00|"+value;
		}
		return value;
	}

	public static String getNatureList(){
		if(render == null){
			render = new NatureList().render();
		}
		return render;
	}

	@Override
	protected String getFilePath(){
		return "natureList.html";
	}
}
