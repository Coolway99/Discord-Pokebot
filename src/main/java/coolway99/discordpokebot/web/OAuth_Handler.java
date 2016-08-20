package coolway99.discordpokebot.web;


import com.google.gson.Gson;
import coolway99.discordpokebot.Pokebot;
import coolway99.discordpokebot.web.json.TokenResponse;
import coolway99.discordpokebot.web.json.UserContainer;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.net.URLEncoder;

public class OAuth_Handler{

	private static final Gson gson = new Gson();

	public static String getToken(String code){
		try{
			Request request = Request.Post("https://discordapp.com/api/oauth2/token"
					+"?grant_type=authorization_code"
					+"&code="+code
					+"&redirect_uri="+URLEncoder.encode("http://localhost:9009/", "UTF-8") //TODO
					+"&client_id="+Pokebot.config.CLIENT_ID
					+"&client_secret="+Pokebot.config.CLIENT_SECRET);
					request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			System.out.println(request.toString());
			TokenResponse tokenResponse = gson.fromJson(request.execute().returnContent().asString(), TokenResponse.class);
			if(tokenResponse.access_token == null){
				System.err.println(tokenResponse.error);
				return null;
			}
			return tokenResponse.access_token;
		} catch(IOException e){
			e.printStackTrace();
			System.err.println("\nUnable to get token");
		}
		return null;
	}

	public static String getUserID(String token){
		try{
			//String response =
			Request request = Request.Get("https://discordapp.com/api/users/@me")
					.addHeader("Authorization", "Bearer "+token);
			String response = request.execute().returnContent().asString();
			UserContainer user = gson.fromJson(response, UserContainer.class);
			return user.id;
		} catch(ClientProtocolException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
}
