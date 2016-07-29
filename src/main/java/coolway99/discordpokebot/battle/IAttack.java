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
	private boolean canceled;

	//Why do I do it like this? I like to imagine it like
	//"Attacker uses move on Defender"
	public IAttack(Player attacker, Moves move, Player defender){
		this.attacker = attacker;
		this.move = move;
		this.defender = defender;
		this.canceled = false;
	}

	public void cancel(){
		this.canceled = true;
	}

	public boolean isCanceled(){
		return this.canceled;
	}

	//We check the usual for if IAttacks equal eachother, but we also add code for Player objects, if the attacker is
	// the same. This is for "ease of use", but rather, two IAttacks may not equal eachother, but their attackers might.
	@Override
	public boolean equals(Object obj){
		if(obj instanceof IAttack){
			IAttack attack = (IAttack) obj;
			return attack.attacker == this.attacker
					&& attack.move == this.move
					&& attack.defender == this.defender;
		}
		return false;
	}
}
