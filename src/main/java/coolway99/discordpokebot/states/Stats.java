package coolway99.discordpokebot.states;

public enum Stats{
	HEALTH(0),
	ATTACK(1),
	SPECIAL_ATTACK(2),
	DEFENSE(3),
	SPECIAL_DEFENSE(4),
	SPEED(5),
	//These shouldn't be used in normal stats
	ACCURACY(6),
	EVASION(7);

	private final int index;

	Stats(int x){
		this.index = x;
	}

	public static Stats getStatFromIndex(int i){
		switch(i){
			default:
			case 0:
				return HEALTH;
			case 1:
				return ATTACK;
			case 2:
				return SPECIAL_ATTACK;
			case 3:
				return DEFENSE;
			case 4:
				return SPECIAL_DEFENSE;
			case 5:
				return SPEED;
			case 6:
				return ACCURACY;
			case 7:
				return EVASION;
		}
	}

	public static Stats getStatFromString(String s){
		switch(s.toLowerCase()){
			case "health":
			case "h":
			case "hp":{
				return Stats.HEALTH;
			}
			case "a":
			case "att":
			case "attack":{
				return Stats.ATTACK;
			}
			case "special attack":
			case "special_attack":
			case "sattack":
			case "sa":{
				return Stats.SPECIAL_ATTACK;
			}
			case "defense":
			case "d":{
				return Stats.DEFENSE;
			}
			case "special defense":
			case "special_defense":
			case "sdefense":
			case "sd":{
				return Stats.SPECIAL_DEFENSE;
			}
			case "speed":
			case "sp":{
				return Stats.SPEED;
			}
			default:{
				return null;
			}
		}
	}

	public int getIndex(){
		return this.index;
	}
}