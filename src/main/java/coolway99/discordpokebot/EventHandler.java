package coolway99.discordpokebot;

import coolway99.discordpokebot.battle.Battle;
import coolway99.discordpokebot.battle.BattleManager;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.states.Abilities;
import coolway99.discordpokebot.moves.Move;
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

import java.util.ArrayList;
import java.util.List;

public class EventHandler{

	//TODO: Not all commands have outputs, this needs to be fixed
	//TODO: Perhaps make this neater somehow
	//TODO: Perhaps split some sections off into different classes
	@SuppressWarnings({"SpellCheckingInspection", "WeakerAccess"})
	@EventSubscriber
	public void onMessage(MessageReceivedEvent event){
		IMessage message = event.getMessage();
		if(message.mentionsEveryone()) return; //We don't want to respond to @everyone
		IUser author = message.getAuthor(); //The author of the message
		//We don't want to respond to bots
		if(author.isBot() && !author.getID().equals(Pokebot.client.getOurUser().getID())) return;
		IChannel channel = message.getChannel();
		//The first person the author mentioned, or the author if there was nobody
		IUser mentionOrAuthor = message.getMentions().isEmpty() ? author : message.getMentions().get(0);
		if(!message.toString().startsWith(Pokebot.config.COMMAND_PREFIX)) return;
		String[] args = message.toString().split(" ");
		try{
			switch(args[0].toLowerCase().replaceFirst(Pokebot.config.COMMAND_PREFIX, "")){
				case "gettype":
				case "gt":
				case "type":
				case "types":{
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
						reply(message, "Usage: "+Pokebot.config.COMMAND_PREFIX+"settype <type> (type)");
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
						reply(message, "Type(s) set to "+type+" "+(type2 != Types.NULL ? type2 : ""));
					} catch(IllegalArgumentException e){
						reply(message, "That's not a valid type!");
					}
					return;
				}
				case "getusedpoints":
				case "gup":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					Pokebot.sendMessage(channel, player.mention()
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
						player.HP = player.getMaxHP();
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
							.append("\nNature: ").append(player.nature.getExpandedText())
							.append("\nAbility: ").append(player.getAbility());
					Pokebot.sendPrivateMessage(author, builder.toString());
					reply(message, "I sent a PM to you with your stats");
					return;
				}
				case "clearstats":
				case "cs":{
					if(args.length < 2){
						reply(message, "for safety, you must type your user id\nUsage: clearstats "+author.getID());
						return;
					}
					if(!args[1].equals(author.getID())){
						reply(message, "That is not your user id!");
						return;
					}
					Player player = PlayerHandler.getPlayer(author);
					for(int x = 0; x < player.stats.length; x++){
						for(int y = 0; y < player.stats[x].length; y++){
							player.stats[x][y] = 0;
						}
					}
					reply(message, "stats cleared");
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
						Natures nature = Natures.valueOf(args[1].toUpperCase());
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
						builder.append('\n').append(nature.getExpandedText());
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
						if(StatHandler.wouldExceedTotalPoints(player, ability)) StatHandler.exceedWarning(channel, player);
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
						Move move = Move.REGISTRY.get(args[2].toUpperCase());
						if(move == null){
							reply(message, "That is not a valid move!");
							return;
						}
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
							if(slot != player.numOfAttacks++){
								reply(message, "Less than 4 moves detected, setting slot to the last slot in the list...");
							}
							slot = player.numOfAttacks;
						}
						slot--;

						if(StatHandler.wouldExceedTotalPoints(player, player.moves[slot].getMove(), move)){
							/*reply(message, "You don't have enough points left for that move!");
							return;*/
							StatHandler.exceedWarning(channel, player);
						}
						player.moves[slot] = new MoveSet(move);
						reply(message, "Set move "+(slot+1)+" to "+move.getName());
					} catch(NumberFormatException e){
						reply(message, "That is not a valid number!");
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
						Move move = Move.REGISTRY.get(args[1].toUpperCase());
						String b = "Stats of "+move+
								"\nType: "+move.getType(Abilities.MC_NORMAL_PANTS)+
								"\nPower: "+move.getPower()+
								"\nPP: "+move.getPP()+
								"\nAccuracy: "+Math.round(move.getAccuracy()*10000)/100+
								'\n'+(move.isSpecial() ? "Special" : "Physical")+
								"\nPoint Cost: "+move.getCost();
						Pokebot.sendMessage(channel, b);
					} catch(IllegalArgumentException e){
						reply(message, "That's not a valid move!");
					}
					return;
				}
				case "getpp":
				case "gpp":
				case "gp":
					Pokebot.sendMessage(channel, "Depreciated, use listMoves instead");
					//fallthru
				case "lm":
				case "listmoves":{
					Player player = PlayerHandler.getPlayer(mentionOrAuthor);
					if(player.numOfAttacks <= 0){
						Pokebot.sendMessage(channel, " has no moves!");
						return;
					}
					StringBuilder builder = new StringBuilder("The moves for ").append(player.mention()).append(" are:\n");
					for(int x = 0; x < player.numOfAttacks; x++){
						MoveSet set = player.moves[x];
						builder.append(x+1).append(": ").append(set.getMove().getDisplayName());
						builder.append(" [").append(set.getPP()).append('/')
								.append(player.moves[x].getMaxPP()).append("]\n");
					}
					Pokebot.sendMessage(channel, builder.toString());
					return;
				}
				case "lam":
				case "listallmoves":{
					StringBuilder builder = new StringBuilder("Here are all the moves I know:\n");
					for(Move move : Move.REGISTRY.values()){ //Starting at one to prevent the NULL move
						builder.append(move.getDisplayName()).append(" (").append(move.getMoveType()).append(')').append("\n");
					}
					Pokebot.sendPrivateMessage(author, builder.toString());
					reply(message, "I sent you all the moves I know");
					return;
				}
				case "removemove":
				case "deletemove":
				case "clearmove":
				case "rm":
				case "dm":
				case "cm":{
					Player player = PlayerHandler.getPlayer(author);
					if(player.inBattle()){
						inBattleMessage(message);
						return;
					}
					if(args.length < 2){
						reply(message, "Usage: clearMove <slotnumber>");
						return;
					}
					try{
						int slot = Integer.parseInt(args[1]);
						if(slot > 4 || slot < 1){
							reply(message, "Invalid slot. Slots are 1-4");
							return;
						}
						slot--; //So that it corisponds to computer code
						if(player.numOfAttacks < slot){
							reply(message, "You don't have a move in that slot!");
							return;
						}
						player.numOfAttacks--;
						player.moves[slot] = new MoveSet();
						for(int x = 0; x < player.moves.length-1; x++){
							if(player.moves[x] == null){
								player.moves[x] = player.moves[x+1];
								player.moves[x+1] = new MoveSet();
							}
						}
						reply(message, "Cleared move in slot "+(slot+1));
					} catch(NumberFormatException e){
						reply(message, "That is not a valid number!");
					}
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
						if(StatHandler.wouldExceedTotalPoints(player, player.level, newL)){
							//reply(message, "you don't have enough points left for that!");
							//return;
							StatHandler.exceedWarning(channel, player);
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
					reply(message, player.mention()+" is level "+player.level);
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
							reply(message, "You have no moves! Set some with "+Pokebot.config.COMMAND_PREFIX+"setmove");
							return;
						}
						if(attacker.numOfAttacks < slot){
							reply(message, "That slot is empty");
							return;
						}
						if(attacker.moves[slot].getPP() < 1){
							reply(message, "You have no PP left for that move!");
							return;
						}
						MoveSet moveSet = attacker.moves[slot];
						Player defender;
						//If this is a status move, then usually we are targeting ourselves
						if(moveSet.getMove().has(Move.Flags.UNTARGETABLE)){
							defender = PlayerHandler.getPlayer(author);
						} else {
							defender = PlayerHandler.getPlayer(message.getMentions().get(0));
						}
						if(attacker.HP < 1 || defender.HP < 1){
							reply(message, attacker.HP < 1 ? "You have fainted and are unable to move!"
									: defender.mention()+" has already fainted!");
							return;
						}
						//At this point, we know there's a valid move in the slot and neither party has fainted
						//Before anything else, lets see if the target is the bot
						if(defender.user.getID().equals(Pokebot.client.getOurUser().getID()) && !attacker.inBattle()){
							Pokebot.sendMessage(channel, author.mention()+" tried hurting me!");
							return;
						}
						//Sanity check for points, due to prevent "errors" between versions and balancing
						if(StatHandler.getTotalPoints(attacker) > StatHandler.MAX_TOTAL_POINTS){
							reply(message, "you have used too many points! You need to reduce them before attacking");
							return;
						}
						//If the player is in a battle, we want to pass on the message
						if(attacker.inBattle()){
							if(defender.inBattle()){
								if(attacker.battle == defender.battle){
									attacker.battle.onAttack(channel, attacker, moveSet, defender);
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
						Move.attack(channel, attacker, moveSet, defender);
						if(StatHandler.getStatPoints(defender) <= 10){
							Pokebot.sendMessage(channel, defender.mention()+", it looks like you haven't set any stats!"
									+" Set some with setstats");
						}
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
						player.moves[x].resetPP();
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
					//Sanity check for points, due to prevent "errors" between versions and balancing
					if(StatHandler.getTotalPoints(PlayerHandler.getPlayer(author)) > StatHandler.MAX_TOTAL_POINTS){
						reply(message, "you have used too many points! You need to reduce them before making a battle");
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
					//Sanity check for points, due to prevent "errors" between versions and balancing
					if(StatHandler.getTotalPoints(PlayerHandler.getPlayer(author)) > StatHandler.MAX_TOTAL_POINTS){
						reply(message, "you have used too many points! You need to reduce them before joining a " +
								"battle");
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
				case "cb":
				case "cancelbattle":{
					BattleManager.onCancelBattle(channel, author);
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
					Pokebot.sendMessage(channel, "A detailed command list can be found at " +
							"https://github.com/Coolway99/Discord-Pokebot/wiki/Command-List");
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
							Pokebot.sendAllMessages();
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
					ArrayList<Player> list = new ArrayList<>();
					list.add(PlayerHandler.getPlayer(author));
					list.add(PlayerHandler.getPlayer(Pokebot.client.getOurUser()));
					Battle battle = new Battle(message.getChannel(), 200000, list);
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
				case "info":{
					reply(message, "I am a Pokémon bot for Discord, but not in the traditional sense. " +
							"The concept is that YOU are the Pokémon, and I am built off of that idea.\n" +
							"While I am released to the public, I am currently incomplete and may change overtime.\n" +
							"I was built by Coolway99, and you can find my source code and more information at " +
							"https://github.com/Coolway99/Discord-Pokebot\n" +
							"Licensed under GNU GPL v3");
					return;
				}
				case "version":{
					reply(message, "I am version "+Pokebot.VERSION);
					return;
				}
				case "webinterface":
				case "wi":{
					reply(message, "My web interface can be found here: "+Pokebot.config.REDIRECT_URL);
					return;
				}
				case "switchslot":{
					if(args.length < 2){
						reply(message, "Usage: switchSlot <slot>");
						return;
					}
					byte slot;
					try{
						slot = (byte) (Byte.parseByte(args[1])-1);
						if(slot < 0 || slot >= PlayerHandler.MAX_SLOTS) throw new NumberFormatException();
						//reply(message, PlayerHandler.switchSlot(author, slot));
						if(PlayerHandler.getPlayer(author).inBattle()){
							reply(message, "You are in a battle!");
							return;
						}
						PlayerHandler.switchSlot(author, slot);
						reply(message,  "Switched slot to "+(slot+1));
					} catch(NumberFormatException e){
						reply(message, "Invalid slot, slots are 1-"+PlayerHandler.MAX_SLOTS);
						return;
					}
					break;
				}
				case "getslot":{
					reply(message, "your slot is "+(PlayerHandler.getMainFile(author).lastSlot+1));
					return;
				}
				case "guilds":{
					if(!author.getID().equals(Pokebot.config.OWNERID)) break;
					reply(message, "I am in "+Pokebot.client.getGuilds().size()+" guilds.");
					return;
				}
				default:
					break;
			}
			//reply(message, "invalid command");
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