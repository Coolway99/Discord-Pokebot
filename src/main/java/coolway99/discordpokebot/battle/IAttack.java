package coolway99.discordpokebot.battle;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.states.Moves;

//Just a storage class for everything
//I make the fields public because they're final anyways, you can't change them so
//why make them private
public class IAttack{
	
	public final Player attacker;
	public final Player defender;
	public final Moves move;
	
	//Why do I do it like this? I like to imagine it like
	//"Attacker uses move on Defender"
	public IAttack(Player attacker, Moves move, Player defender){
		this.attacker = attacker;
		this.move = move;
		this.defender = defender;
	}
}
