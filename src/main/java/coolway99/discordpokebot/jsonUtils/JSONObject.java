package coolway99.discordpokebot.jsonUtils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class JSONObject{

	protected final ScriptObjectMirror root;

	public JSONObject(@NotNull ScriptObjectMirror o){
		this.root = o;
	}

	public Set<String> keySet(){
		return this.root.keySet();
	}

	public Collection<Object> values(){
		return this.root.values();
	}

	@Contract("null -> null")
	public Object getObject(String key){
		return this.root.get(key);
	}

	public int getInt(String key){ return this.getInt(key, 0); }

	public int getInt(String key, int def){
		Object o = this.getObject(key);
		return (o instanceof Integer) ? (Integer) o : def;
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	@Contract("null -> false")
	public boolean getBoolean(String key){ return this.getBoolean(key, false); }

	@SuppressWarnings({"BooleanParameter", "BooleanMethodNameMustStartWithQuestion"})
	@Contract("null, false -> false; null, true -> true")
	public boolean getBoolean(String key, boolean def){
		Object o = this.getObject(key);
		return (o instanceof Boolean) ? (Boolean) o : def;
	}

	@Contract("null -> null")
	public String getString(String key){ return this.getString(key, null); }

	@Contract("null, null -> null; _, !null -> !null")
	public String getString(String key, @Nullable String def){
		Object o = this.getObject(key);
		return (o instanceof String) ? (String) o : def;
	}

	@Contract("null -> null")
	public JSONObject getJSONObject(String key){ return this.getJSONObject(key, null); }

	@Contract("null, null -> null; _, !null -> !null")
	public JSONObject getJSONObject(String key, @Nullable JSONObject def){
		Object o = this.getObject(key);
		return (o instanceof ScriptObjectMirror) ? new JSONObject((ScriptObjectMirror) o) : def;
	}

	public Object call(@NotNull String functionName, Object... args){
		return this.root.callMember(functionName, args);
	}

	public ScriptObjectMirror getRoot(){
		return this.root;
	}

	public Object put(String key, Object val){
		return this.root.put(key, val);
	}
}
