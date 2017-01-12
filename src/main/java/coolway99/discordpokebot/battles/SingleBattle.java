package coolway99.discordpokebot.battles;

import coolway99.discordpokebot.Player;
import coolway99.discordpokebot.moves.AttackLogic;
import coolway99.discordpokebot.moves.MoveSet;
import coolway99.discordpokebot.states.Effects;
import coolway99.discordpokebot.states.Position;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;

public class SingleBattle extends Battle{

	private Player red;
	private Player blue;

	public SingleBattle(IChannel channel, int turnTime, ArrayList<Player> participants){
		super(channel, turnTime, participants);
	}

	@Override
	public void setupBattle(){
		super.setupBattle();
		this.red = this.participants.get(0);
		this.blue = this.participants.get(1);
	}

	/*private static ArrayList<Player> combineTeams(List<Player> teamA, List<Player> teamB){
		ArrayList<Player> list = new ArrayList<>();
		list.addAll(teamA);
		list.addAll(teamB);
		return list;
	}*/


	@Override
	public boolean checkPosition(IChannel channel, Player attacker, MoveSet moveSet, Player defender){
		if(!moveSet.getMove().getTarget().canHit(Position.CENTER, Position.CENTER, attacker == defender)){
			this.sendMessage(attacker.mention()+", This move can't target them!");
			return false;
		}
		return true;
	}

	@Override
	public void attackLogic(IAttack attack){
		//Check for flinch status
		if(attack.attacker.has(Effects.Volatile.FLINCH)){
			//this.sendMessage("But "+attack.attacker.mention()+" is flinching!");
			return;
		}
		if(!this.participants.contains(attack.defender)){
			this.sendMessage(attack.attacker.mention()
					+" went to attack, but there was no target!");
			return;
		}
		//We already know the submitted move passes the positional check
		AttackLogic.attack(this.channel, attack.attacker, attack.move.getMove(), attack.defender);
		//TODO abilities
		/*if(OldMove.attack(this.channel, attack)){
			//if(attack.defender.lastMove.getMove() == Move.REGISTRY.get("DESTINY_BOND")){
			this.sendMessage(attack.attacker.mention()
					+" was taken down with "+attack.defender.mention());
			attack.attacker.HP = 0;
			//}
			//if(attack.attacker.lastMove.getMove() == Move.REGISTRY.get("AFTER_YOU")){
			IAttack defenderAttack = this.attacks.get(attack.defender);
			if(defenderAttack != null && !defenderAttack.isCanceled()){
				this.sendMessage(attack.attacker.mention()+" made "+attack.defender.mention()+" go next!");
				//this.attackLogic(defenderAttack.get());
				//defenderAttack.get().cancel();
			} else {
				this.sendMessage("But there wasn't anything to do!"); //TODO better message
			}*/
			//}
			/*this.onSafeLeaveBattle(attack.defender);
			if(attack.defender.hasAbility(Abilities.AFTERMATH)){
				for(Player player : this.participants){
					if(player == attack.defender)
						continue;
					if(player.hasAbility(Abilities.DAMP)){
						this.preventExplosion(player, attack.defender);
						//If the player has <= 0 HP, then the after-attacks check will catch it
						return false;
					}
				}
				//Else we actually do damage
				attack.attacker.HP -= attack.attacker.getMaxHP()/4;
			}
		}
		return false;*/
	}

	@Override
	protected boolean checkWin(){
		if(this.red.has(Effects.NonVolatile.FAINTED)){
			this.wonBattleMessage(this.blue);
			return true;
		}
		if(this.blue.has(Effects.NonVolatile.FAINTED)){
			this.wonBattleMessage(this.red);
			return true;
		}
		return false;
	}

	@Override
	public int getMaxPlayers(){
		return 2;
	}

	@Override
	public int getMinPlayers(){
		return 2;
	}

	private void wonBattleMessage(Player player){
		this.sendMessage(player.mention()+" won the battle against "+
				((player == this.blue) ? this.red.mention() : this.blue.mention())+"!");
	}

	@Override
	public String getBattleType(){
		return "Single Battle";
	}
}
