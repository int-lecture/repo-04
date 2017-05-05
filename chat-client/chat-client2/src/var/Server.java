package var;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
@Path("")
public class Server {
	public static void main(String[] args) throws IllegalArgumentException, IOException{
		final String baseUri = "http://localhost:5000/";
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
	@Path("/send")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMsg(String msg){
    	
    	System.out.println(msg);
		return null;
    	
    	
    }
}
