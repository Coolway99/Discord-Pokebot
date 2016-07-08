package coolway99.discordpokebot;

import sx.blah.discord.api.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class BotReadyHandler implements IListener<ReadyEvent>{

	@Override
	public void handle(ReadyEvent event){
		System.out.println("The bot is ready");//reggie");
		Pokebot.client.getDispatcher().registerListener(new EventHandler());
	}
}
