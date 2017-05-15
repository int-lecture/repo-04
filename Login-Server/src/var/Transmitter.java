package var;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/header")
public class Transmitter {

	/** Verwaltung von Nachrichten in ein JSONArray */
	static HashMap<String, JSONArray> messages = new HashMap<String, JSONArray>();
	/** Verwaltung von Sequenznummern der User */
	static HashMap<String, Integer> messageIds = new HashMap<String, Integer>();


	/** String for date parsing in ISO 8601 format. */
	 public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	static SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);

	/**
	 * Die Methode liefert die Sequenznummer eines Benutzers
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
	public Response postMessage(String recieved) throws ParseException {
		JSONObject jobj = new JSONObject(recieved);
		Date date = sdf.parse(jobj.optString("date"));
		Login log = new Login();
		int sequence = getSequence(jobj.optString("to")) ;
		//messageID erh�hen bei neuer Nachricht
		messageIds.put(jobj.optString("to"), ++sequence);
		jobj.put("sequence", sequence);

		Message msg = new Message(jobj.optString("token"), jobj.optString("from"), jobj.optString("to"), date, jobj.optString("text"), sequence);

		if (msg.token != null && msg.from != null && msg.to != null && msg.date != null && msg.text != null) {
			if (log.isBase64(msg.token)&& log.isValidToken(jobj)) {
				//Testen ob Erste NAchricht f�r den EMpf�nge
				if (!messages.containsKey(msg.to)) {
					JSONArray array = new JSONArray();
					messages.put(msg.to, array);
			}

			messages.get(msg.to).put(msg.messageToJson(msg));
			// return Response.status(201)
			return Response.status(Response.Status.CREATED).entity(msg.toStringpost()).build();
			}
			// return Response.status(401);
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		else {
			// return Response.status(400);
			return Response.status(Response.Status.BAD_REQUEST).entity("Falsches Format.").build();
		}
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
	public Response getMessages(@PathParam("user_id") String user_id) throws ParseException, JSONException{
		return this.getMessages(user_id, 0);
	}

	/**
	 * Die Methode liefert die Nachricht mit der entsprechenden Sequenznummer
	 *
	 * @param user_id Name des Benutzers
	 * @param seqence Sequenznummer der Nachricht
	 * @return Nachricht
	 * @throws ParseException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/messages/{userid}/{sequenceNumber}")
	public Response getMessages(@PathParam("userid") String user_id, @PathParam("sequenceNumber") int sequence) throws JSONException {
		if (messages.containsKey(user_id)) {
			JSONArray cloneArray = messages.get(user_id);
			JSONArray msgArray=new JSONArray();
			//alle neuen Nachrichten in msgArray speichern
			for (int i=0; i<cloneArray.length();i++){
				if ((cloneArray.getJSONObject(i).getInt("sequence")>sequence)||sequence==0){
						msgArray.put(cloneArray.getJSONObject(i));
				}
			}
			//Alte Nachrichten l�schen
			if (msgArray.length()!=0){
				messages.put(user_id, msgArray);

				try {
					// return Response.status(200);
					return Response.status(Response.Status.OK).entity(msgArray.toString()).build();
				} catch (Exception e) {
					e.printStackTrace();
					// return Response.status(500);
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			} else {
				// return Response.status(204);
				return Response.status(Response.Status.NO_CONTENT).build();
			}
		}
		else {
			// return Response.status(400);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

}
