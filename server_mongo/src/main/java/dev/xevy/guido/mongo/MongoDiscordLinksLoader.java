package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.xevy.guido.mongo.types.MongoDiscordLinkable;
import dev.xevy.guido.mongo.types.MongoUserData;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.DiscordLinkableLoader;
import me.googas.api.user.UserData;
import net.dv8tion.jda.api.entities.User;
import org.bson.conversions.Bson;

public class MongoDiscordLinksLoader extends SimpleMongoLoader implements DiscordLinkableLoader {

  @NonNull @Getter private final MongoLoader loader;
  @NonNull private final MongoCollection<MongoDiscordLinkable.Document> collection;

  public MongoDiscordLinksLoader(
      @NonNull MongoLoader loader,
      @NonNull MongoCollection<MongoDiscordLinkable.Document> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  private Optional<MongoDiscordLinkable> getBy(@NonNull Bson bson) {
    MongoDiscordLinkable.Document doc = this.collection.find(bson).first();
    MongoDiscordLinkable match = doc == null ? null : new MongoDiscordLinkable(doc, this);
    return Optional.ofNullable(match);
  }

  @NonNull
  private Optional<MongoDiscordLinkable> getById(long id) {
    return this.getBy(Filters.eq("_id", id));
  }

  @NonNull
  private MongoDiscordLinkable create(@NonNull User user) {
    MongoUserData userData = this.loader.getUsers().create();
    MongoDiscordLinkable.Document doc = new MongoDiscordLinkable.Document();
    doc.id = user.getIdLong();
    doc.linkedUserId = userData.getId();
    this.collection.insertOne(doc);
    return new MongoDiscordLinkable(doc, this);
  }

  @Override
  public @NonNull MongoDiscordLinkable ensureByUser(@NonNull User user) {
    return this.getById(user.getIdLong()).orElseGet(() -> this.create(user));
  }

  @Override
  public @NonNull Optional<MongoDiscordLinkable> getByLinkedUser(@NonNull UUID linkedUserId) {
    return this.getBy(Filters.eq("linkedUserId", linkedUserId));
  }

  public void setLinkedUser(
      @NonNull MongoDiscordLinkable mongoDiscordLinkable, @NonNull UserData user) {
    this.collection.updateOne(
        Filters.eq("_id", mongoDiscordLinkable.getId()), Updates.set("linkedUserId", user.getId()));
  }
}
