import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;




public class MongoDB {
    MongoClientURI uri;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    /*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currentDateAndTime = dateFormat.format(Calendar.getInstance().getTime());*/


    public MongoDB(){
        uri = new MongoClientURI("mongodb+srv://DataTeam:datapassword@measurements-pdu9a.mongodb.net/test?retryWrites=true");
        mongoClient = new MongoClient(uri);
        mongoDatabase = mongoClient.getDatabase("test");

    }
   public void insertNewData(String collectionName, int temperature, int co2, int humidity){
        Document document = new Document()
                .append("Temperature", temperature)
                .append("CO2", co2)
                .append("Humidity", humidity)
                .append("GreenhouseID", 1)
                .append("Timestamp", Calendar.getInstance().getTimeInMillis());

        mongoDatabase.getCollection(collectionName).insertOne(document);
    }
}
