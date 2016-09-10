package coolway99.discordpokebot.states;


public enum Items{
	NULL(),
	BLACK_BELT(Types.FIGHTING),
	BLACK_GLASSES(Types.DARK),
	CHARCOAL(Types.FIRE),
	DRAGON_FANG(Types.DRAGON),
	HARD_STONE(Types.ROCK),
	MAGNET(Types.ELECTRIC),
	METAL_COAT(Types.STEEL),
	MIRACLE_SEED(Types.GRASS),
	MYSTIC_WATER(Types.WATER),
	//Had to change it's name slightly because java
	@SuppressWarnings("SpellCheckingInspection")
	NEVERMELT_ICE(Types.ICE),
	// Only 10% PINK_BOW(Types.NORMAL),
	POISON_BARB(Types.POISON),
	// Only 12.5% Polkadot Bow(Types.NORMAL),
	SHARP_BEAK(Types.FLYING),
	SILK_SCARF(Types.NORMAL),
	SILVER_POWDER(Types.BUG),
	SOFT_SAND(Types.GROUND),
	SPELL_TAG(Types.GHOST),
	TWISTED_SPOON(Types.PSYCHIC),

	;

	private final Types type;

	Items(){
		this.type = null;
	}

	Items(Types type){
		this.type = type;
	}

	public Types getPoweredUpType(){
		return this.type;
	}
}
