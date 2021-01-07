package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;
import me.googas.api.loader.GroupLoader;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.bot.core.permissions.GuidoGroup;
import me.googas.bot.core.permissions.GuidoGroupInfo;
import me.googas.bot.core.util.Mongo;
import org.bson.Document;

public class JsongoGroupsLoader extends SimpleJsongoLoader implements GroupLoader {

  public JsongoGroupsLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  @NonNull
  public MongoCollection<Document> groups() {
    return this.getCollection("groups");
  }

  @Override
  public Group getGroup(@NonNull String id) {
    return Mongo.get(
        GuidoGroup.class, this.groups(), new Document("id", id), group -> group.getId().equals(id));
  }

  @Override
  public boolean deleteGroup(@NonNull String id) {
    Group group = this.getGroup(id);
    if (group != null) {
      try {
        group.unload(false);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return Mongo.delete(this.groups(), new Document("id", id));
    }
    return false;
  }

  @Override
  public @NonNull Collection<Group> getGroups() {
    return new ArrayList<>(
        Mongo.getMany(
            GuidoGroup.class, this.groups(), new Document(), null, -1, -1, (group) -> true));
  }

  @Override
  public long maxPageGroups(int size) {
    return Mongo.count(this.groups(), new Document()) / size;
  }

  @Override
  public @NonNull Collection<GroupInfo> getGroups(int page, int size) {
    return new ArrayList<>(
        Mongo.getMany(GuidoGroupInfo.class, this.groups(), new Document(), null, page, size));
  }
}
