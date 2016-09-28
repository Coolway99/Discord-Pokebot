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

	private final Thread mainThread;

	public BotReadyHandler(Thread mainThread){
		this.mainThread = mainThread;
	}

	@EventSubscriber
	public void handle(ReadyEvent event){
		try{
			if(this.mainThread.isAlive()) Pokebot.LOGGER.debug("Waiting for mainThread to halt");
			this.mainThread.join();
		} catch(InterruptedException e){
			Pokebot.LOGGER.error("Error in ready thread, Interrupt received", e);
			System.exit(-1);
			return;
		}
		Pokebot.LOGGER.info("The bot is ready");
		Pokebot.client.getDispatcher().registerListener(new EventHandler());
		Pokebot.client.getDispatcher().unregisterListener(this);
		Pokebot.client.changeStatus(Status.game(Pokebot.getRandomGame()));
		Pokebot.client.changePresence(false);
		try {
			Pokebot.client.changeUsername(Pokebot.config.BOTNAME);
		} catch(DiscordException | RateLimitException e) {
			Pokebot.LOGGER.error("Error changing username, it might contain invalid symbols", e);
		}
	}

	@EventSubscriber
	public void guild(GuildCreateEvent event){
		for(IChannel channel : event.getGuild().getChannels()){
			channel.getMessages().setCacheCapacity(1);
		}
	}
}