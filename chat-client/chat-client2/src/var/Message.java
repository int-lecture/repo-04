package var.chatclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Chat message.
 */
@Path("")
class Message {

	/** From. */
	String from;

	/** To. */
	String to;

	/** Date. */
	Date date;

	/** Text. */
	String text;

	/** Sequence number. */
	int sequence;

	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";

	static SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);

	/**
	 * Create a new message.
	 *
	 * @param from
	 *            From.
	 * @param to
	 *            To.
	 * @param date
	 *            Date.
	 * @param text
	 *            Contents.
	 * @param sequence
	 *            Sequence-Number.
	 */
	public Message(String from, String to, Date date, String text, int sequence) {
		this.from = from;
		this.to = to;
		this.date = date;
		this.text = text;
		this.sequence = sequence;
	}

	/**
	 * Create a new message.
	 *
	 * @param from
	 *            From.
	 * @param to
	 *            To.
	 * @param date
	 *            Date.
	 * @param text
	 *            Contents.
	 */
	public Message(String from, String to, Date date, String text) {
		this(from, to, date, text, 0);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);

		return String.format("{ 'from': '%s', 'to': '%s', 'date': '%s', 'text': '%s'}".replace('\'', '"'), from, to,
				sdf.format(new Date()), text);
	}
	
	/**
	 * Überprüft ob der übergebenden String in JSON-Format 
	 * abgeändert werden kann
	 * 
	 * @param test Text der überprüft werden soll
	 * @return True wenn JSONkompatibel sonst false
	 */
	public boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Übergibt den aktullen Status einer Nachricht
	 * 
	 * @return Status der Nachricht
	 * @throws JSONException
	 */
	public JSONObject statusMessage() throws JSONException {
		JSONObject jobj = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);
		jobj.put("date", sdf.format(date));
		jobj.put("sequence", sequence);
		return jobj;
	}

	/**
	 * Das übergebende Objekt wird in ein JSONkompatibles 
	 * Objekt übersetzt
	 * 
	 * @return JSONObjekt
	 */
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("from", from);
			obj.put("to", to);
			obj.put("date", sdf.format(new Date()));
			obj.put("text", text);
			obj.put("sequence", sequence);
			return obj;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Versendet die übergebende Nachricht
	 * 
	 * @param msg Nachricht die versendt werden soll
	 * @return
	 */
	public Message sendMessage(Message msg) {
		User u = null;
		msg.sequence = u.sequence++;
		u.list.add(msg);
		System.out.println(String.format("%s -> %s [%d]: %s", msg.from, msg.to, msg.sequence, msg.text));
		return msg;
	}

	/**
	 * Methode zur Umwandlung von String in JSONObject
	 * 
	 * @param text Text welcher übersetzt wird
	 * @return Nachricht, welche auf JSON-Format basiert
	 * @throws ParseException
	 * @throws JSONException
	 */
	public static Message toJSON(String text) throws ParseException, JSONException {
		JSONObject jobj = new JSONObject(text);
		Date date = sdf.parse(jobj.optString("date"));
		return new Message(jobj.optString("from"), jobj.optString("to"), date, jobj.optString("text"),
				jobj.optInt("sequence"));
	}

	/**
	 * Methode für das senden der Nachrichten.
	 * 
	 * @param text Nachricht die versendet werden soll
	 * @return die übergebende Nachricht
	 * @throws ParseException
	 * @throws JSONException
	 */
	@PUT
	@Path("/send")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response send(String text) throws ParseException, JSONException {
		Message msg = Message.toJSON(text);
		if (msg.to != null && msg.from != null && msg.date != null && msg.text != null) {
			msg = msg.sendMessage(msg);
			return Response.status(Response.Status.CREATED).entity(msg.statusMessage().toString()).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Format").build();
		}

	}

	/**
	 * Die Methode zeigt alle Nachrichten an, welche bisher empfangen wurden
	 * 
	 * @param user Name des Benutzers
	 * @param seq Sequencenummer der Nachricht, sonst alle Nachrichten
	 * @return Nachricht
	 */
	@GET
	@Path("/messages/{userid}/{sequenceNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response receive(@PathParam("userid") String user, @PathParam("sequenceNumber") int seq) {
		User u = null;
		if (seq == 0) {
			for (Message message : u.list) {
				u.list.add(message);
			}
			return Response.status(Response.Status.CREATED).entity(u.list.toString()).build();
		}
		if (seq != 0) {
			return Response.status(Response.Status.CREATED).entity(u.list.toString()).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("User not found.").build();
		}
	}

	/**
	 * Die Methode zeigt alle Nachrichten an, welche bisher empfangen wurden
	 * 
	 * @param user Name des Benutzers
	 * @return Nachricht
	 */
	@GET
	@Path("/messages/{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response receive(@PathParam("userid") String user) throws JSONException, ParseException {
		return this.receive(user, 0);
	}

}