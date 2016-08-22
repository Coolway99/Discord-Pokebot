package coolway99.discordpokebot.states;

public enum Natures{ //In this case, "Health" is null, since nature never applies to health
	LONELY(Stats.ATTACK, Stats.DEFENSE),
	BRAVE(Stats.ATTACK, Stats.SPEED),
	ADAMANT(Stats.ATTACK, Stats.SPECIAL_ATTACK),
	NAUGHTY(Stats.ATTACK, Stats.SPECIAL_DEFENSE),
	
	BOLD(Stats.DEFENSE, Stats.ATTACK),
	RELAXED(Stats.DEFENSE, Stats.SPEED),
	IMPISH(Stats.DEFENSE, Stats.SPECIAL_ATTACK),
	LAX(Stats.DEFENSE, Stats.SPECIAL_DEFENSE),
	
	MODEST(Stats.SPECIAL_ATTACK, Stats.ATTACK),
	MILD(Stats.SPECIAL_ATTACK, Stats.DEFENSE),
	QUIET(Stats.SPECIAL_ATTACK, Stats.SPEED),
	RASH(Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENSE),
	
	CALM(Stats.SPECIAL_DEFENSE, Stats.ATTACK),
	GENTLE(Stats.SPECIAL_DEFENSE, Stats.DEFENSE),
	SASSY(Stats.SPECIAL_DEFENSE, Stats.SPEED),
	CAREFUL(Stats.SPECIAL_DEFENSE, Stats.SPECIAL_ATTACK),
	
	TIMID(Stats.SPEED, Stats.ATTACK),
	HASTY(Stats.SPEED, Stats.DEFENSE),
	JOLLY(Stats.SPEED, Stats.SPECIAL_ATTACK),
	NAIVE(Stats.SPEED, Stats.SPECIAL_DEFENSE),
	
	HARDY(Stats.HEALTH, Stats.HEALTH),
	DOCILE(Stats.HEALTH, Stats.HEALTH),
	SERIOUS(Stats.HEALTH, Stats.HEALTH),
	BASHFUL(Stats.HEALTH, Stats.HEALTH),
	QUIRKY(Stats.HEALTH, Stats.HEALTH)
	;
	
	public final Stats increase;
	public final Stats decrease;
	
	Natures(Stats increase, Stats decrease){
		this.increase = increase;
		this.decrease = decrease;
	}
	
	public double getStatMultiplier(Stats stat){
		if(stat == this.increase) return 1.1;
		if(stat == this.decrease) return 0.9;
		return 1;
	}
	
	public double getStatMultiplier(int index){
		return this.getStatMultiplier(Stats.getStatFromIndex(index));
	}
	
	public boolean hasEffect(){
		return !(this.increase == Stats.HEALTH);
	}
	
	public String getExpandedText(){
		if(!this.hasEffect()) return this.toString();
		return this.toString()+
				" (+"+
				this.increase+
				") (-"+
				this.decrease+
				')';
	}
}
