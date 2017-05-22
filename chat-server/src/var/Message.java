package var;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

/**
 * Chat message.
 */
@Path("")
class Message {
	


	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	static SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);
	
	String token;
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
	public Message(String token,String from, String to, Date date, String text, int sequence) {
		this.token = token;
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
	public Message(String token, String from, String to, Date date, String text) {
		this(token,from, to, date, text, 0);
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

	public String toStringpost() {
		SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);

		return String.format("{ 'date': '%s', 'sequence': '%s'}".replace('\'', '"'),
				sdf.format(new Date()), sequence);
	}

	/**
	 * Wandelt ein Nachricht in ein JsonObj
	 *
	 * @param msg Obj welches umgewandelt wird
	 * @return Jsonobj
	 * @throws ParseException
	 */
	 public JSONObject messageToJson(Message msg) throws ParseException{
	    	JSONObject jobj=new JSONObject();
	    	jobj.put("from", msg.from);
	    	jobj.put("to", msg.to);
	    	jobj.put("date", sdf.format(msg.date));
	    	jobj.put("text", msg.text);
	    	jobj.put("sequence", msg.sequence);
	    	System.out.println(jobj);
	    	return jobj;

	    }
	 /**
		 * Wandelt die Nachricht in ein Jsonobj + Userverwaltung
		 * @param json obj welches in JsonFormat ge�ndert wird
		 * @return ein JSONObject
		 */
		public JSONObject isJsontrue(boolean json){
			JSONObject obj = new JSONObject();
			SimpleDateFormat sdf = new SimpleDateFormat(Transmitter.ISO8601);
			obj.put("date", sdf.format(date));
			obj.put("sequence", sequence);
			if (!json) {
				obj.put("from", from);
				obj.put("to", to);
				obj.put("text", text);
			}
			return obj;
	}
}