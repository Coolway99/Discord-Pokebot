package coolway99.discordpokebot;

import java.util.Optional;

import sx.blah.discord.api.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;

public class BotReadyHandler implements IListener<ReadyEvent>{

	@Override
	public void handle(ReadyEvent event){
		System.out.println("The bot is ready");//reggie");
		Pokebot.client.getDispatcher().registerListener(new EventHandler());
		Pokebot.client.getDispatcher().unregisterListener(this);
		Pokebot.client.updatePresence(false, Optional.of(Pokebot.getRandomGame()));
		try {
			Pokebot.client.changeUsername(Pokebot.config.BOTNAME);
		} catch(DiscordException | HTTP429Exception e) {
			e.printStackTrace();
			System.err.println("\nError changing username");
		}
	}
}
