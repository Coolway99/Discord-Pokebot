package coolway99.discordpokebot.web;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Moves;
import coolway99.discordpokebot.states.Types;
import coolway99.discordpokebot.storage.PlayerHandler;
import org.watertemplate.Template;
import sx.blah.discord.handle.obj.IUser;

public class FormHandler extends Template{

	public FormHandler(IUser user, String token){
		this(PlayerHandler.getPlayer(user), token);
	}

	public FormHandler(Player player, String token){
		this.add("maxPoints", Integer.toString(StatHandler.MAX_TOTAL_POINTS));
		this.add("maxLevel", Integer.toString(StatHandler.MAX_LEVEL));
		this.add("user", player.user.getName());
		this.add("id", player.user.getID());
		this.add("level", Integer.toString(player.level));
		this.add("typeList", TypeList.getTypeList());
		this.add("moveList", MoveList.getMoveList());
		this.add("abilityList", AbilityList.getAbilityList());
		this.add("statGrid", new StatGrid(player).render());
		this.add("token", token);
		this.add("primary", player.primary.toString());
		if(player.secondary == Types.NULL){
			this.add("secondary", "NONE");
		} else {
			this.add("secondary", player.secondary.toString());
		}
		for(int x = 0; x < 4; x++){
			Moves move = player.moves[x];
			if(move == Moves.NULL){
				this.add("move"+(x+1), "0|NONE");
			} else {
				this.add("move"+(x+1), move.getCost()+"|"+move.toString().replaceAll("_", " "));
			}
		}
		Abilities ability = player.getAbility();
		this.add("ability", ability.getCost()+"|"+ability.toString().replace("_", " "));
	}

	@Override
	protected String getFilePath(){
		return "application.html";
	}
}
