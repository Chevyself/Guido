package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import java.util.regex.Pattern;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.loader.TeamLoader;
import me.googas.api.matches.team.Team;
import me.googas.bot.core.util.Mongo;
import org.bson.Document;

public class JsongoTeamLoader extends SimpleJsongoLoader implements TeamLoader {

  public JsongoTeamLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  @NonNull
  public MongoCollection<Document> teams() {
    return this.getCollection("teams");
  }

  @Override
  public Team getTeam(@NonNull String id) {
    return Mongo.get(
        Team.class, this.teams(), new Document("id", id), team -> team.getId().equals(id));
  }

  @Override
  public Team getTeamByName(@NonNull String name) {
    return Mongo.get(
        Team.class,
        this.teams(),
        new Document("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE)),
        team -> team.getName().equalsIgnoreCase(name));
  }

  @Override
  public Team getTeam(@NonNull Linkable linkable) {
    Document query = new Document();
    linkable
        .getIdentification()
        .forEach((key, value) -> query.append("members.linkInfo.identification." + key, value));
    return Mongo.get(Team.class, this.teams(), query, team -> team.contains(linkable));
  }

  @Override
  public boolean deleteTeam(@NonNull String id) {
    Team team = this.getTeam(id);
    if (team != null) {
      try {
        team.unload(false);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return Mongo.delete(this.teams(), new Document("id", id));
    }
    return false;
  }
}
