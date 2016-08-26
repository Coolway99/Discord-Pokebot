package coolway99.discordpokebot.web;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.StatHandler;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Moves;
import coolway99.discordpokebot.states.Natures;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
import coolway99.discordpokebot.states.Types;
import coolway99.discordpokebot.storage.PlayerHandler;
import spark.Spark;
import sx.blah.discord.handle.obj.IUser;

import java.net.URLEncoder;

public class WebInterface{

	private static final String SCOPE = "identify";

	public static void initWebInterface(String URL, int port){
		Spark.port(port);
		Spark.get("/authorize", (req, res) -> {
			try{
				res.redirect("https://discordapp.com/oauth2/authorize?response_type=code&scope="+SCOPE+"&client_id"+
						"="+Pokebot.config.CLIENT_ID+"&redirect_uri="+URLEncoder.encode(URL, "UTF-8"));
			}catch(Exception e){
				e.printStackTrace();
			}
			return "redirecting";
		});
		Spark.get("/", (req, res) -> {
			String code = req.queryParams("code");
			if(code == null || code.equals("") || code.equals("null")){
				res.redirect("/authorize");
				return "redirecting";
			}
			String token = OAuth_Handler.getToken(req.queryParams("code"));
			String id = OAuth_Handler.getUserID(token);
			if(id == null){
				return "ERROR: UNABLE TO GET USER ID";
			}
			IUser user = Pokebot.client.getUserByID(id);
			if(user == null){
				return "ERROR: UNABLE TO GET USER OBJECT";
			}
			System.out.println("Preparing to render");
			String render;
			try{
				render = new FormHandler(user, token).render();
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			return render;
			//return new FormHandler(user, token).render();
		});
		Spark.post("/submit", (req, res) -> {
			try{
				String token = req.headers("token");
				String id = OAuth_Handler.getUserID(token);
				if(id == null){
					return "Authorization error: Reload the application again (did you leave it open for days on end?)";
				}
				if(!id.equals(req.headers("id"))){
					return "Authorization error: ID mismatch (have you been tinkering with me?)";
				}

				Moves[] moves = new Moves[4];
				try{
					String[] moveNames = {req.headers("move1"), req.headers("move2"), req.headers("move3"), req.headers("move4")};
					for(int x = 0; x < moveNames.length; x++){
						String moveSelector = moveNames[x];
						int cost = Integer.parseInt(moveSelector.substring(0, moveSelector.indexOf('|')));
						String moveName = moveSelector.substring(moveSelector.indexOf('|')+1);
						if(moveName.equals("NONE")) moveName = "NULL";
						Moves move = Moves.valueOf(moveName);
						if(cost != move.getCost()) return "Move Checksum Error (did you try setting a move manually?)";
						moves[x] = move;
					}
				} catch(IllegalArgumentException e){
					return "Move Checksum Error (did you try setting a move manually?)";
				}

				Types primary, secondary;
				try{
					primary = Types.valueOf(req.headers("primary"));
					if(primary == Types.NULL) return "Type Checksum Error (did you try setting a type manually?)"; //Sanity Check
					String s = req.headers("secondary");
					if(s.equals("NONE")) s = "NULL";
					secondary = Types.valueOf(s);
				} catch(IllegalArgumentException e){
					return "Type Checksum Error (did you try setting a type manually?)";
				}

				int[][] stats = new int[6][3];
				try{
					for(int x = 0; x < stats.length; x++){
						Stats stat = Stats.getStatFromIndex(x);
						for(int y = 0; y < stats[x].length; y++){
							SubStats subStat = SubStats.getSubStatFromIndex(y);
							int val = Integer.parseInt(req.headers(stat.toString()+'.'+subStat.toString()));
							if(val < 0 || val > StatHandler.MAX_SINGLE_SUBSTATS[subStat.getIndex()]){
								return "Stat Error: "+stat+" "+subStat+" "+val+" is outside of the range";
							}
							stats[x][y] = val;
						}
					}
				} catch(NumberFormatException e){
					return "Stat Error: Are you sure you only entered numbers into the stat grid?";
				}

				Abilities ability;
				try{
					String abilitySelector = req.headers("ability");
					int cost = Integer.parseInt(abilitySelector.substring(0, abilitySelector.indexOf('|')));
					String abilityName = abilitySelector.substring(abilitySelector.indexOf('|')+1);
					ability = Abilities.valueOf(abilityName);
					if(ability.getCost() != cost){
						return "Ability Checksum Error (did you try setting an ability manually?)";
					}
				} catch(IllegalArgumentException e){
					return "Ability Checksum Error (did you try setting an ability manually?)";
				}

				int level;
				try{
					level = Integer.parseInt(req.headers("level"));
					if(level > StatHandler.MAX_LEVEL || level < 1){
						return "Level Checksum Error (did you set it outside the range?)";
					}
				} catch(IllegalArgumentException e){
					return "Level Checksum Error (are you sure it's a number?)";
				}

				Natures nature;
				try{
					String natureSelector = req.headers("nature");
					nature = Natures.valueOf(natureSelector.substring(natureSelector.indexOf('|')+1));
					//TODO checksum? idk
				}catch(IllegalArgumentException e){
					return "Nature Checksum Error (have you been messing with my source code?)";
				}

				Player player = PlayerHandler.getPlayer(Pokebot.client.getUserByID(id));
				if(player.inBattle()) return "Error: You are in a battle, your stats cannot be set";
				player.numOfAttacks = 0;
				for(int x = 0; x < player.moves.length; x++){
					player.moves[x] = moves[x];
					player.PP[x] = moves[x].getPP();
					if(moves[x] != Moves.NULL){
						player.numOfAttacks++;
					}
				}
				player.primary = primary;
				player.secondary = secondary;
				player.setAbility(ability);
				for(int x = 0; x < player.stats.length; x++){
					player.stats[x] = stats[x];
				}
				player.level = level;
				player.nature = nature;
				return "Set your stats successfully!";
			}catch(NullPointerException e){
				return "Ah, ye tried not specifying something, didn'tcha?";
			}catch(Exception e){
				e.printStackTrace();
				return "A unknown error occurred, please report this to the bot author:\n" + e.getCause();
			}
		});
		//Spark.get("/test", (req, res) -> new FormHandler(Pokebot.client.getUserByID(Pokebot.config.OWNERID), "WIP").render());
		/*Spark.get("/submit", (req, res) -> {
			res.redirect("/application", 307);
			return "INVALID METHOD, REDIRECTING TO APPLICATION";
		});*/
	}
}