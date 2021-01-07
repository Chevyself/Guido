package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.loader.PunishmentLoader;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.utility.Enums;
import me.googas.bot.core.punishment.GuidoPunishment;
import me.googas.bot.core.util.Mongo;
import org.bson.Document;

public class JsongoPunishmentLoader extends SimpleJsongoLoader implements PunishmentLoader {

  public JsongoPunishmentLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  @NonNull
  public MongoCollection<Document> punishments() {
    return this.getCollection("punishments");
  }

  @Override
  public @NonNull Collection<Punishment> getPunishments(
      @NonNull LinkableInfo link, @NonNull PunishmentStatus... statuses) {
    PunishmentStatus[] finalStatuses = statuses.length == 0 ? PunishmentStatus.values() : statuses;
    Document query =
        new Document(
            "punished",
            Mongo.getQuery(link.getType(), link.getIdentification())
                .append("status", new Document("$in", Enums.getNames(statuses))));
    return new ArrayList<>(
        Mongo.getMany(
            GuidoPunishment.class,
            this.punishments(),
            query,
            null,
            -1,
            -1,
            punishment ->
                Enums.contains(finalStatuses, punishment.getStatus())
                    && punishment.getPunished().compare(link)));
  }

  @Override
  public Punishment getPunishment(@NonNull String id) {
    return Mongo.get(
        GuidoPunishment.class,
        this.punishments(),
        new Document("id", id),
        punishment -> punishment.getId().equalsIgnoreCase(id));
  }
}
