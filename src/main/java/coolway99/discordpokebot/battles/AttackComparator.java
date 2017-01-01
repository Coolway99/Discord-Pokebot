package coolway99.discordpokebot.battles;

import java.util.Comparator;

public class AttackComparator implements Comparator<IAttack>{
	@Override
	public int compare(IAttack o1, IAttack o2){
		int priority = o2.move.getMove().getPriority().getPriority();
		priority -= o1.move.getMove().getPriority().getPriority();
		if(priority != 0) return priority;
		return o2.attacker.getSpeedStat() - o1.attacker.getSpeedStat();
	}
}
