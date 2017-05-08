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

@Path("/")
public class Transmitter {
	static HashMap<String, JSONArray> messages = new HashMap<String, JSONArray>();
	static HashMap<String, Integer> messageIds = new HashMap<String, Integer>();
	
	private Queue<Message> messagesque = new ArrayDeque<Message>();

	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";
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
	 * Methode für das senden der Nachrichten.
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
		
		int sequence = getSequence(jobj.optString("to")) ;
		messageIds.put(jobj.optString("to"), ++sequence);
		jobj.put("sequenceNumber", sequence);
		

		Message msg = new Message(jobj.optString("from"), jobj.optString("to"), date, jobj.optString("text"), sequence);

		if (msg.from != null && msg.to != null && msg.date != null && msg.text != null) {

			if (!messages.containsKey(msg.to)) {
				JSONArray array = new JSONArray();
				messages.put(msg.to, array);
			}
			
			System.out.println(msg.toString());
			System.out.println(msg.toStringpost());
			messages.get(msg.to).put(msg.messageToJson(msg));
			messagesque.add(msg);
			
			// return Response.status(201)
			return Response.status(Response.Status.CREATED).entity(msg.toStringpost()).build();
		} 
		else {
			// return Response.status(400);
			return Response.status(Response.Status.BAD_REQUEST).entity("User existiert nicht.").build();
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
	public Response getMessages(@PathParam("userid") String userID, @PathParam("sequenceNumber") int sequenceNumber) throws JSONException {
		if (messages.containsKey(userID)) {
			JSONArray jsonarray = new JSONArray();
			jsonarray = messages.get(userID);
			List<Message> neuemsg = recMessages(sequenceNumber);
			if (neuemsg.isEmpty()) {
				return Response.status(Response.Status.NO_CONTENT).entity("No new messages").build();
			} else {
				for (Message msg : neuemsg) {
					jsonarray.put(msg.isJsontrue(false));
					System.out.println("testmsgrec");
				}
				try {
					return Response.status(Response.Status.OK).entity(jsonarray.toString()).build();
				} catch (Exception e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			}
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("User not found.").build();
}}
	
	/**
	 * Gibt alle Nachrichten aus mit einer SeqNr kleiner als der übergebende Paramater
	 *
	 * @param sequenceNumber letzte seqnr      
	 * @return alle nachrichten < der sequenz
	 */
	public List<Message> recMessages(int sequenceNumber) {
		ArrayList<Message> recmsg = new ArrayList<>();

		for (Message message : messagesque) {
			if (sequenceNumber == 0 || message.sequence > sequenceNumber) {
				recmsg.add(message);
			}
		}
		return recmsg;
	}
	
	
	
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/messages/{user_id}/{sequence_number}")
//	public Response getMessages(@PathParam("user_id") String user_id, @PathParam("sequence_number") int sequence) throws ParseException {
//		
		// purified list ist eine Liste mit den noch nicht gelesenen Nachrichten
//		JSONArray purifiedArray = new JSONArray();
//		JSONObject jobj = new JSONObject();
//		boolean listIsEmpty = true;
//		
//		Date date = sdf.parse(jobj.optString("date"));
//		int seq = getSequence(jobj.optString("to")) ;
//		
//		
//		if(sequence==0){
//			if(messageIds.containsKey(user_id)){
//				Message msg = new Message(jobj.optString("from"), jobj.optString("to"), date, jobj.optString("text"), sequence);
//				JSONArray temp = messages.get("user_id");
//				for (int i = 0; i < temp.length(); i++) {
//					jobj = temp.getJSONObject(i);
//					System.out.println(jobj.toString());
//				}
			//return Response.status(Response.Status.NO_CONTENT).entity(msg.toStringget()).build();
//			}
//			else{
			//return Response.status(Response.Status.BAD_REQUEST).entity("User not found.").build();
//			}
//		}
//		if(sequence!=0){
//			if(messageIds.containsKey(user_id)){
//				Message msg = new Message(jobj.optString("from"), jobj.optString("to"), date, jobj.optString("text"), sequence);
				//return Response.status(Response.Status.NO_CONTENT).entity(msg.toStringget()).build();
//			}
//			else{
				//return Response.status(Response.Status.BAD_REQUEST).entity("User not found.").build();
//			}
//		}
		// remove all old messages
//		int newSequence = getSequence(user_id);
//		int messageAmount = newSequence - sequence;
//		if (messageAmount != 0){
//			messageAmount = messageAmount - 1;
//		}
		// delete old messages
//		messages.replace(user_id, purifiedArray);
		// send messages to Client
//		return Response.status(Status.CREATED).entity(purifiedArray.toString()).build();
//		return null;
//	}
}
