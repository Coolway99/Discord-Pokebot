package coolway99.discordpokebot;

import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.battle.BattleManager;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.states.Moves;
import coolway99.discordpokebot.states.Natures;
import coolway99.discordpokebot.states.Stats;
import coolway99.discordpokebot.states.SubStats;
import coolway99.discordpokebot.states.Types;
import coolway99.discordpokebot.storage.PlayerHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.MessageBuilder;

import java.util.Arrays;
import java.util.List;

public class EventHandler{

	private static final String HELP_TEXT = "Here are the commands I know:\n"
			+"Arguments in <> are mandatory, arguments in () are optional\n"
			+"alternative spellings are shown in []\n"
			+"All commands start with \""
			+Pokebot.config.COMMAND_PREFIX
			+"\"\n"
			+"All commands are case insensitive"
			+" If a command has only an optional @mention \"(@mention)\""
			+" then excluding it will show relevant info for yourself\n"
			+'\n'
			+"help : Shows this dialog\n"
			+'\n'
			+"getUsedPoints [gup] (@mention) : Displays the point of the user\n"
			+"getStats [stats] [gs] (@mention) : Displays the stats of the user\n"
			+"setStat [ss] <statname> <amount> (ev or iv) :"
			+" Sets your stats, if the subtype is excluded, it's assume to be base stats\n"
			+"printStats [ps] : Sends you a PM with your stats in detail."
			+" Instead of showing you the total value, this breaks it down by every favor\n"
			+'\n'
			+"setMove [sm] <slot> <move_name> : Sets the slot to this move."
			+" Slots are 1-4, and will be ignored if you don't yet have 4 moves\n"
			+"getMove [gm] <slot> (@mention) : Displays the move in that user's slot\n"
			+"listMoves [lm] (@mention) : Displays a list of the user's moves\n"
			+"listAllMoves [lam] : Sends you a list of every move I know\n"
			+'\n'
			+"type (@mention) : Displays the type(s) of the user\n"
			+"setType [st] <type> (type2) : Sets your type(s)\n"
			+'\n'
			+"getPP [gpp] [gp] (@mention) : Displays the remaining PP for the user's moves\n"
			+"attack <slot> <@mention> : Attacks the user with the move in your given slot\n"
			+"heal [revive] (@mention) : Heals the given user to full HP, PP, and removes all status effects\n"
			+'\n'
			+"battle <turnTime> <@mention> (@mention...) : Starts a battle with the given turn time."
			+" The turnTime is how many seconds before the turn is forcefully ended."
			+" You need at to invite least one person, you can invite more, and it's not limited to those you invite\n"
			+"joinBattle [jb] <@mention> : Joins the battle that person opened\n"
			+"startBattle [sb] : Starts the battle you previously opened";

