package var.chatclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("")
public class HelloWorld {

	@GET
	@Path("helloworld")
	@Produces("text/plain")
	public String sayHello() {
		return "Hello World!\n";

	}

}
