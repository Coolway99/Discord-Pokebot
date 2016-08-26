package coolway99.discordpokebot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.RateLimitException;

public class BotReadyHandler{

	@EventSubscriber
	public void handle(ReadyEvent event){
		System.out.println("The bot is ready");//reggie");
		Pokebot.client.getDispatcher().registerListener(new EventHandler());
		Pokebot.client.getDispatcher().unregisterListener(this);
		Pokebot.client.changeStatus(Status.game(Pokebot.getRandomGame()));
		Pokebot.client.changePresence(false);
		try {
			Pokebot.client.changeUsername(Pokebot.config.BOTNAME);
		} catch(DiscordException | RateLimitException e) {
			e.printStackTrace();
			System.err.println("\nError changing username");
		}
	}

	@EventSubscriber
	public void guild(GuildCreateEvent event){
		for(IChannel channel : event.getGuild().getChannels()){
			channel.getMessages().setCacheCapacity(1);
		}
	}
}