package coolway99.discordpokebot;

import coolway99.discordpokebot.storage.PlayerHandler;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.concurrent.TimeUnit;

import static coolway99.discordpokebot.Pokebot.client;

public class BotReadyHandler implements IListener<ReadyEvent>{

	private final Thread mainThread;

	public BotReadyHandler(Thread mainThread){
		this.mainThread = mainThread;
	}

	@Override
	public void handle(ReadyEvent event){
		try{
			this.mainThread.join();
		} catch(InterruptedException e){
			System.err.println("Error in ready thread, Interrupt received");
			e.printStackTrace();
			System.exit(-1);
			return;
		}
		Pokebot.timer.scheduleAtFixedRate(PlayerHandler::saveAll, Pokebot.SAVE_DELAY, Pokebot.SAVE_DELAY, TimeUnit.MINUTES);
		Pokebot.timer.scheduleAtFixedRate(Pokebot::sendAllMessages, Pokebot.MESSAGE_DELAY, Pokebot.MESSAGE_DELAY,
				TimeUnit.MILLISECONDS);
		Pokebot.timer.scheduleAtFixedRate(() -> {
					if(!client.isReady()){
						System.err.println("Skipping game status update, bot isn't ready");
						return;
					}
					client.changeStatus(Status.game(Pokebot.getRandomGame()));
				}
				, Pokebot.GAME_DELAY, Pokebot.GAME_DELAY, TimeUnit.MINUTES);
		System.out.println("The bot is ready");//reggie");
		client.getDispatcher().registerListener(new EventHandler());
		client.getDispatcher().unregisterListener(this);
		client.changeStatus(Status.game(Pokebot.getRandomGame()));
		client.changePresence(false);
		try {
			client.changeUsername(Pokebot.config.BOTNAME);
		} catch(DiscordException | RateLimitException e) {
			e.printStackTrace();
			System.err.println("\nError changing username");
		}
	}
}