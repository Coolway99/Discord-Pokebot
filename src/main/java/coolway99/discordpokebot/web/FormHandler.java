package coolway99.discordpokebot.web;

import org.watertemplate.Template;
import sx.blah.discord.handle.obj.IUser;

public class FormHandler extends Template{

	public FormHandler(IUser user){
		this.add("user", user.getName());
		this.add("id", user.getID());
		this.add("typeList", TypeList.getTypeList());
	}

	@Override
	protected String getFilePath(){
		return "application.html";
	}
}
