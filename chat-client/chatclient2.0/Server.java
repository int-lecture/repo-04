package var.chatclient;

import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

import java.io.IOException;

public class Server {
	public static void main(String[] args) throws IllegalArgumentException, IOException {

		final String baseUri = "http://localhost:5000";
		final String paket = "var.chatclient";
		final Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("com.sun.jersey.config.property.packages", paket);
		
		System.out.println("Starte grizzly...");
		SelectorThread threadSelector = GrizzlyWebContainerFactory.create(
		baseUri, initParams);
		System.out.printf("Grizzly l�uft unter %s%n", baseUri);
		System.out.println("[ENTER] dr�cken, um Grizzly zu beenden");
		System.in.read();
		threadSelector.stopEndpoint();
		System.out.println("Grizzly wurde beendet");
		System.exit(0);
	}

	
	
	
	
	
	
}
