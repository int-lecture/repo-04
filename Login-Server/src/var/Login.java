package var;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Path("")
public class Login {
	String user;
	String password;
	String token;
	Date expireDate;
	String pseudonym;
	boolean success=true;
	

	Database mongo= new Database();
	//Speichern von User und Password
	static HashMap<String, String> userpassword=new HashMap <String,String>();
	//Speichern von User und Token und Expire Date von Token
	static HashMap<String, JSONObject> usertoken=new HashMap<String, JSONObject>();

	public Login() {

	}
	public Login(String user, String password, String pseudonym) {
		this.user=user;
		this.password=password;
		this.pseudonym=pseudonym;
		this.expireDate=null;
		this.token=null;
	}
	
	/**
	 * Die Methode prüft den gesendeten Token auf Gleicheit und Gültigkeit
	 *
	 * @param jobj Pseudonym und token des Users
	 * @return True/False ob Token gültig
	 */
 	public boolean isValidToken(JSONObject jobj) throws ParseException{
 		SimpleDateFormat sdf = new SimpleDateFormat(Transmitter.ISO8601);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); //Aktuelle Uhrzeit
		String tempTime = sdf.format(c.getTime());
		Date currentTime=sdf.parse(tempTime);
		//Zugriff auf Speicher ohne Datenbank
		//JSONObject savedData = usertoken.get(jobj.get("pseudonym"));
		//Zugriff auf Speicher mit Datenbank
		JSONObject savedData= mongo.getTokenData(jobj.getString("pseudonym"));
		Date otherDate=sdf.parse(savedData.getString("expireDate"));
		//Vergleich übergebener Token mit dem gespeihcerten+ testen ob expireDate überschritten wurde
		if(!(savedData.getString("token").equals(jobj.getString("token")))||  otherDate.before(currentTime)){
			success=false;
			return success;
		}
		success=true;
		return success;
	}
	/**
	 * Die Methode testet, ob die gesendete Email-Adresse gültiges Format besitzt
	 *
	 * @param mail Mail Adresse des Benutzers
	 * @return True/False ob Email gültiges Format
	 */
	public boolean isValidEMail(String mail){
		Pattern p = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
		Matcher m = p.matcher(mail);
		return m.matches();
	}
	/**
	 * Die Methode testet, ob ein Token das richtige Format hat
	 *
	 * @param token Token des Benutzers
	 * @return True/False ob Token gültiges Format
	 */
	public boolean isBase64(String token){
		String stringToBeChecked = token;
		boolean isBase = org.apache.commons.codec.binary.Base64.isArrayByteBase64(stringToBeChecked.getBytes());
		return isBase;
	}
	/**
	 * Die Methode lierstellt ein Ablaufdatum 1 Tag in der Zukunft
	 *
	 * @param
	 * @return futureTime aktuelle Zeit +1 Tag
	 */
	public String createExpireDate() throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(Transmitter.ISO8601);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Heutiges Datum benutzen
		c.add(Calendar.DATE, 1); // 1 Tag gültigkeit
		String futureTime = sdf.format(c.getTime());
		return futureTime;


	}
	/**
	 * Die Methode erstellt ein Token nach Base 64
	 *
	 * @param
	 * @return token im Base 64
	 */
	public String createToken() {
		  SecureRandom random = new SecureRandom();
		    byte bytes[] = new byte[128];
		    random.nextBytes(bytes);
		    Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		String token = encoder.encodeToString(bytes);
		return token;
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


	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(String log) throws ParseException, NoSuchAlgorithmException, InvalidKeySpecException, JSONException {
		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		JSONObject jobj = new JSONObject(log);
		System.out.println(log);
		//Date date = Transmitter.sdf.parse(jobj.optString("expireDate"));
		jobj.put("token", createToken());
		Login login = new Login(jobj.getString("user"),jobj.getString("password"), jobj.getString("token"), expireDate);
		//Testen ob alle wichtigen Daten da sind
		if ((login.user != null && login.password != null && login.token != null /**  && login.expireDate != null **/) || !isValidEMail(user)) {
			//Checken ob username vorhanden ist ohne Datenbank
			//Checken ob Usewrname vorhanden mit Datenbank
			if(mongo.getUserData(login.user)!=null){
				//Passwort überprüfen ohne Daternbank
				//if(!userpassword.get(login.user).equals(login.password)){
				//Passwort überprüfen mit Datenbank ohne Security Helper
				//if(!mongo.getUserData(login.user).get("password").equals(login.password)){
				//Passwort überprüfen mit Datenbank und mit Securityhelper
				if(!SecurityHelper.validatePassword(login.password, (String) mongo.getUserData(login.user).get("password")  )){
					// return Response.status(401);
					System.out.println("psswort falsch");
					return Response.status(Response.Status.UNAUTHORIZED).entity("Passwort falsch.").header("Access-Control-Allow-Origin", "*").build();
				}
			JSONObject tokenisizer =new JSONObject();
			tokenisizer.put("token", jobj.getString("token"));
			//Abspeicherung OHNE DATENBANK
			tokenisizer.put("expireDate", createExpireDate());
			usertoken.put(jobj.getString("pseudonym"), tokenisizer);
			//abspeicherung in Datenbank:
			tokenisizer.put("pseudonym", jobj.getString("pseudonym"));
			mongo.saveTokenData(tokenisizer);
			// return Response.status(200);
			System.out.println("alles gut");
			return Response.status(Response.Status.OK).entity(login.toStringT()).header("Access-Control-Allow-Origin", "*").build();
			}}
			// return Response.status(400);
		System.out.println("no user");
		return Response.status(Response.Status.BAD_REQUEST).entity("User existiert nicht.").header("Access-Control-Allow-Origin", "*").build();


	}

	@POST
	@Path("/auth")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response auth(String auth) throws ParseException{
		JSONObject jobj = new JSONObject(auth);
		//checken ob pseudonym vorhanden + token format richtig + Token gültig
		//if (usertoken.containsKey(jobj.getString("pseudonym")) && isBase64(jobj.getString("token"))&& isValidToken(jobj)) {
		if(mongo.getTokenData(jobj.getString("pseudonym"))!=null && isBase64(jobj.getString("token"))&& isValidToken(jobj)) {
			//return erstellemn ohne Datanbank
			//JSONObject temp= usertoken.get(jobj.getString("pseudonym"));
			// return Response.status(200);
			JSONObject temp=mongo.getTokenData(jobj.getString("pseudonym"));
			return Response.status(Response.Status.OK).entity(toStringS(temp)).build();
		}
		else{
			return Response.status(Response.Status.UNAUTHORIZED).header("Access-Control-Allow-Origin", "*").build();
		}

	}
	@OPTIONS
	@Path("/login")
	public Response optionsReg() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}

	@OPTIONS
	@Path("/auth")
	public Response optionsProfile() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}
}
