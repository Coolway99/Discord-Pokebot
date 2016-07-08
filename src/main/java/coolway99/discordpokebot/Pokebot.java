package coolway99.discordpokebot;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

public class Pokebot{
	
	public static final String COMMAND_PREFIX = "==";
	
	public static IDiscordClient client;
	public static final Scanner in = new Scanner(System.in);
	public static final Random ran = new Random();
	
	public static void main(String... args) throws Exception{
		if(args.length < 1){
			System.out.println("The app needs a token to log in");
			client = getClient(in.nextLine());
		} else {
			client = getClient(args[0]);
		}
		System.out.println("Logging in");
		//client = getClient(TOKEN);
		client.getDispatcher().registerListener(new BotReadyHandler());
	}
	
	public static IDiscordClient getClient(String token) throws Exception{
		return new ClientBuilder().withToken(token).login();
	}
	
	public static File getSaveFile(IUser user){
		return new File("/botdata/userpokemon/"+user.getID());
	}
	
	public static void sendMessage(IChannel channel, String message){
		try{
			channel.sendMessage(message);
		}catch(DiscordException | HTTP429Exception | MissingPermissionsException e){
			e.printStackTrace();
		}
	}

}