	//TODO: Not all commands have outputs, this needs to be fixed
	//TODO: Perhaps make this neater somehow
	//TODO: Perhaps split some sections off into different classes
	@SuppressWarnings("SpellCheckingInspection")
	@EventSubscriber
	public void onMessage(MessageReceivedEvent event){
		IMessage message = event.getMessage();
		IChannel channel = message.getChannel();
		IUser author = message.getAuthor(); //The author of the message
		IUser mentionOrAuthor = message.getMentions().isEmpty() ?
				author : message.getMentions().get(0); //The first person the author mentioned, or the author if there
		// was nobody
		if(message.mentionsEveryone()) return; //We don't want to respond to @everyone
		/*if(message.toString().toLowerCase().contains("pokemon go")){
			Pokebot.sendMessage(channel, "Our servers are experiencing issues. Please come back later");
			return;
		}*/
		if(!message.toString().startsWith(Pokebot.config.COMMAND_PREFIX)) return;
		String[] args = message.toString().split(" ");
		try{
			switch(args[0].toLowerCase().replaceFirst(Pokebot.config.COMMAND_PREFIX, "")){
				case "gettype":
				case "gt":
				case "type":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					Pokebot.sendMessage(channel, mentionOrAuthor.mention()+" is type "+player.primary.toString()
							+(player.hasSecondaryType() ? " with secondary type "+player.secondary.toString()
							: ""));
					return;
				}
				case "st":
				case "settype":{
					Player player = PlayerHandler.getPlayer(author);
					if(player.inBattle()){
						inBattleMessage(message);
						return;
					}
					if(args.length < 2){
						reply(message, "Usage: "+Pokebot.config.COMMAND_PREFIX+" <type> (type)");
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
					} catch(IllegalArgumentException e){
						reply(message, "That's not a valid type!");
					}
					return;
				}
				case "getusedpoints":
				case "gup":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					Pokebot.sendMessage(channel, player.user.mention()
							+" has a total point count of "+StatHandler.getTotalPoints(player)
							+" out of a maximum of "+StatHandler.MAX_TOTAL_POINTS);
					return;
				}
				case "gs":
				case "stats":
				case "getstats":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					StringBuilder builder = new StringBuilder(mentionOrAuthor.mention());
					builder.append(" has:\n");
					builder.append(Stats.HEALTH.toString()).append(": ");
					builder.append(player.HP).append('/').append(player.getMaxHP()).append("HP");
					for(int x = 1; x < player.stats.length; x++){
						builder.append('\n');
						builder.append(Stats.getStatFromIndex(x).toString()).append(": ");
						builder.append(player.getStatFromIndex(x));
						if(player.modifiers[x] != 0){
							builder.append(String.format(" (%+d)", player.modifiers[x]));
						}
					}
					builder.append("\nNature: ");
					builder.append(player.nature);
					Pokebot.sendMessage(channel, builder.toString());
					return;
				}
				case "setstats":
				case "setstat":
				case "ss":{
					Player player = PlayerHandler.getPlayer(author);
					if(player.inBattle()){
						inBattleMessage(message);
						return;
					}
					if(args.length < 3){
						reply(message, "Usage: setstat <statname> <amount> (optional EV or IV modifier)");
						return;
					}
					try{
						StatHandler.setStats(channel, player, args[1], Integer.parseInt(args[2]),
								args.length > 3 ? args[3] : null);
					} catch(NumberFormatException e){
						reply(message, "Invalid number");
					}
					return;
				}
				case "printstats":
				case "ps":
				case "printinfo":
				case "pi":{
					Player player = PlayerHandler.getPlayer(author);
					StringBuilder builder = new StringBuilder("Your stats are as follows:\n");
					for(int x = 0; x < player.stats.length; x++){
						builder.append(Stats.getStatFromIndex(x).toString())
								.append('\t')
								//if(!(x == 2 || x == 4)) builder.append("\t\t\t\t")
								.append(SubStats.BASE)
								.append(':')
								.append(player.stats[x][SubStats.BASE.getIndex()])
								.append('\t')
								.append(SubStats.IV)
								.append(':')
								.append(player.stats[x][SubStats.IV.getIndex()])
								.append('\t')
								.append(SubStats.EV)
								.append(':')
								.append(player.stats[x][SubStats.EV.getIndex()])
								.append('\n');
					}
					builder.append("Total Base Stat Points Used: ")
							.append(StatHandler.getCombinedPoints(player, SubStats.BASE))
							.append('/').append(StatHandler.MAX_TOTAL_STAT_POINTS)
							.append("\nTotal IV Stat Points Used: ")
							.append(StatHandler.getCombinedPoints(player, SubStats.IV))
							.append("\nTotal EV Stat Points Used: ")
							.append(StatHandler.getCombinedPoints(player, SubStats.EV))
							.append('/').append(StatHandler.MAX_TOTAL_EV_POINTS)
							.append("\nLevel: ").append(player.level).append('/').append(StatHandler.MAX_LEVEL)
							.append("\nNature: ").append(player.nature.getExpandedText());
				/*TODO
				 * builder.append("\nAbility").append(player.ability);
				 */
					Pokebot.sendPrivateMessage(author, builder.toString());
					reply(message, "I sent a PM to you with your stats");
					return;
				}
				case "gn":
				case "getnature":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					Pokebot.sendMessage(channel, mentionOrAuthor.mention()+" has nature "
							+player.nature.getExpandedText());
					return;
				}
				case "sn":
				case "setnature":{
					Player player = PlayerHandler.getPlayer(author);
					if(player.inBattle()){
						inBattleMessage(message);
						return;
					}
					if(args.length < 2){
						reply(message, "Usage: sn <nature>");
						return;
					}
					try{
						Natures nature = Natures.valueOf(args[1]);
						player.nature = nature;
						reply(message, "Set nature to "+nature.toString());
					} catch(IllegalArgumentException e){
						reply(message, "That's not a valid nature!");
					}
					return;
				}
				case "lan":
				case "listallnatures":
				case "listallnature":{
					StringBuilder builder = new StringBuilder("These are the natures I know:");
					for(Natures nature : Natures.values()){
						builder.append('\n').append(nature);
					}
					Pokebot.sendPrivateMessage(author, builder.toString());
					reply(message, "I sent you all the natures I know");
					return;
				}
				case "ga":
				case "getability":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					Pokebot.sendMessage(channel, player.mention()+"'s ability is "+player.getAbility());
					return;
				}
				case "sa":
				case "setability":{
					Player player = PlayerHandler.getPlayer(author);
					try{
						Abilities ability = Abilities.valueOf(args[1].toUpperCase());
						player.setAbility(ability);
						reply(message, "Set ability to "+ability);
					} catch(IndexOutOfBoundsException e){
						reply(message, "Usage: sa <ability>");
					} catch(IllegalArgumentException e){
						reply(message, "That's not an ability, list them with laa");
					}
					return;
				}
				case "listallabilities":
				case "listallability":
				case "laa":{
					StringBuilder builder = new StringBuilder("These are all the abilities I know:");
					for(Abilities ability : Abilities.values()){
						builder.append('\n');
						builder.append(ability);
						builder.append(" Cost:(").append(ability.getCost()).append(')');
					}
					Pokebot.sendPrivateMessage(author, builder.toString());
					reply(message, "I sent you all the abilities I know");
					return;
				}
				case "sm":
				case "setmove":{
					Player player = PlayerHandler.getPlayer(author);
					if(player.inBattle()){
						inBattleMessage(message);
						return;
					}
					if(args.length < 3){
						reply(message, "Use: <slotnumber> <move>");
						return;
					}
					try{
						Moves move = Moves.valueOf(args[2].toUpperCase());
						if(move.equals(Moves.NULL)) throw new IllegalArgumentException("Null move");
						int slot = Integer.parseInt(args[1]);
						if(slot > 4 || slot < 1){
							reply(message, "Invalid slot. Slots are 1-4");
							return;
						}
						if(player.hasMove(move)){ //The null move check is already done above
							reply(message, "You already have that move!");
							return;
						}
						if(player.numOfAttacks < 4){
							slot = player.numOfAttacks++;
							reply(message, "Less than 4 moves detected, setting slot to the last slot in the list...");
						} else {
							slot--;
						}
						player.moves[slot] = move;
						player.PP[slot] = move.getPP();
						reply(message, "Set move "+(slot+1)+" to "+move.getName());
					} catch(NumberFormatException e){
						reply(message, "That is not a valid number!");
					} catch(IllegalArgumentException e){
						reply(message, "That is not a valid move!");
					}
					return;
				}
				case "gm":
				case "getmove":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					try{
						if(args.length < 2){
							reply(message, "Usage: <slot> (@user)");
							return;
						}
						int slot = Integer.parseInt(args[1]);
						if(slot < 1 || slot > 4){
							reply(message, "Slots range from 1-4");
							return;
						}
						if(slot > player.numOfAttacks){
							reply(message, "They don't have a move in that slot!");
							return;
						}
						Pokebot.sendMessage(channel, player.user.mention()
								+" has "+player.moves[slot-1].getName()
								+" in slot "+slot);
						return;
					} catch(NumberFormatException e){
						reply(message, "That's not a number!");
					}
					return;
				}
				case "gmi":
				case "getmoveinfo":{
					try{
						if(args.length < 2){
							reply(message, "Usage: gmi <move>");
							return;
						}
						Moves move = Moves.valueOf(args[1].toUpperCase());
						StringBuilder b = new StringBuilder("Stats of ").append(move);
						b.append("\nType: ").append(move.getType(Abilities.MC_NORMAL_PANTS));
						b.append("\nPower: ").append(move.getPower());
						b.append("\nPP: ").append(move.getPP());
						b.append("\nAccuracy: ").append(Math.round(move.getAccuracy()*10000)/100);
						b.append('\n').append(move.isSpecial() ? "Special" : "Physical");
						b.append("\nPoint Cost: ").append(move.getCost());
						Pokebot.sendMessage(channel, b.toString());
					} catch(IllegalArgumentException e){
						reply(message, "That's not a valid move!");
					}
					return;
				}
				case "lm":
				case "listmoves":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					if(player.numOfAttacks <= 0){
						Pokebot.sendMessage(channel, " has no moves!");
						return;
					}
					StringBuilder builder = new StringBuilder("The moves for ");
					builder.append(player.user.mention());
					builder.append(" are:\n");
					for(int x = 0; x < player.numOfAttacks; x++){
						builder.append(x+1);
						builder.append(": ");
						builder.append(player.moves[x].toString());
						builder.append('\n');
					}
					Pokebot.sendMessage(channel, builder.toString());
					return;
				}
				case "lam":
				case "listallmoves":{
					StringBuilder builder = new StringBuilder("Here are all the moves I know:\n");
					Moves[] moves = Moves.values();
					for(int x = 1; x < moves.length; x++){ //Starting at one to prevent the NULL move
						builder.append(moves[x].toString()).append("\n");
					}
					Pokebot.sendPrivateMessage(author, builder.toString());
					reply(message, "I sent you all the moves I know");
					return;
				}
				case "gp":
				case "gpp":
				case "getpp":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					StringBuilder builder = new StringBuilder("The PP remaining for "+player.user.mention()+" is:\n");
					for(int x = 0; x < player.numOfAttacks; x++){
						builder.append(x+1);
						builder.append(": ");
						builder.append(player.PP[x]);
						builder.append('\n');
					}
					Pokebot.sendMessage(channel, builder.toString());
					return;
				}
				case "setlevel":
				case "sl":{
					if(args.length < 2){
						reply(message, "Usage: setlevel <level>");
					}
					Player player = PlayerHandler.getPlayer(author);
					if(player.inBattle()){
						inBattleMessage(message);
						return;
					}
					try{
						int newL = Integer.parseInt(args[1]);
						if(newL > StatHandler.MAX_LEVEL || newL < 1){
							reply(message, "that's not a valid level. The range is 1-"+StatHandler.MAX_LEVEL);
							return;
						}
						if(StatHandler.getTotalPoints(player)-player.level+newL > StatHandler.MAX_TOTAL_POINTS){
							reply(message, "you don't have enough points left for that!");
							return;
						}
						player.level = newL;
						reply(message, "set new level to "+newL);
					} catch(NumberFormatException e){
						reply(message, "that's not a valid number!");
					}
					return;
				}
				case "getlevel":
				case "gl":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					reply(message, player.user.mention()+" is level "+player.level);
					return;
				}
				case "attack":{
					try{
						//We rely on error catching if there is the incorrect args
						int slot = Integer.parseInt(args[1]);
						if(slot < 1 || slot > 4){
							reply(message, "Slot number is from 1-4");
							return;
						}
						slot--;
						Player attacker = PlayerHandler.getPlayer(author);
						if(attacker.numOfAttacks == 0){
							reply(message, "You have no moves! Set some with "+Pokebot.config.COMMAND_PREFIX+"sm");
							return;
						}
						if(attacker.numOfAttacks < slot){
							reply(message, "That slot is empty");
							return;
						}
						if(attacker.PP[slot] < 1){
							reply(message, "You have no PP left for that move!");
							return;
						}
						Moves move = attacker.moves[slot];
						Player defender;
						//If this is a status move, then usually we are targeting ourselves
						if(move.has(Moves.Flags.UNTARGETABLE)){
							defender = PlayerHandler.getPlayer(author);
						} else {
							defender = PlayerHandler.getPlayer(message.getMentions().get(0));
						}
						if(attacker.HP < 1 || defender.HP < 1){
							reply(message, attacker.HP < 1 ? "You have fainted and are unable to move!"
									: defender.user.mention()+" has already fainted!");
							return;
						}
						//At this point, we know there's a valid move in the slot and neither party has fainted
						//Before anything else, lets see if the target is the bot
					/*if(defender.user.getID().equals(Pokebot.client.getOurUser().getID())){
						Pokebot.sendMessage(channel, 
								author.mention()+" tried hurting me!");
						return;
					}*/
						//If the player is in a battle, we want to pass on the message
						if(attacker.inBattle()){
							if(defender.inBattle()){
								if(attacker.battle == defender.battle){
									attacker.battle.onAttack(channel, attacker, move, defender);
									return; //We don't want the standard logic to run
								}
								reply(message, "you two are in different battles!");
								return;
							}
							reply(message, "you can only attack those in your battle!");
							return;
						}
						//at this point, we know the attacker is not in battle
						if(defender.inBattle()){
							reply(message, "you unable to hit them because they are in a battle!");
							return;
						}
						//This is the normal neither-in-battle mess around attack
						Moves.attack(channel, attacker, move, defender);
						if(StatHandler.getStatPoints(defender) <= 10){
							Pokebot.sendMessage(channel, defender.user.mention()+", it looks like you haven't set any " +
									"stats!"
									+"Set some with setstats");
						}
						attacker.PP[slot]--;
					} catch(NumberFormatException e){
						reply(message, "That's not a number!");
					} catch(IndexOutOfBoundsException e){
						reply(message, "Usage: attack <slotnum> @target");
					}
					return;
				}
				//TODO this should redirect to the heal function
				case "revive":
				case "heal":{
					if(PlayerHandler.getPlayer(author).inBattle()){
						inBattleMessage(message);
						return;
					}
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					//At this point, we know the person sending the message isn't in battle
					if(player.inBattle()){
						reply(message, "unable to heal them because they are in a battle!");
						return;
					}
					player.HP = player.getMaxHP();
					for(int x = 0; x < player.numOfAttacks; x++){
						player.PP[x] = player.moves[x].getPP();
					}
					player.cureNV();
					for(int x = 0; x < player.modifiers.length; x++){
						player.modifiers[x] = 0;
					}
					reply(message, " fully healed "+player.user.mention());
					return;
				}
				case "battle":{
					if(PlayerHandler.getPlayer(author).inBattle()){
						reply(message, "You're already in a battle!");
						return;
					}
					try{
						if(args.length < 3 || message.getMentions().isEmpty()){
							reply(message, "Usage: "+Pokebot.config.COMMAND_PREFIX+"battle <time for turns> "
									+"<@User, @User, @User...>");
							return;
						}
						BattleManager.createBattle(channel, author,
								message.getMentions(), Integer.parseInt(args[1]));
					} catch(NumberFormatException e){
						reply(message, "That's not a valid number!");
					}
					return;
				}
				case "sb":
				case "startbattle":{
					BattleManager.onStartBattle(channel, author);
					return;
				}
				case "jb":
				case "joinbattle":{
					if(message.getMentions().isEmpty()){
						reply(message, "Usage: "+Pokebot.config.COMMAND_PREFIX+"joinbattle <@host>");
						return;
					}
					BattleManager.onJoinBattle(channel,
							author, message.getMentions().get(0));
					return;
				}
				case "lb":
				case "leavebattle":{
					BattleManager.onLeaveBattle(PlayerHandler.getPlayer(author));
					return;
				}
				case "saveall":{
					if(!author.getID().equals(Pokebot.config.OWNERID)){
						reply(message, "you are not the owner!");
						return;
					}
					PlayerHandler.saveAll();
					reply(message, "saved all open players that could be.");
					return;
				}
				case "help":{
					Pokebot.sendMessage(Pokebot.client.getOrCreatePMChannel(author), EventHandler.HELP_TEXT);
					reply(message, "I sent you a PM with my help menu");
					return;
				}
				case "pgs":
				case "pokemongo":
				case "pokemongosimulator":{
					Pokebot.sendMessage(channel, "Our servers are experiencing issues. Please come back later");
					return;
				}
				case "stop":{
					if(author.getID().equals(Pokebot.config.OWNERID)){
						try{
							reply(message, "shutting down");
							System.out.println("Shutting down by owner request");
							Pokebot.client.changeStatus(Status.game("Currently Offline"));
							Pokebot.client.changePresence(true);
							Pokebot.timer.shutdownNow();
							BattleManager.nukeBattles();
							new Pokebot.MessageTimer().run();
							PlayerHandler.saveAll();
							Pokebot.client.logout();
						} catch(Exception e){
							e.printStackTrace();
							System.err.println("Error while shutting down");
						}
						System.out.println("Terminated");
						System.exit(0);
					}
					reply(message, "you aren't the owner, shoo!");
					return;
				}
				case "listallroles":{
					if(!author.getID().equals(Pokebot.config.OWNERID)) break;
					List<IRole> roles = channel.getGuild().getRoles();
					StringBuilder b = new StringBuilder();
					for(IRole role : roles){
						//Pokebot.sendMessage(channel, role.getName());
						if(role.getName().equals("@everyone")) continue;
						b.append(role.getName()).append('\n');
					}
					MessageBuilder builder = new MessageBuilder(Pokebot.client);
					builder.appendCode(null, b.toString());
					builder.withChannel(message.getChannel());
					builder.send();
					return;
				}
				case "testbattle":{
					if(!author.getID().equals(Pokebot.config.OWNERID)) break;
					Battle battle = new Battle(message.getChannel(), 200000, Arrays.asList(PlayerHandler.getPlayer
							(author), PlayerHandler.getPlayer(Pokebot.client.getOurUser())));
					BattleManager.battles.add(battle);
					return;
				}
				case "spoof":{
					if(!author.getID().equals(Pokebot.config.OWNERID)) break;
					StringBuilder builder = new StringBuilder(Pokebot.config.COMMAND_PREFIX);
					for(int x = 1; x < args.length; x++){
						builder.append(args[x]);
						builder.append(' ');
					}
					reply(message, "Running "+builder);
					MessageBuilder newMessage = new MessageBuilder(Pokebot.client);
					newMessage.appendContent(builder.toString());
					newMessage.withChannel(channel);
					this.onMessage(new MessageReceivedEvent(newMessage.send()));
					return;
				}
				default:
					break;
			}
			reply(message, "invalid command");
		} catch(Exception e){
			reply(message, "there was an exception");
			e.printStackTrace();
		}
	}

	private static void inBattleMessage(IMessage message){
		reply(message, "you can't use this because you're in a battle!");
	}

	private static void reply(IMessage message, String reply){
		Pokebot.sendMessage(message.getChannel(), message.getAuthor().mention()+", "+reply);
	}

}