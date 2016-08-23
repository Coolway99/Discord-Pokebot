package coolway99.discordpokebot.web;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
import coolway99.discordpokebot.storage.PlayerHandler;
import org.watertemplate.Template;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Arrays;

public class StatGrid extends Template{

	public StatGrid(IUser user){
		this(PlayerHandler.getPlayer(user));
	}

	public StatGrid(Player player){
		this.add("baseMax", Integer.toString(StatHandler.MAX_TOTAL_STAT_POINTS));
		this.add("evMax", Integer.toString(StatHandler.MAX_TOTAL_EV_POINTS));
		this.add("singleBaseMax", Integer.toString(StatHandler.MAX_SINGLE_STAT_POINTS));
		this.add("singleIvMax", Integer.toString(StatHandler.MAX_SINGLE_IV_POINTS));
		this.add("singleEvMax", Integer.toString(StatHandler.MAX_SINGLE_EV_POINTS));
		ArrayList<Stats> statList = new ArrayList<>();
		statList.addAll(Arrays.asList(Stats.values()).subList(0, 6));
		this.addCollection("stats", statList, (stat, map) -> {
			map.add("value", stat.toString());
			map.add("name", stat.toString().replace("_", " "));
			map.add("base", Integer.toString(player.stats[stat.getIndex()][SubStats.BASE.getIndex()]));
			map.add("iv", Integer.toString(player.stats[stat.getIndex()][SubStats.IV.getIndex()]));
			map.add("ev", Integer.toString(player.stats[stat.getIndex()][SubStats.EV.getIndex()]));
		});
	}

	@Override
	protected String getFilePath(){
		return "statGrid.html";
	}
}
