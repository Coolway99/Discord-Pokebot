package coolway99.discordpokebot.jsonUtils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class JSONObject{

	protected final Map<String, Object> root;

	public JSONObject(@NotNull Map<String, Object> o){ this.root = o; }

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

	@Contract("_, !null -> !null; null, null -> null")
	public <T> T getObject(String key, T def){
		Object o = this.getObject(key);
		try{
			//Yes IntelliJ, I know it's an unchecked cast >.>
			//noinspection unchecked
			return (o != null) ? (T) o : def;
		} catch(ClassCastException e){
			return def;
		}
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
		return (o instanceof Map) ? new JSONObject((Map<String, Object>) o) : def;
	}

	@Contract("null -> null")
	public Object[] getArray(String key){
		return this.getArray(key, (Object[]) null);
	}

	@Contract("null, null -> null; _, !null -> !null")
	public Object[] getArray(String key, Object[] def){
		Object o = this.getObject(key);
		if(!(o instanceof ScriptObjectMirror) || !((ScriptObjectMirror) o).isArray()) return def;
		return ((ScriptObjectMirror) o).to(Object[].class);
	}

	@Contract("null, _ -> null")
	public <T> T[] getArray(String key, @NotNull Class<T> type){
		return this.getArray(key, type, null);
	}

	@Contract("null, _, null -> null; _, _, !null -> !null")
	public <T> T[] getArray(String key, @NotNull Class<T> type, T[] def){
		Object[] o = this.getArray(key);
		//noinspection unchecked
		if(o == null) return def;
		try{
			return (T[]) o;
		} catch(ClassCastException e){
			return def;
		}
	}

	@Nullable
	public Object call(@NotNull String functionName, Object thiz, Object... args){
		//return this.root.callMember(functionName, args);
		Object o = this.root.get(functionName);
		if(!(o instanceof ScriptObjectMirror) || !((ScriptObjectMirror) o).isFunction()) return null;
		return ((ScriptObjectMirror) o).call(thiz, args);
	}

	@Nullable
	public ScriptObjectMirror getFunction(@NotNull String functionName){
		return (ScriptObjectMirror) this.getObject(functionName);
	}

	@Nullable
	public <T> T getFunction(@NotNull String functionName, @NotNull Class<T> type){
		return this.getFunction(functionName, type, null);
	}

	@Nullable
	public <T> T getFunction(@NotNull String functionName, @NotNull Class<T> type, T def){
		ScriptObjectMirror o = (ScriptObjectMirror) this.root.get("functionName");
		if(o == null || !o.isFunction()) return def;
		return o.to(type);
	}

	public Map<String, Object> getRoot(){
		return this.root;
	}

	public Object put(String key, Object val){
		return this.root.put(key, val);
	}
}
