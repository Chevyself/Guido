package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import java.util.Collection;
import java.util.regex.Pattern;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.loader.GroupLoader;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
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
        Group.class, this.groups(), new Document("id", id), group -> group.getId().equals(id));
  }

  @Nullable
  @Override
  public Group getGroupByBane(@NonNull String name) {
    return Mongo.get(
        Group.class,
        this.groups(),
        new Document("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE)),
        group -> group.getName().equalsIgnoreCase(name));
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
      Mongo.delete(this.groups(), new Document("id", id));
      return true;
    }
    return false;
  }

  @Override
  public @NonNull Collection<Group> getGroups() {
    return Mongo.getMany(Group.class, this.groups(), new Document(), null, -1, -1, (group) -> true);
  }

  @Override
  public long maxPageGroups(int size) {
    return Mongo.count(this.groups(), new Document()) / size;
  }

  @Override
  public @NonNull Collection<GroupInfo> getGroups(int page, int size) {
    return Mongo.getMany(GroupInfo.class, this.groups(), new Document(), null, page, size);
  }
}
