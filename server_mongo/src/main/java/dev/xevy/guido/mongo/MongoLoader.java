package dev.xevy.guido.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.xevy.guido.mongo.types.*;
import lombok.Getter;
import lombok.NonNull;
import me.googas.server.GuidoServerRuntime;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public final class MongoLoader implements me.googas.server.loader.GuidoLoader {

  @NonNull public static final String MINECRAFT_LINKS_COLLECTION_NAME = "minecraft-links";

  @NonNull @Getter private final GuidoServerRuntime runtime;
  @NonNull @Getter private final MongoClient client;
  @NonNull @Getter private final MongoDatabase database;
  @NonNull @Getter private final MongoTokenLoader tokens;
  @NonNull @Getter private final MongoUserLoader users;
  @NonNull @Getter private final MongoMinecraftMatchLoader minecraftMatches;
  @NonNull @Getter private final MongoDiscordLinksLoader discordLinks;
  @NonNull @Getter private final MongoMinecraftLinksLoader minecraftLinks;
  @NonNull @Getter private final MongoGuidoGuildLoader guidoGuildLoader;
  @NonNull @Getter private final MongoStatsLoader stats;

  private MongoLoader(
      @NonNull GuidoServerRuntime runtime,
      @NonNull MongoClient client,
      @NonNull MongoDatabase database) {
    this.runtime = runtime;
    this.client = client;
    this.database = database;
    this.tokens =
        new MongoTokenLoader(this, this.database.getCollection("tokens", MongoToken.class));
    this.users =
        new MongoUserLoader(
            this, this.database.getCollection("users", MongoUserData.Document.class));
    this.minecraftMatches =
        new MongoMinecraftMatchLoader(
            this,
            this.database.getCollection("minecraft-matches", MongoMinecraftMatch.Document.class));
    this.discordLinks =
        new MongoDiscordLinksLoader(
            this,
            this.database.getCollection("discord-links", MongoDiscordLinkable.Document.class));
    this.minecraftLinks =
        new MongoMinecraftLinksLoader(
            this,
            this.database.getCollection(
                MINECRAFT_LINKS_COLLECTION_NAME, MongoMinecraftLinkable.Document.class));
    this.guidoGuildLoader =
        new MongoGuidoGuildLoader(
            this, this.database.getCollection("guilds", MongoGuidoGuild.Document.class));
    this.stats =
        new MongoStatsLoader(this, this.database.getCollection("stats", MongoStats.Document.class));
  }

  @NonNull
  public static MongoLoader join(
      @NonNull GuidoServerRuntime runtime, @NonNull String uri, @NonNull String databaseName) {
    CodecRegistry codecRegistry =
        CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoClientSettings settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(uri))
            .codecRegistry(codecRegistry)
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .build();
    MongoClient mongoClient = MongoClients.create(settings);
    return new MongoLoader(runtime, mongoClient, mongoClient.getDatabase(databaseName));
  }

  @Override
  public void close() {
    this.client.close();
  }
}
