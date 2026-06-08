package dev.xevy.guido.bot;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.utility.RandomUtils;
import me.googas.bot.GuidoBotConfig;

public class JGuidoConfig implements GuidoBotConfig {

  @Getter private final int port = RandomUtils.nextInt(3366, 10000);

  @NonNull
  private String getEnvOr(@NonNull String key, @NonNull String def) {
    String value = System.getenv(key);
    return value == null ? def : value;
  }

  @Override
  public @NonNull String getMongoUri() {
    return getEnvOr("JGUIDO_MONGO_URI", "mongodb://localhost:27017");
  }

  @Override
  public @NonNull String getDatabase() {
    return getEnvOr("JGUIDO_MONGO_DATABASE", "jguido-test");
  }

  @Override
  public @NonNull String getDiscordToken() {
    return getEnvOr("JGUIDO_DISCORD_TOKEN", "");
  }

  @Override
  public int getServerPort() {
    return Integer.parseInt(getEnvOr("JGUIDO_SERVER_PORT", "3366"));
  }

  @Override
  public long getTimeout() {
    return Long.parseLong(getEnvOr("JGUIDO_TIMEOUT", "10000"));
  }

  @Override
  public long getGuildId() {
    return Long.parseLong(getEnvOr("JGUIDO_GUILD_ID", "1513271141521686799"));
  }
}
