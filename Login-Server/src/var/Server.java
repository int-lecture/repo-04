package var;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class Server {
	public static void main(String[] args) throws IllegalArgumentException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		//Test parameters
		//Login.userpassword.put("bob@web.de", "halloIchbinBob");
		
	//New TEST PARAMETERS
		Database mongo = new Database();
		String password = SecurityHelper.hashPassword("halloIchbinBob");
		JSONObject object= new JSONObject("{'user':'bob@web.de','pseudonym':'bob'}" );
		object.put("password", password);
		mongo.saveUserData(object);
		String password2 =  SecurityHelper.hashPassword("1");
		JSONObject jobject= new JSONObject("{'user':'job@web.de','pseudonym':'job'}" );
		jobject.put("password", password2);
		mongo.saveUserData(jobject);
	final String baseUri = "http://141.19.142.58:5001/";
	final String paket = "var";
	final Map<String, String> initParams = new HashMap<String, String>();
	initParams.put("com.sun.jersey.config.property.packages", paket);
	System.out.println("Starte grizzly...");
	SelectorThread threadSelector = GrizzlyWebContainerFactory.create(
	baseUri, initParams);
	System.out.printf("Grizzly läuft unter %s%n", baseUri);
	System.out.println("[ENTER] drücken, um Grizzly zu beenden");
	System.in.read();
	threadSelector.stopEndpoint();
	System.out.println("Grizzly wurde beendet");
	System.exit(0);
}
	}


