package me.googas.bot.core.loader.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.StatsLoader;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.loader.mongo.types.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public final class MongoLoader implements GuidoLoader {

  @NonNull @Getter private final GuidoBotRuntime runtime;
  @NonNull @Getter private final MongoClient client;
  @NonNull @Getter private final MongoDatabase database;
  @NonNull @Getter private final MongoTokenLoader tokens;
  @NonNull @Getter private final MongoUserLoader users;
  @NonNull @Getter private final MongoMinecraftMatchLoader minecraftMatches;
  @NonNull @Getter private final MongoDiscordLinksLoader discordLinks;
  @NonNull @Getter private final MongoMinecraftLinksLoader minecraftLinks;
  @NonNull @Getter private final MongoGuidoGuildLoader guidoGuildLoader;

  public MongoLoader(
      @NonNull GuidoBotRuntime runtime,
      @NonNull MongoClient client,
      @NonNull MongoDatabase database) {
    this.runtime = runtime;
    this.client = client;
    this.database = database;
    this.tokens =
        new MongoTokenLoader(this, this.database.getCollection("tokens", MongoToken.class));
    this.users =
        new MongoUserLoader(this, this.database.getCollection("users", MongoUserData.class));
    this.minecraftMatches =
        new MongoMinecraftMatchLoader(
            this, this.database.getCollection("minecraft-matches", MongoMinecraftMatch.class));
    this.discordLinks =
        new MongoDiscordLinksLoader(
            this, this.database.getCollection("discord-links", MongoDiscordLinkable.class));
    this.minecraftLinks =
        new MongoMinecraftLinksLoader(
            this, this.database.getCollection("minecraft-links", MongoMinecraftLink.class));
    this.guidoGuildLoader =
        new MongoGuidoGuildLoader(
            this, this.database.getCollection("guilds", MongoGuidoGuild.class));
  }

  @Override
  public @NonNull StatsLoader getStats() {
    // TODO
    throw new UnsupportedOperationException("Stats are wip");
  }

  @NonNull
  public static MongoLoader join(
      @NonNull GuidoBotRuntime runtime, @NonNull String uri, @NonNull String databaseName) {
    CodecRegistry codecRegistry =
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
    MongoClientSettings settings =
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(uri))
            .codecRegistry(codecRegistry)
            .build();
    MongoClient mongoClient = MongoClients.create(settings);
    return new MongoLoader(runtime, mongoClient, mongoClient.getDatabase(databaseName));
  }
}
