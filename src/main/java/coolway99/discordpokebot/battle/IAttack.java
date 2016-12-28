package coolway99.discordpokebot.battle;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.MoveSet;

//Just a storage class for everything
//I make the fields public because they're final anyways, you can't change them so
//why make them private
public class IAttack{
	
	public final Player attacker;
	public final Player defender;
	public final MoveSet move;
	private boolean canceled;

	/*public IAttack(Player attacker, int slot, Player defender){
		this(attacker, attacker.moves[slot], defender);
	}*/

	//Why do I do it like this? I like to imagine it like
	//"Attacker uses move on Defender"
	public IAttack(Player attacker, MoveSet move, Player defender){
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

	//We check the usual for if IAttacks equal each other
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
