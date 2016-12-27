package coolway99.discordpokebot.states;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum Side{
	//It's basically a binary check. Enums are great, don't judge me...
	A,
	B,
	UNKNOWN, //Will always fail checks, might be replaced with null...
	;

	@Contract("null, _ -> false; _, null -> false")
	public static boolean isSameSide(@Nullable Side a, @Nullable Side b){
		if(a == null || b == null) return false;
		if(a == UNKNOWN || b == UNKNOWN) return false;
		return a == b;
	}
}
