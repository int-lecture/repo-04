package var;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/")
public class Transmitter {
	Database mongo= new Database();
	/** Verwaltung von Nachrichten in ein JSONArray */
	static HashMap<String, JSONArray> messages = new HashMap<String, JSONArray>();
	/** Verwaltung von Sequenznummern der User */
	static HashMap<String, Integer> messageIds = new HashMap<String, Integer>();

	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	static SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);

 	public boolean isValidToken(JSONObject jobj) throws ParseException{
 		SimpleDateFormat sdf = new SimpleDateFormat(Transmitter.ISO8601);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); //Aktuelle Uhrzeit
		String tempTime = sdf.format(c.getTime());
		Date currentTime=sdf.parse(tempTime);
		//Zugriff auf Speicher ohne Datenbank
		//JSONObject savedData = usertoken.get(jobj.get("pseudonym"));
		//Zugriff auf Speicher mit Datenbank
		JSONObject savedData= mongo.getTokenData(jobj.getString("from"));
		Date otherDate=sdf.parse(savedData.getString("expireDate"));
		//Vergleich übergebener Token mit dem gespeihcerten+ testen ob expireDate überschritten wurde
		if(!(savedData.getString("token").equals(jobj.getString("token")))||  otherDate.before(currentTime)){
			return false;
		}
		return true;
	}
	
	
	/**
	 * Die Methode liefert die Sequenznummer f�r die NAchrichten eines Benutzers
	 *
	 * @param user_id Name des Benutzers
	 * @return Sequenznummer des Benuntzer
	 */
	public int getSequence(String user_id){
		if (!messageIds.containsKey(user_id)){
			messageIds.put(user_id, 0);
		}
		int sequence_number=messageIds.get(user_id);

		return sequence_number;
	}

	/**
	 * Methode f�r das senden der Nachrichten.
	 *
	 * @param recieved Nachricht die versendet werden soll
	 * @return statuscode + statusmessage
	 * @throws ParseException
	 */
	@PUT
	@Path("/send")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postMessage(String recieved) throws ParseException, JSONException {
		JSONObject jobj = new JSONObject(recieved);
		Date date = sdf.parse(jobj.optString("date"));
		int sequence = (int)mongo.retrieveAndUpdateSequence(jobj.getString("from")) ;
		//int sequence = getSequence(jobj.optString("to")) ;
		//messageID erh�hen bei neuer Nachricht
		//messageIds.put(jobj.optString("to"), ++sequence);
		jobj.put("sequence", sequence);
		Message msg = new Message(jobj.optString("token"),jobj.optString("from"), jobj.optString("to"), date, jobj.optString("text"), sequence);
		if (msg.token != null && msg.from != null && msg.to != null && msg.date != null && msg.text != null) {
			//Testen ob Erste NAchricht f�r den EMpf�nger
			if(isValidToken(jobj)){
			//if (!messages.containsKey(msg.to)) {
				//JSONArray array = new JSONArray();
			//	messages.put(msg.to, array);
			//}
			//messages.get(msg.to).put(msg.messageToJson(msg));
			mongo.storeMessage(msg);
			// return Response.status(201)
			return Response.status(Response.Status.CREATED).entity(msg.toStringpost()).header("Access-Control-Allow-Origin", "*").build();
		}else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Token ungültig").header("Access-Control-Allow-Origin", "*").build();
		}
			
		}
		else {
			// return Response.status(400);
			return Response.status(Response.Status.BAD_REQUEST).entity("Falsches Format.").header("Access-Control-Allow-Origin", "*").build();
		}
	}
	@OPTIONS
	@Path("/send")
	public Response optionsRegSend() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}
	/**
	 * Die Methode zeigt alle Nachrichten an, welche bisher empfangen wurden
	 *
	 * @param user_id Name des Benutzers
	 * @return Nachrichten
	 * @throws ParseException
	 * @throws JSONException
	 */
	@GET
	@Path("/messages/{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessages(@PathParam("user_id") String user_id,  @Context HttpHeaders header) throws ParseException, JSONException{
		return this.getMessages(user_id, 0, header);
	}

	/**
	 * Die Methode liefert die Nachricht mit der entsprechenden Sequenznummer
	 *
	 * @param user_id Name des Benutzers
	 * @param seqence Sequenznummer der Nachricht
	 * @return Nachricht
	 * @throws ParseException
	 * 
	 */
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/messages/{userid}/{sequenceNumber}")
	public Response getMessages(@PathParam("userid") String user_id, @PathParam("sequenceNumber") int sequence,  @Context HttpHeaders header) throws JSONException, ParseException {	
		MultivaluedMap<String, String> map = header.getRequestHeaders();
		List<Message> neueNachrichten= mongo.retrieveMessages(user_id,sequence,true);
		JSONArray cloneArray =new JSONArray();
		System.out.println("test1");
		System.out.println(neueNachrichten);
		if (neueNachrichten!=null) {
			System.out.println("test2");
			for (Message m : neueNachrichten) {
				System.out.println("test3");
				cloneArray.put(m.messageToJson(m));
			} 
			System.out.println("error");
			JSONArray msgArray=new JSONArray();
			//Lösche Token vom Token String und speichere es in 'JSONObject
			String temp=(map.get("Authorization").get(0)).replaceAll("Token ", "");
			JSONObject testToken = new JSONObject();
			testToken.put("token", temp);
			testToken.put("from", user_id);
			if(isValidToken(testToken)){
				
			
			//alle neuen Nachrichten in msgArray speichern
			for (int i=0; i<cloneArray.length();i++){
				if ((cloneArray.getJSONObject(i).getInt("sequence")>sequence)||sequence==0){
						msgArray.put(cloneArray.getJSONObject(i));
				}
			}
			//Alte Nachrichten l�schen
			if (msgArray.length()!=0){
		
				//messages.put(user_id, msgArray);
				try {
					// return Response.status(200);
					return Response.status(Response.Status.OK).entity(msgArray.toString()).header("Access-Control-Allow-Origin", "*").build();
				} catch (Exception e) {
					e.printStackTrace();
					// return Response.status(500);
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*").build();
				}
			} else {
				// return Response.status(204);
				return Response.status(Response.Status.NO_CONTENT).header("Access-Control-Allow-Origin", "*").build();
			}
			}else{
				return Response.status(Response.Status.UNAUTHORIZED).entity("Token ungültig").header("Access-Control-Allow-Origin", "*").build();
			}
		}
		else {
			// return Response.status(400);

			return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
		}
	}
	@OPTIONS
	@Path("/messages/{userid}/{sequenceNumber}")
	public Response optionsRegGet() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, token, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}
	@OPTIONS
	@Path("/messages/{userid}")
	public Response optionsRegGet2() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}

}
