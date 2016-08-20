package coolway99.discordpokebot.web;

import coolway99.discordpokebot.Pokebot;
import spark.Spark;

public class WebInterface{

	private static final String SCOPE = "identify";

	public static void initWebInterface(int port){
		Spark.port(port);
		Spark.get("/authorize", (req, res) -> {
			res.redirect("https://discordapp.com/oauth2/authorize?response_type=code&scope="+SCOPE+"&client_id" +
					"="+Pokebot.config.CLIENT_ID+"&redirect_uri=http://localhost:9009/");
			return "";
		});
		Spark.get("/", (req, res) -> {
			String token = OAuth_Handler.getToken(req.queryParams("code"));
			String id = OAuth_Handler.getUserID(token);
			return token+'\n'+id;
		});
		//IUser test = Pokebot.client.getUserByID("blehblah");
		/*
		Spark.port(port);
		Spark.get("/", (request, response) -> "blargh");
*/
	}
}