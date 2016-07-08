package coolway99.discordpokebot;

import coolway99.discordpokebot.StatHandler.Stats;
import coolway99.discordpokebot.StatHandler.SubStats;
import coolway99.discordpokebot.storage.PlayerHandler;
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
		if(!message.toString().startsWith(Pokebot.COMMAND_PREFIX)) return;
		String[] args = message.toString().split(" ");
		try{
		switch(args[0].toLowerCase().replace(Pokebot.COMMAND_PREFIX, "")){
			case "testload":{
				IUser user = message.getAuthor();
				Player player = PlayerHandler.getPlayer(user);
				message.reply("Loaded, trying to extract info");
				message.getChannel().sendMessage(user.mention()+" loaded, they have "+player.HP);
				break;
			}
			case "testsave":{
				message.getChannel().sendMessage("removing user "+message.getAuthor().mention());
				PlayerHandler.removePlayer(message.getAuthor());
				break;
			}
			case "type":{
				IUser user = message.getAuthor();
				Player player = PlayerHandler.getPlayer(user);
				Pokebot.sendMessage(message.getChannel(), user.mention()+" is type "+player.primary.toString()
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
				Pokebot.sendMessage(message.getChannel(), player.getUser().mention() 
						+" has "+player.getMaxHP()+" MAX HP, "+player.getAttackStat()+" attack, "
						+player.getSpecialAttackStat()+" special attack, "+player.getDefenseStat()
						+" defense, "+player.getSpecialDefenseStat()+" special defense, and "
						+player.getSpeedStat()+" speed");
				break;
			}
			case "setstat":
			case "ss":{
				if(args.length < 3){
					message.reply("Usage: setstat <statname> <amount> (optional EV or IV modifier)"); 
					return;
				}
				Player player = PlayerHandler.getPlayer(message.getAuthor());
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
					slot = Math.max(slot-1, player.numOfAttacks);
					player.moves[slot] = move;
					player.PP[slot] = move.getPP();
					if(player.numOfAttacks < 4) player.numOfAttacks++;
					message.reply("Set move "+(slot+1)+" to "+move.getName()+"\n"
							+ "No, the slot changing is not a bug");
				}catch(NumberFormatException e){
					message.reply("That is not a valid number!");
				}catch(IllegalArgumentException e){
					message.reply("That is not a valid move!");
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
					Moves.attack(message.getChannel(), attacker, move, defender);
					attacker.PP[slot]--;
				}catch(NumberFormatException e){
					message.reply("That's not a number!");
				}
				break;
			}
			case "help":{
				Pokebot.sendMessage(message.getChannel(), "No.");
				return;
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
	
	
}