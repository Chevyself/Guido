import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import lombok.NonNull;
import me.googas.bot.core.loader.jsongo.JsongoLoader;
import org.bson.Document;

public class SeeDuplicates {

  public static void main(String[] args) {
    JsongoLoader loader =
        new JsongoLoader("mongodb+srv://Chevy:1004133609@googas.jbisg.mongodb.net", "Main");
    MongoCollection<Document> collection =
        loader.getClient().getDatabase("Main").getCollection("links");
    MongoCursor<Document> cursor = collection.find().cursor();
    while (cursor.hasNext()) {
      String uuid = SeeDuplicates.getUuid(cursor.next());
      if (uuid == null) continue;
      long count = collection.countDocuments(new Document("identification.uuid", uuid));
      if (count > 1) {
        System.out.println("There's " + count + " documents of " + uuid);
      }
    }
  }

  /**
   * Get the trimmed uuid from a document
   *
   * @param doc the document to get the uuid
   * @return the uuid
   */
  public static String getUuid(@NonNull Document doc) {
    if (doc.get("identification") != null) {
      return doc.get("identification", Document.class).getString("uuid");
    }
    return null;
  }
}
