package var;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


/**
 * Storage provider for a MongoDB.
 */
class Database {
	private static String MONGO_URL= "mongodb://141.19.142.58:27017";
    /** URI to the MongoDB instance. */
    private static MongoClientURI connectionString = new MongoClientURI(MONGO_URL);

    /** Client to be used. */
    private static MongoClient mongoClient = new MongoClient(connectionString);

    /** Mongo database. */
    private static MongoDatabase database = mongoClient.getDatabase("users");

    /**
      @see var.chat.server.persistence.StorageProvider#retrieveAndUpdateSequence(java.lang.String)
     
    public synchronized long retrieveAndUpdateSequence(String userId) {
        MongoCollection<Document> sequences = database.getCollection(
                "sequences");

        Document seqDoc = sequences.find(eq("user", userId)).first();
        long sequence = 1L;

        if (seqDoc != null) {
            sequence = seqDoc.getLong("sequence");
            sequence++;
            seqDoc.replace("sequence", sequence);
            sequences.updateOne(eq("user", seqDoc.get("user")),
                    new Document("$set", seqDoc));
        }
        else {
            sequences.insertOne(new Document("sequence", sequence)
                    .append("user", userId));
        }

        return sequence;
    }
 */
    /**
     * @see var.chat.server.persistence.StorageProvider#storeMessage(var.chat.server.domain.Message)
     */
  /**  public synchronized void storeMessage(Message message) {
        MongoCollection<Document> collection = database.getCollection("messages");

        Document doc = new Document("from", message.from)
                .append("to", message.to)
                .append("date", message.date)
                .append("sequence", message.sequence)
                .append("text", message.text);

        collection.insertOne(doc);
    }
*/
    /**Methode um Benutzersdaten
     * @return 
     * 
     */
  
    public synchronized JSONObject getUserData (String user){
    	//
    	MongoCollection<Document> collection = database.getCollection("user");
    	Document userdata= collection.find(eq("user", user)).first();
       //keine Daten für den User vorhanden
        if (userdata==null) {
            return null;
        }
        else {
        	//userdata.append("user", user);
        	return new JSONObject(userdata.toJson());
        }
    }
    public synchronized void saveUserData(JSONObject user){
    	MongoCollection<Document> collection = database.getCollection("user");
        Document userData= new Document("user", user.get("user")).append("password", user.getString("password")).append("pseudonym", user.getString("pseudonym"));
        if (collection.find(eq("user", userData.getString("user"))).first() != null) {
			collection.updateMany(eq("user", userData.getString("user")), new Document("$set", userData));
        			   }
        else{
        	collection.insertOne(userData);
        }
        	
        }
    
    public synchronized JSONObject getTokenData (String pseudonym){
        MongoCollection<Document> collection = database.getCollection("token");
        Document tokenData= collection.find(eq("pseudonym", pseudonym)).first();
       //keine Daten für den User vorhanden
        if (tokenData==null) {
            return null;
        }
        else return new JSONObject(tokenData.toJson());

    }
    
    public synchronized void saveTokenData(JSONObject token){
    	MongoCollection<Document> collection = database.getCollection("token");
        Document tokenData= new Document("token", token.get("token")).append("expireDate", token.getString("expireDate")).append("pseudonym", token.getString("pseudonym"));
        if (collection.find(eq("pseudonym", token.getString("pseudonym"))).first() != null) {
			collection.updateMany(eq("pseudonym", token.getString("pseudonym")), new Document("$set", tokenData));
        			   }
        else{
        	collection.insertOne(tokenData);
        }
        	
        }
        

    /**
     * @see var.chat.server.persistence.StorageProvider#clearForTest()
     */
    public void clearForTest() {
        database.getCollection("messages").drop();
        database.getCollection("sequences").drop();
    }
}
