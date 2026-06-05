package me.googas.bot.core.loader.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.DiscordLinkableLoader;
import me.googas.api.user.UserData;
import me.googas.bot.core.loader.mongo.types.MongoDiscordLinkable;
import me.googas.bot.core.loader.mongo.types.MongoUserData;
import net.dv8tion.jda.api.entities.User;

public class MongoDiscordLinksLoader extends SimpleMongoLoader implements DiscordLinkableLoader {

  @NonNull @Getter private final MongoLoader loader;
  @NonNull private final MongoCollection<MongoDiscordLinkable> collection;

  public MongoDiscordLinksLoader(
      @NonNull MongoLoader loader, @NonNull MongoCollection<MongoDiscordLinkable> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  private Optional<MongoDiscordLinkable> getById(long id) {
    MongoDiscordLinkable match = this.collection.find(Filters.eq("_id", id)).first();
    if (match != null) {
      match.setLoader(this);
    }
    return Optional.ofNullable(match);
  }

  @NonNull
  private MongoDiscordLinkable create(@NonNull User user) {
    MongoUserData userData = this.loader.getUsers().create();
    MongoDiscordLinkable created = new MongoDiscordLinkable(user.getIdLong(), userData.getId());
    this.collection.insertOne(created);
    created.setLoader(this);
    return created;
  }

  @Override
  public @NonNull MongoDiscordLinkable ensureByUser(@NonNull User user) {
    return this.getById(user.getIdLong()).orElseGet(() -> this.create(user));
  }

  public void setLinkedUser(
      @NonNull MongoDiscordLinkable mongoDiscordLinkable, @NonNull UserData user) {
    this.collection.updateOne(
        Filters.eq("_id", mongoDiscordLinkable.getId()), Updates.set("linkedUserId", user.getId()));
  }
}
