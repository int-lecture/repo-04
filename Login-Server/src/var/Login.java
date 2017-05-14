package var;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("")
public class Login {
	String user;
	String password;
	String token;
	Date expireDate;
	String pseudonym;
	boolean success=true;


	//Speichern von User und Password
	static HashMap<String, String> userpassword=new HashMap <String,String>();
	//Speichern von User und Token und Expire Date von Token
	static HashMap<String, JSONObject> usertoken=new HashMap<String, JSONObject>();

 	public String getToken(){
 		return this.token;
 	}
	public boolean isValidToken(JSONObject jobj) throws ParseException{
		TimeZone tz = TimeZone.getTimeZone("GMT+1");
		Date currentTime=Transmitter.sdf.parse(tz.toString());
		Date date = Transmitter.sdf.parse(jobj.optString("expireDate"));
		JSONObject savedData = usertoken.get(jobj.get(pseudonym));
		if((savedData.getString(token)!=(jobj.getString(token)))&&  date.before(currentTime)){
			success=false;
			return success;
		}
		return success;
	}

	public boolean isValidEMail(String mail){
		Pattern p = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
		Matcher m = p.matcher(mail);
		return m.matches();
	}

	public boolean isBase64(String token){
		String stringToBeChecked = token;
		boolean isBase = org.apache.commons.codec.binary.Base64.isArrayByteBase64(stringToBeChecked.getBytes());
		return isBase;
	}

	public String createToken() {
		  SecureRandom random = new SecureRandom();
		    byte bytes[] = new byte[128];
		    random.nextBytes(bytes);
		    Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		String token = encoder.encodeToString(bytes);
		return token;
	}
	public Login() {

	}
	public Login(String user, String password, String token, Date expireDate) {
		this.user = user;
		this.password = password;
		this.token = token;
		this.expireDate = expireDate;
	}

	public String toString() {
		return String.format("{ 'user': '%s', 'password': '%s'}".replace('\'', '"'), user, password);
	}

	public String toStringT() {
		SimpleDateFormat sdf = new SimpleDateFormat(Transmitter.ISO8601);
		return String.format("{ 'token': '%s', 'expireDate': '%s'}".replace('\'', '"'), token, sdf.format(new Date()));

	}

	public String toStringS(JSONObject jobj) throws ParseException {
		Date date = Transmitter.sdf.parse(jobj.optString("expireDate"));
		return String.format("{ 'succes': '%s', 'expireDate': '%s'}".replace('\'', '"'), success, date);

	}

	@PUT
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(String log) throws ParseException {
		JSONObject jobj = new JSONObject(log);
		Date date = Transmitter.sdf.parse(jobj.optString("expireDate"));
		jobj.put(jobj.getString(token), createToken());
		Login login = new Login(jobj.optString(user), jobj.optString(password), jobj.optString(token), date);
		if ((login.user != null && login.password != null && login.token != null && login.expireDate != null) || !isValidEMail(user)) {
			if(userpassword.containsKey(login.user)){

				if(userpassword.get(login.user)!=login.password){
					// return Response.status(401);
					return Response.status(Response.Status.UNAUTHORIZED).entity("Passwort falsch.").build();
				}
			}

			JSONObject tokenisizer =new JSONObject();
			tokenisizer.put("token", jobj.getString("token"));
			tokenisizer.put("expireDate", date);
			usertoken.put(jobj.getString(user), tokenisizer);

			// return Response.status(200);
			return Response.status(Response.Status.OK).entity(login.toStringT()).build();
		} else {
			// return Response.status(400);
			return Response.status(Response.Status.BAD_REQUEST).entity("User existiert nicht.").build();
		}

	}

	@PUT
	@Path("/auth")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response auth(String auth) throws ParseException{
		JSONObject jobj = new JSONObject(auth);
		if (isValidToken(jobj) && isBase64(jobj.getString("token"))) {
			JSONObject temp= usertoken.get(jobj.getString("pseudonym"));

			// return Response.status(200);
			return Response.status(Response.Status.OK).entity(toStringS(temp)).build();
		}
		else{
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

	}
}
