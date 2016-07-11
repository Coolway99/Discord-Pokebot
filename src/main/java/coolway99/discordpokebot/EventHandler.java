package coolway99.discordpokebot;

import coolway99.discordpokebot.StatHandler.Stats;
import coolway99.discordpokebot.StatHandler.SubStats;
import coolway99.discordpokebot.battle.BattleManager;
import coolway99.discordpokebot.storage.PlayerHandler;
import coolway99.discordpokebot.types.Types;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

@SuppressWarnings("static-method")
public class EventHandler{
	
	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) throws MissingPermissionsException, HTTP429Exception, DiscordException{
		IMessage message = event.getMessage();
		if(message.mentionsEveryone()) return;
		/*if(message.toString().toLowerCase().contains("pokemon go")){
			Pokebot.sendMessage(message.getChannel(), "Our servers are experiencing issues. Please come back later");
			return;
		}*/
		if(!message.toString().startsWith(Pokebot.COMMAND_PREFIX)) return;
		String[] args = message.toString().split(" ");
		try{
		switch(args[0].toLowerCase().replace(Pokebot.COMMAND_PREFIX, "")){
			case "type":{
				Player player = PlayerHandler.getPlayer((message.getMentions().isEmpty() ?
						message.getAuthor() : message.getMentions().get(0)));
				Pokebot.sendMessage(message.getChannel(), player.getUser().mention()+" is type "+player.primary.toString()
						+(player.hasSecondaryType() ? " with secondary type "+player.secondary.toString()
						 : ""));
				break;
			}
			case "getusedpoints":
			case "gup":{
				Player player = PlayerHandler.getPlayer((message.getMentions().isEmpty() ?
						message.getAuthor() : message.getMentions().get(0)));
				Pokebot.sendMessage(message.getChannel(), player.getUser().mention()
						+" has a total point count of "+StatHandler.getTotalPoints(player));
				break;
			}
			case "gs":
			case "stats":
			case "getstats":{
				Player player = PlayerHandler.getPlayer((message.getMentions().isEmpty() ?
						message.getAuthor() : message.getMentions().get(0)));
				StringBuilder builder = new StringBuilder(player.getUser().mention());
				builder.append(" has:\n");
				builder.append(Stats.HEALTH.toString()).append(": ");
				builder.append(player.HP).append('/').append(player.getMaxHP()).append("HP");
				for(int x = 1; x < player.stats.length; x++){
					builder.append('\n');
					builder.append(Stats.getStatFromIndex(x).toString()).append(": ");
					builder.append(player.getStatFromIndex(x));
					if(player.modifiers[x] != 0){
						builder.append(String.format(" (%+d)",player.modifiers[x]));
					}
				}
				Pokebot.sendMessage(message.getChannel(), builder.toString());
				break;
			}
			case "setstats":
			case "setstat":
			case "ss":{
				Player player = PlayerHandler.getPlayer(message.getAuthor());
				if(player.inBattle()){
					inBattleMessage(message);
					return;
				}
				if(args.length < 3){
					message.reply("Usage: setstat <statname> <amount> (optional EV or IV modifier)"); 
					return;
				}
				try{
					StatHandler.setStats(message.getChannel(), player, args[1], Integer.parseInt(args[2]),
							(args.length > 3 ? args[3] : null));
				}catch(NumberFormatException e){
					message.reply("Invalid number");
				}
				break;
			}
			case "printstats":
			case "ps":{
				Player player = PlayerHandler.getPlayer(message.getAuthor());
				StringBuilder builder = new StringBuilder();
				builder.append("Your stats are as follows:\n");
				for(int x = 0; x < player.stats.length; x++){
					builder.append(Stats.getStatFromIndex(x).toString());
					builder.append('\t');
					//if(!(x == 2 || x == 4)) builder.append("\t\t\t\t");
					builder.append(SubStats.BASE);
					builder.append(':');
					builder.append(player.stats[x][SubStats.BASE.getIndex()]);
					builder.append('\t');
					builder.append(SubStats.IV);
					builder.append(':');
					builder.append(player.stats[x][SubStats.IV.getIndex()]);
					builder.append('\t');
					builder.append(SubStats.EV);
					builder.append(':');
					builder.append(player.stats[x][SubStats.EV.getIndex()]);
					builder.append('\n');
				}
				Pokebot.client.getOrCreatePMChannel(message.getAuthor()).sendMessage(builder.toString());
				message.reply("I sent a PM to you with your stats");
				break;
			}
			case "sm":
			case "setmove":{
				Player player = PlayerHandler.getPlayer(message.getAuthor());
				if(player.inBattle()){
					inBattleMessage(message);
					return;
				}
				if(args.length < 3){
					message.reply("Use: <slotnumber> <move>");
					return;
				}
				try{
					Moves move = Moves.valueOf(args[2].toUpperCase());
					if(move.equals(Moves.NULL)) throw new IllegalArgumentException("Null move");
					int slot = Integer.parseInt(args[1]);
					if(slot > 4 || slot < 1){
						message.reply("Invalid slot. Slots are 1-4");
						return;
					}
					if(player.hasMove(move)){ //The null move check is already done above
						message.reply("You already have that move!");
						return;
					}
					if(player.numOfAttacks < 4){
						slot = player.numOfAttacks++;
						message.reply("Less than 4 moves detected, setting slot to the last slot in the list...");
					} else {
						slot--;
					}
					player.moves[slot] = move;
					player.PP[slot] = move.getPP();
					message.reply("Set move "+(slot+1)+" to "+move.getName());
				}catch(NumberFormatException e){
					message.reply("That is not a valid number!");
				}catch(IllegalArgumentException e){
					message.reply("That is not a valid move!");
				}
				break;
			}
			case "gm":
			case "getmove":{
				Player player = PlayerHandler.getPlayer((message.getMentions().isEmpty() ?
						message.getAuthor() : message.getMentions().get(0)));
				try{
					if(args.length < 2){
						message.reply("Usage: <slot> (@user)");
						return;
					}
					int slot = Integer.parseInt(args[1]);
					if(slot < 1 || slot > 4){
						message.reply("Slots range from 1-4");
						return;
					}
					if(slot > player.numOfAttacks){
						message.reply("They don't have a move in that slot!");
						return;
					}
					Pokebot.sendMessage(message.getChannel(), player.user.mention()
							+" has "+player.moves[slot-1].getName()
							+" in slot "+slot);
					return;
				}catch(NumberFormatException e){
					message.reply("That's not a number!");
				}
				break;
			}
			case "st":
			case "settype":{
				Player player = PlayerHandler.getPlayer(message.getAuthor());
				if(player.inBattle()){
					inBattleMessage(message);
					return;
				}
				if(args.length < 2){
					message.reply("Usage: "+Pokebot.COMMAND_PREFIX+" <type> (type)");
					return;
				}
				try{
					Types type = Types.valueOf(args[1].toUpperCase());
					if(type == Types.NULL) throw new IllegalArgumentException("Null type");
					Types type2 = Types.NULL;
					if(args.length >= 3){
						type2 = Types.valueOf(args[2].toUpperCase());
						if(type2 == Types.NULL) throw new IllegalArgumentException("Null type");
					}
					player.primary = type;
					player.secondary = type2;
				}catch(IllegalArgumentException e){
					message.reply("That's not a valid type!");
				}
				break;
			}
			case "lm":
			case "listmoves":{
				Player player = PlayerHandler.getPlayer((message.getMentions().isEmpty() ?
						message.getAuthor() : message.getMentions().get(0)));
				StringBuilder builder = new StringBuilder("The moves for ");
				builder.append(player.getUser().mention());
				builder.append(" are:\n");
				for(int x = 0; x < player.numOfAttacks; x++){
					builder.append(x+1);
					builder.append(": ");
					builder.append(player.moves[x].toString());
					builder.append('\n');
				}
				Pokebot.sendMessage(message.getChannel(), builder.toString());
				break;
			}
			case "lam":
			case "listallmoves":{
				IUser user = message.getAuthor();
				StringBuilder builder = new StringBuilder("Here are all the moves I know:\n");
				Moves[] moves = Moves.values();
				for(int x = 1; x < moves.length; x++){ //Starting at one to prevent the NULL move
					builder.append(moves[x].toString()+"\n");
				}
				Pokebot.client.getOrCreatePMChannel(user).sendMessage(builder.toString());
				message.reply("I sent you all the moves I know");
				break;
			}
			case "gp":
			case "gpp":
			case "getpp":{
				Player player = PlayerHandler.getPlayer((message.getMentions().isEmpty() ?
						message.getAuthor() : message.getMentions().get(0)));
				StringBuilder builder = new StringBuilder("The PP remaining for "+player.getUser().mention()+" is:\n");
				for(int x = 0; x < player.numOfAttacks; x++){
					builder.append(x+1);
					builder.append(": ");
					builder.append(player.PP[x]);
					builder.append('\n');
				}
				Pokebot.sendMessage(message.getChannel(), builder.toString());
				break;
			}
			case "attack":{
				if(args.length < 3 || message.getMentions().isEmpty()){
					message.reply("Use: attack <slotnum> @target");
					return;
				}
				try{
					int slot = Integer.parseInt(args[1]);
					if(slot < 1 || slot > 4){
						message.reply("Slot number is from 1-4");
						return;
					}
					slot--;
					Player attacker = PlayerHandler.getPlayer(message.getAuthor());
					if(attacker.numOfAttacks == 0){
						message.reply("You have no moves! Set some with "+Pokebot.COMMAND_PREFIX+"sm");
						return;
					}
					if(attacker.numOfAttacks < slot){
						message.reply("That slot is empty");
						return;
					}
					Player defender = PlayerHandler.getPlayer(message.getMentions().get(0));
					if(attacker.HP < 1 || defender.HP < 1){
						message.reply((attacker.HP < 1 ? "You have fainted and are unable to move!"
								: defender.getUser().mention()+" has already fainted!"));
						return;
					}
					//At this point, we know there's a valid move in the slot and neither party has fainted
					if(attacker.PP[slot] < 1){
						message.reply("You have no PP left for that move!");
						return;
					}
					Moves move = attacker.moves[slot];
					//Before anything else, lets see if the target is ourselves
					if(defender.user.getID().equals(Pokebot.client.getOurUser().getID())){
						Pokebot.sendMessage(message.getChannel(), 
								message.getAuthor().mention()+" tried hurting me!");
						return;
					}
					//If the player is in a battle, we want to pass on the message
					if(attacker.inBattle()){
						if(defender.inBattle()){
							if(attacker.battle == defender.battle){
								attacker.battle.onAttack(message.getChannel(), attacker, move, defender);
								return; //We don't want the standard logic to run
							}
							message.reply("you two are in different battles!");
							return;
						}
						message.reply("you can only attack those in your battle!");
						return;
					}
					//at this point, we know the attacker is not in battle
					if(defender.inBattle()){
						message.reply("you unable to hit them because they are in a battle!");
						return;
					}
					//This is the normal neither-in-battle mess around attack
					Moves.attack(message.getChannel(), attacker, move, defender);
					attacker.PP[slot]--;
				}catch(NumberFormatException e){
					message.reply("That's not a number!");
				}
				break;
			}
			case "revive":
			case "heal":{
				if(PlayerHandler.getPlayer(message.getAuthor()).inBattle()){
					inBattleMessage(message);
					return;
				}
				Player player = PlayerHandler.getPlayer((message.getMentions().isEmpty() ?
						message.getAuthor() : message.getMentions().get(0)));
				//At this point, we know the person sending the message isn't in battle
				if(player.inBattle()){
					message.reply("unable to heal them because they are in a battle!");
					return;
				}
				player.HP = player.getMaxHP();
				for(int x = 0; player.numOfAttacks < x; x++){
					player.PP[x] = player.moves[x].getPP();
				}
				message.reply(" fully healed "+player.getUser().mention());
				break;
			}
			case "battle":{
				if(PlayerHandler.getPlayer(message.getAuthor()).inBattle()){
					message.reply("You're already in a battle!");
					return;
				}
				try{
					if(args.length < 3 || message.getMentions().isEmpty()){
						message.reply("Usage: "+Pokebot.COMMAND_PREFIX+"battle <time for turns> "
								+ "<@User, @User, @User...>");
						return;
					}
					BattleManager.createBattle(message.getChannel(), message.getAuthor(),
							message.getMentions(), Integer.parseInt(args[1]));
				}catch(NumberFormatException e){
					message.reply("That's not a valid number!");
				}
				break;
			}
			case "sb":
			case "startbattle":{
				BattleManager.onStartBattle(message.getChannel(), message.getAuthor());
				break;
			}
			case "jb":
			case "joinbattle":{
				if(message.getMentions().isEmpty()){
					message.reply("Usage: "+Pokebot.COMMAND_PREFIX+"joinbattle <@host>");
					return;
				}
				BattleManager.onJoinBattle(message.getChannel(),
						message.getAuthor(), message.getMentions().get(0)); 
				break;
			}
			/*case "saveall":{
				PlayerHandler.saveAll();
				break;
			}*/ //TODO
			case "help":{
				IUser user = message.getAuthor();
				StringBuilder builder = new StringBuilder("Here are the commands I know:\n");
				builder.append("Arguments in <> are manditory, arguments in () are optional\n");
				builder.append("alternative spellings are shown in []\n");
				builder.append("All commands start with \"");
				builder.append(Pokebot.COMMAND_PREFIX);
				builder.append("\"\n");
				builder.append("All commands are case insensitive");
				builder.append(" If a command has only an optional @mention \"(@mention)\"");
				builder.append(" then excluding it will show relevant info for yourself\n");
				builder.append('\n');
				builder.append("help : Shows this dialog\n");
				builder.append('\n');
				builder.append("getUsedPoints [gup] (@mention) : Displays the point of the user\n");
				builder.append("getStats [stats] [gs] (@mention) : Displays the stats of the user\n");
				builder.append("setStat [ss] <statname> <amount> (ev or iv) :");
				builder.append(" Sets your stats, if the subtype is excluded, it's assume to be base stats\n");
				builder.append("printStats [ps] : Sends you a PM with your stats in detail.");
				builder.append(" Instead of showing you the total value, this breaks it down by every favor\n");
				builder.append('\n');
				builder.append("setMove [sm] <slot> <move_name> : Sets the slot to this move.");
				builder.append(" Slots are 1-4, and will be ignored if you don't yet have 4 moves\n");
				builder.append("getMove [gm] <slot> (@mention) : Displays the move in that user's slot\n");
				builder.append("listMoves [lm] (@mention) : Displays a list of the user's moves\n");
				builder.append("listAllMoves [lam] : Sends you a list of every move I know\n");
				builder.append('\n');
				builder.append("type (@mention) : Displays the type(s) of the user\n");
				builder.append("setType [st] <type> (type2) : Sets your type(s)\n");
				builder.append('\n');
				builder.append("getPP [gpp] [gp] (@mention) : Displays the remaining PP for the user's moves\n");
				builder.append("attack <slot> <@mention> : Attacks the user with the move in your given slot\n");
				builder.append("heal [revive] (@mention) : Heals the given user to full HP, PP, and removes all status effects\n");
				builder.append('\n');
				builder.append("battle <turnTime> <@mention> (@mention...) : Starts a battle with the given turn time.");
				builder.append(" The turnTime is how many seconds before the turn is forcefully ended.");
				builder.append(" You need at to invite least one person, you can invite more, and it's not limited to those you invite\n");
				builder.append("joinBattle [jb] <@mention> : Joins the battle that person opened\n");
				builder.append("startBattle [sb] : Starts the battle you previously opened");
				Pokebot.client.getOrCreatePMChannel(user).sendMessage(builder.toString());
				message.reply("I sent you a PM with my help menu");
				return;
			}
			case "pgs":
			case "pokemongo":
			case "pokemongosimulator":{
				Pokebot.sendMessage(message.getChannel(), "Our servers are experiencing issues. Please come back later");
				break;
			}
			default:
				Pokebot.sendMessage(message.getChannel(), "invalid command");
				break;
		}
		}catch(Exception e){
			message.reply("There was an exception");
			e.printStackTrace();
		}
	}
	
	private static void inBattleMessage(IMessage message) throws MissingPermissionsException, HTTP429Exception, DiscordException{
		message.reply("you can't use this because you're in a battle!");
	}
	
	private static void reply(IMessage message, String reply){
		Pokebot.sendMessage(message.getChannel(), message.getAuthor().mention()+", "+reply);
	}
	
}