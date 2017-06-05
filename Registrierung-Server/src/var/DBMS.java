package var;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


public class DBMS {

	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";

	private static final String MONGO_URL = "mongodb://141.19.142.58:27017";
	//private static final String MONGO_URL = "mongodb://141.19.142.58:";

	/** URI to the MongoDB instance. */
	private static MongoClientURI connectionString = new MongoClientURI(MONGO_URL);

	/** Client to be used. */
	private static MongoClient mongoClient = new MongoClient(connectionString);

	/** Mongo database. */
	private static MongoDatabase database = mongoClient.getDatabase("users");

	/** Mongo Collection for accounts */

	/** Mongo Collection for tokens which belongs to a account */
	public synchronized JSONObject getAllUsers(String pseudonym){
    	DB database = mongoClient.getDB("users");
    	 DBCollection collection = database.getCollection("user");

    	  // To Find All the Records
    	  DBCursor cursor = collection.find();
    	  JSONObject jObj= new JSONObject();
    	  int counter=0;
    	  String[] contacts= getContacts(pseudonym);
    	  while(cursor.hasNext()) {
    	      JSONObject temp= new JSONObject(cursor.next().toString());
    	      boolean noClone=true;
    	      for(int i=0; i<contacts.length; i++){
    	    	  if(contacts[i].equals( temp.get("pseudonym"))||contacts[i]==pseudonym){
    	    		  noClone=false;}
    	      }
    	      if(noClone){
    	      jObj.put(String.valueOf(counter), temp.get("pseudonym"));
    	      counter++;
    	      }
    	     
    	  }
    	  return jObj;
    	 };

	public void addContact(String user, String contact) {
		MongoCollection<Document> contactCollection = database.getCollection("contact");
		Document checkContact = contactCollection.find(and(eq("pseudonym", user), eq("contact", contact))).first();
		if (checkContact == null) {
			Document newContact = new Document("pseudonym", user);
			newContact.append("contact", contact);
			contactCollection.insertOne(newContact);
		}
	}

	public String[] getContacts(String user) {

		MongoCollection<Document> contactCollection = database.getCollection("contact");
		FindIterable<Document> contacts = contactCollection.find(eq("pseudonym", user));
		long size = contactCollection.count(eq("pseudonym", user));
		String[] contString = new String[(int) size];
		int i = 0;
		for (Document cont : contacts) {
			contString[i] = cont.get("contact").toString();
			i++;
		}
		return contString;
	}

	public void createUser(String pseudonym, String password, String user) {
		MongoCollection<Document> accountCollection = database.getCollection("user");
		Document doc = accountCollection.find(eq("pseudonym", pseudonym)).first();

		if (doc != null) {
			throw new InvalidParameterException();
		}

		Document newDoc = new Document();
		newDoc.append("pseudonym", pseudonym);
		String userPW = "";
		try {
			userPW = SecurityHelper.hashPassword(password);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		newDoc.append("password", userPW);
		newDoc.append("user", user);
		accountCollection.insertOne(newDoc);

		MongoCollection<Document> tokenCollection = database.getCollection("token");
		Document tokenDoc = new Document();
		tokenDoc.append("pseudonym", pseudonym);
		tokenDoc.append("token", "save token");
		tokenDoc.append("expire-date", new Date());
		tokenCollection.insertOne(tokenDoc);
	}

	public boolean checkToken(String pseudonym, String token) {
		String url = "http://localhost:5001/auth";
		Client client = Client.create();
		WebResource webResource = client
		   .resource(url);
		String input = "{\"token\": \"" + token + "\",\"pseudonym\": \"" + pseudonym + "\"}";
		System.out.println(input);
		ClientResponse response = webResource.type("application/json")
		   .post(ClientResponse.class, input);
		if (response.getStatus() != 200) {
			System.out.println(response.getStatus());
			return false;
		}

		return true;
	}


	public String getEmail(String pseudonym) {
		MongoCollection<Document> accountCollection = database.getCollection("user");
		// Get Account Collection
		Document doc = accountCollection.find(eq("pseudonym", pseudonym)).first();
		return doc.getString("user");
	}

	public void clearForTest() {
		database.getCollection("user").drop();
		database.getCollection("contact").drop();
		database.getCollection("token").drop();
		System.out.println("Cleared");
	}
}
