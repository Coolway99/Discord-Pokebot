package coolway99.discordpokebot.web;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.moves.Move;
import coolway99.discordpokebot.states.Types;
import coolway99.discordpokebot.storage.PlayerHandler;
import org.watertemplate.Template;
import sx.blah.discord.handle.obj.IUser;

import java.util.Random;

public class FormHandler extends Template{

	public FormHandler(IUser user, String token, String code, String ran, byte slot){
		PlayerHandler.switchSlot(user, slot);
		Player player = PlayerHandler.getPlayer(user);
		this.add("maxPoints", Integer.toString(StatHandler.MAX_TOTAL_POINTS));
		this.add("maxLevel", Integer.toString(StatHandler.MAX_LEVEL));
		this.add("maxSlot", Byte.toString(PlayerHandler.MAX_SLOTS));
		this.add("slot", Integer.toString(slot+1));
		this.add("code", code);
		this.add("user", user.getName());
		this.add("id", user.getID());
		this.add("level", Integer.toString(player.level));
		this.add("nature", NatureList.getNatureIdentifier(player.nature));
		this.add("typeList", TypeList.getTypeList());
		this.add("moveList", MoveList.getMoveList());
		this.add("abilityList", AbilityList.getAbilityList());
		this.add("natureList", NatureList.getNatureList());
		this.add("statGrid", new StatGrid(player).render());
		this.add("token", token);
		this.add("primary", player.primary.toString());
		if(player.secondary == Types.NULL){
			this.add("secondary", "NONE");
		} else {
			this.add("secondary", player.secondary.toString());
		}
		for(int x = 0; x < 4; x++){
			MoveSet moveSet = player.moves[x];
			if(moveSet == null){
				this.add("move"+(x+1), "0|NONE");
			} else {
				Move move = moveSet.getMove();
				this.add("move"+(x+1), move.getCost()+"|"+move.getName());
			}
		}
		this.add("ran", ran);
		Abilities ability = player.getAbility();
		this.add("ability", ability.getCost()+"|"+ability.toString());
	}

	@Override
	protected String getFilePath(){
		return "application.html";
	}
}
