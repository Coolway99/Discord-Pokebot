package coolway99.discordpokebot;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.obj.IChannel;

public class GuildCreateHandler implements IListener<GuildCreateEvent>{

	@Override
	public void handle(GuildCreateEvent event){
		for(IChannel channel : event.getGuild().getChannels()){
			channel.getMessages().setCacheCapacity(1);
		}
	}
}
